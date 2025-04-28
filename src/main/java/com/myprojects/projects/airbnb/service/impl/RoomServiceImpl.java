package com.myprojects.projects.airbnb.service.impl;

import com.myprojects.projects.airbnb.dto.RoomDto;
import com.myprojects.projects.airbnb.entity.Hotel;
import com.myprojects.projects.airbnb.entity.Room;
import com.myprojects.projects.airbnb.exception.ResourceNotFoundException;
import com.myprojects.projects.airbnb.repository.HotelRepository;
import com.myprojects.projects.airbnb.repository.RoomRepository;
import com.myprojects.projects.airbnb.service.InventoryService;
import com.myprojects.projects.airbnb.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;



    @Override
    public RoomDto createNewRoom(Long hotelId,RoomDto roomDto) {


        log.info("Creating a new room in a hotel with ID: {}",hotelId);
        Hotel hotel=hotelRepository.findById(hotelId).orElseThrow(()->new RuntimeException("Hotel not found with ID:"+hotelId));
        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        room=roomRepository.save(room);
        if(hotel.getActive()){
            inventoryService.initializeRoomForAYear(room);
        }
        return modelMapper.map(room,RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all the rooms in a hotel with ID:{}",hotelId);
        Hotel hotel=hotelRepository.findById(hotelId).orElseThrow(()->new RuntimeException("Hotel not found with ID:"+hotelId));

        return hotel.getRooms()
                .stream()
                .map((element)-> modelMapper.map(element,RoomDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting the room in a hotel with ID:{}",roomId);

        Room room=roomRepository.findById(roomId).orElseThrow(()->new RuntimeException("Room not found with ID:"+roomId));
        return modelMapper.map(room,RoomDto.class);
    }
    @Transactional
    @Override
    public void deleteRoomById(Long roomId) {
        log.info("Deleting the room with ID: {}", roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: "+roomId));
        inventoryService.deleteAllInventories(room);
        roomRepository.deleteById(roomId);
    }

}
