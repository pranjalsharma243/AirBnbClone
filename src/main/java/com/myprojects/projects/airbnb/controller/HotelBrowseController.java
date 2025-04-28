package com.myprojects.projects.airbnb.controller;


import com.myprojects.projects.airbnb.dto.HotelDto;
import com.myprojects.projects.airbnb.dto.HotelInfoDto;
import com.myprojects.projects.airbnb.dto.HotelSearchRequest;
import com.myprojects.projects.airbnb.repository.InventoryRepository;
import com.myprojects.projects.airbnb.service.HotelService;
import com.myprojects.projects.airbnb.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    private final HotelService hotelService;

    private final InventoryService inventoryService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelDto>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest) {

        Page<HotelDto> page=inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId) {
        HotelInfoDto hotelInfoDto = hotelService.getHotelInfoById(hotelId);
        return ResponseEntity.ok(hotelInfoDto);
    }


}
