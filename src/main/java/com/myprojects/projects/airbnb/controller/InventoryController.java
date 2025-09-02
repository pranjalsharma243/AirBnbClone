package com.myprojects.projects.airbnb.controller;

import com.myprojects.projects.airbnb.dto.InventoryDto;
import com.myprojects.projects.airbnb.dto.UpdateInventoryRequestDto;
import com.myprojects.projects.airbnb.service.InventoryService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;


    @GetMapping("/hotels/{hotelId}rooms/{roomId}")
    public ResponseEntity<List<InventoryDto>> getAllInventoryByRoom(
             @PathVariable Long hotelId, @PathVariable Long roomId) {
        return ResponseEntity.ok(inventoryService.getAllInventoryByRoom(hotelId,roomId));
    }

    @Operation(summary = "Update inventory for a specific room",
            description = "Updates the inventory details for the given room based on the provided request body.")
    @PatchMapping("/rooms/{roomId}")
    public ResponseEntity<Void> updateInventory(
            @Parameter(description = "ID of the room to update inventory") @PathVariable Long roomId,
            @Parameter(description = "Inventory update request body") @RequestBody UpdateInventoryRequestDto updateInventoryRequestDto) {
        inventoryService.updateInventory(roomId, updateInventoryRequestDto);
        return ResponseEntity.noContent().build();
    }
}