package com.myprojects.projects.airbnb.controller;


import com.myprojects.projects.airbnb.dto.*;

import com.myprojects.projects.airbnb.service.HotelService;
import com.myprojects.projects.airbnb.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    private final HotelService hotelService;
    private final InventoryService inventoryService;

    @Operation(
            summary = "Search hotels by city and availability",
            description = "Returns a paginated list of hotels in the given city, available between the specified dates with the requested number of rooms."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of hotels found",
                    content = @Content(schema = @Schema(implementation = HotelPriceResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters", content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceResponseDto>> searchHotels(
            @RequestParam String city,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam Integer roomCount,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        HotelSearchRequest hotelSearchRequest = new HotelSearchRequest();
        hotelSearchRequest.setCity(city);
        hotelSearchRequest.setStartDate(startDate);
        hotelSearchRequest.setEndDate(endDate);
        hotelSearchRequest.setRoomCount(roomCount);
        hotelSearchRequest.setPage(page);
        hotelSearchRequest.setSize(size);

        var pageResponse = inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(pageResponse);
    }

    @Operation(
            summary = "Get detailed hotel information",
            description = "Fetches full hotel information including rooms, amenities, photos, and availability for the requested dates."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel info retrieved",
                    content = @Content(schema = @Schema(implementation = HotelInfoDto.class))),
            @ApiResponse(responseCode = "404", description = "Hotel not found", content = @Content)
    })
    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(
            @PathVariable Long hotelId,
            @RequestBody HotelInfoRequestDto hotelInfoRequestDto) {
        HotelInfoDto hotelInfoDto = hotelService.getHotelInfoById(hotelId, hotelInfoRequestDto);
        return ResponseEntity.ok(hotelInfoDto);
    }
}
