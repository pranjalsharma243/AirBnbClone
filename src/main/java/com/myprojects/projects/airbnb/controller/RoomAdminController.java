package com.myprojects.projects.airbnb.controller;

import com.myprojects.projects.airbnb.dto.RoomDto;
import com.myprojects.projects.airbnb.entity.Hotel;
import com.myprojects.projects.airbnb.exception.ResourceNotFoundException;
import com.myprojects.projects.airbnb.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
public class RoomAdminController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDto> createNewRoom(@RequestBody RoomDto roomDto,@PathVariable Long hotelId) {

       RoomDto room= roomService.createNewRoom(hotelId,roomDto);
        return new ResponseEntity<>(room, HttpStatus.CREATED);

    }


    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRoomsInHotel(@PathVariable Long hotelId) {

        return new ResponseEntity<>(roomService.getAllRoomsInHotel(hotelId), HttpStatus.OK);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomByid(@PathVariable Long hotelId, @PathVariable Long roomId) {
        // Check if the room belongs to the hotel
        if (!roomService.getAllRoomsInHotel(hotelId).stream().anyMatch(room -> room.getId().equals(roomId))) {
            throw new ResourceNotFoundException("Room not found with ID: " + roomId);
        }
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoomById(@PathVariable Long hotelId, @PathVariable Long roomId) {
        roomService.deleteRoomById(roomId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDto> updateRoomById(@PathVariable Long hotelId, @PathVariable Long roomId,
                                                  @RequestBody RoomDto roomDto) {
        if (!roomService.getAllRoomsInHotel(hotelId).stream().anyMatch(room -> room.getId().equals(roomId))) {
            throw new ResourceNotFoundException("Room not found with ID: " + roomId);
        }
        return ResponseEntity.ok(roomService.updateRoomById(hotelId,roomId,roomDto));

    }

}
