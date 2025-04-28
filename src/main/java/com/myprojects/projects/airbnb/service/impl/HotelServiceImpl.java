package com.myprojects.projects.airbnb.service.impl;

import com.myprojects.projects.airbnb.dto.HotelDto;
import com.myprojects.projects.airbnb.dto.HotelInfoDto;
import com.myprojects.projects.airbnb.dto.RoomDto;
import com.myprojects.projects.airbnb.entity.Hotel;
import com.myprojects.projects.airbnb.entity.Room;
import com.myprojects.projects.airbnb.exception.ResourceNotFoundException;
import com.myprojects.projects.airbnb.repository.HotelRepository;
import com.myprojects.projects.airbnb.repository.RoomRepository;
import com.myprojects.projects.airbnb.service.HotelService;
import com.myprojects.projects.airbnb.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {


    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating new hotel: {}", hotelDto.getName());
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);
        Hotel savedhotel = hotelRepository.save(hotel);
        log.info("Created a new Hotel with ID: {}", savedhotel.getId());
        return modelMapper.map(savedhotel, HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Getting the hotel with ID: {}", id);
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID:" + id));
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("Updating the hotel with ID: {}", id);
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID:" + id));
        modelMapper.map(hotelDto, hotel);
        hotel.setId(id);
        hotel = hotelRepository.save(hotel);
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID:" + id));

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
        //Only once
        for (Room room : hotel.getRooms()) {
            inventoryService.initializeRoomForAYear(room);
        }

    }

    @Override
    public List<HotelDto> getAllHotels() {
        log.info("Getting all hotels");
        List<Hotel> hotel = hotelRepository.findAll();
        return hotel.stream().map((receivedHotel) -> modelMapper.map(receivedHotel, HotelDto.class)).collect(Collectors.toList());

    }

    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {
        Hotel hotel=hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+hotelId));

        List<RoomDto> rooms= hotel.getRooms()
                            .stream()
                            .map((room) -> modelMapper.map(room, RoomDto.class))
                            .collect(Collectors.toList());

        return new HotelInfoDto(modelMapper.map(hotel, HotelDto.class), rooms);


    }


}
