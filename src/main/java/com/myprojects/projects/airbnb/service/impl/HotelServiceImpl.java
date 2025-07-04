package com.myprojects.projects.airbnb.service.impl;

import com.myprojects.projects.airbnb.dto.*;
import com.myprojects.projects.airbnb.entity.Hotel;
import com.myprojects.projects.airbnb.entity.Room;
import com.myprojects.projects.airbnb.entity.User;
import com.myprojects.projects.airbnb.exception.ResourceNotFoundException;
import com.myprojects.projects.airbnb.exception.UnAuthorizedException;
import com.myprojects.projects.airbnb.repository.HotelRepository;
import com.myprojects.projects.airbnb.repository.InventoryRepository;
import com.myprojects.projects.airbnb.repository.RoomRepository;
import com.myprojects.projects.airbnb.service.HotelService;
import com.myprojects.projects.airbnb.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.myprojects.projects.airbnb.util.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {


    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating new hotel: {}", hotelDto.getName());
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hotel.setOwner(user);
        Hotel savedhotel = hotelRepository.save(hotel);
        log.info("Created a new Hotel with ID: {}", savedhotel.getId());
        return modelMapper.map(savedhotel, HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Getting the hotel with ID: {}", id);
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID:" + id));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            log.error("Unauthorized access attempt by user: {}", user.getId());
            throw new UnAuthorizedException("This User does not own this hotel with ID: " + id);
        }
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("Updating the hotel with ID: {}", id);
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID:" + id));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            log.error("Unauthorized access attempt by user: {}", user.getId());
            throw new UnAuthorizedException("This User does not own this hotel with ID: " + id);
        }
        modelMapper.map(hotelDto, hotel);
        hotel.setId(id);
        hotel = hotelRepository.save(hotel);
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID:" + id));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            log.error("Unauthorized access attempt by user: {}", user.getId());
            throw new UnAuthorizedException("This User does not own this hotel with ID: " + id);
        }
        for (Room room : hotel.getRooms()) {
            inventoryService.deleteAllInventories(room);
            roomRepository.deleteById(room.getId());
        }
        hotelRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void activateHotel(Long id) {
        log.info("Activating the hotel with ID: {}", id);
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new RuntimeException("Hotel not found with ID:" + id));
        hotel.setActive(true);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            log.error("Unauthorized access attempt by user: {}", user.getId());
            throw new UnAuthorizedException("This User does not own this hotel with ID: " + id);
        }
        //Only once

        for (Room room : hotel.getRooms()) {
            inventoryService.initializeRoomForAYear(room);
        }

    }

    @Override
    public List<HotelDto> getAllHotels() {
        User user = getCurrentUser();
        log.info("Getting all hotels for the admin user with ID: {}" , user.getId());
        List<Hotel> hotel = hotelRepository.findByOwner(user);
        return hotel.stream().map((receivedHotel) -> modelMapper.map(receivedHotel, HotelDto.class)).collect(Collectors.toList());

    }
    //public method to get hotel info by ID, including rooms
    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId, HotelInfoRequestDto hotelInfoRequestDto) {
        Hotel hotel=hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+hotelId));
        long daysCount = ChronoUnit.DAYS.between(hotelInfoRequestDto.getStartDate(), hotelInfoRequestDto.getEndDate())+1;

        List<RoomPriceDto> roomPriceDtoList = inventoryRepository.findRoomAveragePrice(hotelId,
                hotelInfoRequestDto.getStartDate(), hotelInfoRequestDto.getEndDate(),
                hotelInfoRequestDto.getRoomsCount(), daysCount);

        List<RoomPriceResponseDto> rooms = roomPriceDtoList.stream()
                .map(roomPriceDto -> {
                    RoomPriceResponseDto roomPriceResponseDto = modelMapper.map(roomPriceDto.getRoom(),
                            RoomPriceResponseDto.class);
                    roomPriceResponseDto.setPrice(roomPriceDto.getPrice());
                    return roomPriceResponseDto;
                })
                .collect(Collectors.toList());

        return new HotelInfoDto(modelMapper.map(hotel, HotelDto.class), rooms);





    }


}
