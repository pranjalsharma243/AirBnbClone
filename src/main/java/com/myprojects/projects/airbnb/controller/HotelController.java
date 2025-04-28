package com.myprojects.projects.airbnb.controller;

import com.myprojects.projects.airbnb.dto.HotelDto;
import com.myprojects.projects.airbnb.exception.ResourceNotFoundException;
import com.myprojects.projects.airbnb.repository.HotelRepository;
import com.myprojects.projects.airbnb.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
public class HotelController {


    private static final Logger log= LoggerFactory.getLogger(HotelController.class);

    private final HotelService hotelService;
    private final HotelRepository hotelRepository;

    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(@RequestBody HotelDto hotelDto) {


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
        boolean exists=hotelRepository.existsById(hotelId);
        if(!exists) throw new ResourceNotFoundException("Hotel not found with ID: "+hotelId);
        hotelService.deleteHotelById(hotelId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{hotelId}")
    public ResponseEntity<Void> activateHotel(@PathVariable Long hotelId) {
        hotelService.activateHotel(hotelId);
        return ResponseEntity.noContent().build();

    }

    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels() {
        List<HotelDto> hotelDtos=hotelService.getAllHotels();
        return new ResponseEntity<>(hotelDtos, HttpStatus.OK);

    }

}
