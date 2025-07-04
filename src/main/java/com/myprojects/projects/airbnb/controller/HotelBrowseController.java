package com.myprojects.projects.airbnb.controller;


import com.myprojects.projects.airbnb.dto.*;
import com.myprojects.projects.airbnb.repository.InventoryRepository;
import com.myprojects.projects.airbnb.service.HotelService;
import com.myprojects.projects.airbnb.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    private final HotelService hotelService;

    private final InventoryService inventoryService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceResponseDto>> searchHotels(@RequestParam String city,
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
        var pageResponse=inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId) {
        HotelInfoDto hotelInfoDto = hotelService.getHotelInfoById(hotelId);
        return ResponseEntity.ok(hotelInfoDto);
    }


}
