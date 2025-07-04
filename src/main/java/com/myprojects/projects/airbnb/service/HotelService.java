package com.myprojects.projects.airbnb.service;

import com.myprojects.projects.airbnb.dto.HotelDto;
import com.myprojects.projects.airbnb.dto.HotelInfoDto;
import com.myprojects.projects.airbnb.dto.HotelInfoRequestDto;

import java.util.List;

public interface HotelService {


    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long id);

    HotelDto updateHotelById(Long id,HotelDto hotelDto);

    void deleteHotelById(Long id);

    void activateHotel(Long id);

    List<HotelDto> getAllHotels();


    HotelInfoDto getHotelInfoById(Long hotelId, HotelInfoRequestDto hotelInfoRequestDto);
}
