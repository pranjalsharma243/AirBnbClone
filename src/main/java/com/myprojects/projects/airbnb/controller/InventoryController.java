package com.myprojects.projects.airbnb.controller;

import com.myprojects.projects.airbnb.dto.InventoryDto;
import com.myprojects.projects.airbnb.dto.UpdateInventoryRequestDto;
import com.myprojects.projects.airbnb.service.InventoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "bearerAuth")
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Get all inventory entries for a specific room",
            description = "Returns a list of inventory records related to the specified room ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of inventory entries",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventoryDto.class))),
            @ApiResponse(responseCode = "404", description = "Room not found", content = @Content)
    })
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<InventoryDto>> getAllInventoryByRoom(
            @Parameter(description = "ID of the room to get inventory for") @PathVariable Long roomId) {
        return ResponseEntity.ok(inventoryService.getAllInventoryByRoom(roomId));
    }

    @Operation(summary = "Update inventory for a specific room",
            description = "Updates the inventory details for the given room based on the provided request body.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Inventory updated successfully"),
            @ApiResponse(responseCode = "404", description = "Room not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PatchMapping("/rooms/{roomId}")
    public ResponseEntity<Void> updateInventory(
            @Parameter(description = "ID of the room to update inventory") @PathVariable Long roomId,
            @Parameter(description = "Inventory update request body") @RequestBody UpdateInventoryRequestDto updateInventoryRequestDto) {
        inventoryService.updateInventory(roomId, updateInventoryRequestDto);
        return ResponseEntity.noContent().build();
    }
}