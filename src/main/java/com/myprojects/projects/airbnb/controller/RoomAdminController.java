package com.myprojects.projects.airbnb.controller;

import com.myprojects.projects.airbnb.dto.RoomDto;
import com.myprojects.projects.airbnb.entity.Hotel;
import com.myprojects.projects.airbnb.exception.ResourceNotFoundException;
import com.myprojects.projects.airbnb.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
public class RoomAdminController {

    private final RoomService roomService;

    private void verifyRoomInHotel(Long hotelId, Long roomId) {
        boolean exists = roomService.getAllRoomsInHotel(hotelId)
                .stream()
                .anyMatch(room -> room.getId().equals(roomId));
        if (!exists) {
            throw new ResourceNotFoundException("Room not found with ID: " + roomId + " in hotel with ID: " + hotelId);
        }
    }

    @Operation(summary = "Create a new room in a hotel",
            description = "Creates a new room under the specified hotel ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Room created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomDto.class))),
            @ApiResponse(responseCode = "404", description = "Hotel not found", content = @Content)
    })
    @PostMapping
    public ResponseEntity<RoomDto> createNewRoom(
            @Parameter(description = "Room details") @RequestBody RoomDto roomDto,
            @Parameter(description = "Hotel ID") @PathVariable Long hotelId) {
        RoomDto room = roomService.createNewRoom(hotelId, roomDto);
        return new ResponseEntity<>(room, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all rooms in a hotel",
            description = "Fetches all rooms associated with the specified hotel ID.")
    @ApiResponse(responseCode = "200", description = "List of rooms",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomDto.class)))
    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRoomsInHotel(
            @Parameter(description = "Hotel ID") @PathVariable Long hotelId) {
        return new ResponseEntity<>(roomService.getAllRoomsInHotel(hotelId), HttpStatus.OK);
    }

    @Operation(summary = "Get a specific room by ID in a hotel",
            description = "Retrieves details of a specific room under the given hotel.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room details",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomDto.class))),
            @ApiResponse(responseCode = "404", description = "Room not found in hotel", content = @Content)
    })
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomById(
            @Parameter(description = "Hotel ID") @PathVariable Long hotelId,
            @Parameter(description = "Room ID") @PathVariable Long roomId) {
        verifyRoomInHotel(hotelId, roomId);
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @Operation(summary = "Delete a room by ID from a hotel",
            description = "Deletes a room by its ID under the given hotel.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Room deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Room not found in hotel", content = @Content)
    })
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoomById(
            @Parameter(description = "Hotel ID") @PathVariable Long hotelId,
            @Parameter(description = "Room ID") @PathVariable Long roomId) {
        verifyRoomInHotel(hotelId, roomId);
        roomService.deleteRoomById(roomId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a room by ID in a hotel",
            description = "Updates the details of a room under the specified hotel.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomDto.class))),
            @ApiResponse(responseCode = "404", description = "Room not found in hotel", content = @Content)
    })
    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDto> updateRoomById(
            @Parameter(description = "Hotel ID") @PathVariable Long hotelId,
            @Parameter(description = "Room ID") @PathVariable Long roomId,
            @Parameter(description = "Updated room details") @RequestBody RoomDto roomDto) {
        verifyRoomInHotel(hotelId, roomId);
        RoomDto updatedRoom = roomService.updateRoomById(hotelId, roomId, roomDto);
        return ResponseEntity.ok(updatedRoom);
    }
}