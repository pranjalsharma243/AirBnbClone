package com.myprojects.projects.airbnb.controller;

import com.myprojects.projects.airbnb.dto.HotelDto;
import com.myprojects.projects.airbnb.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
public class HotelController {



    private static final Logger logg= LoggerFactory.getLogger(HotelController.class);

    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<HotelDto> create(@RequestBody HotelDto hotelDto) {


        HotelDto hotel=hotelService.createNewHotel(hotelDto);
        return new ResponseEntity<>(hotel, HttpStatus.CREATED);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId) {

        HotelDto hotelDto=hotelService.getHotelById(hotelId);
        return new ResponseEntity<>(hotelDto, HttpStatus.OK);
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable Long hotelId,@RequestBody HotelDto hotelDto) {

        HotelDto hotel=hotelService.updateHotelById(hotelId,hotelDto);
        return new ResponseEntity<>(hotel, HttpStatus.OK);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotelById(@PathVariable Long hotelId) {
        hotelService.deleteHotelById(hotelId);
        return ResponseEntity.noContent().build();
    }





}
