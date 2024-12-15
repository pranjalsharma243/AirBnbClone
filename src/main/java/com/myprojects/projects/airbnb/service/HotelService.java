package com.myprojects.projects.airbnb.service;

import com.myprojects.projects.airbnb.dto.HotelDto;

public interface HotelService {


    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long id);

    HotelDto updateHotelById(Long id,HotelDto hotelDto);

    void deleteHotelById(Long id);






}
