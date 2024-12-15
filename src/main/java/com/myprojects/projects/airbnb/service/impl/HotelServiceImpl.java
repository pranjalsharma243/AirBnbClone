package com.myprojects.projects.airbnb.service.impl;

import com.myprojects.projects.airbnb.dto.HotelDto;
import com.myprojects.projects.airbnb.entity.Hotel;
import com.myprojects.projects.airbnb.exception.ResourceNotFoundException;
import com.myprojects.projects.airbnb.repository.HotelRepository;
import com.myprojects.projects.airbnb.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.nio.file.ReadOnlyFileSystemException;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {


    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating new hotel: {}", hotelDto.getName());
        Hotel hotel=modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);
        Hotel savedhotel=hotelRepository.save(hotel);
        log.info("Created a new Hotel with ID: {}", savedhotel.getId());
        return modelMapper.map(savedhotel,HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Getting the hotel with ID: {}", id);
        Hotel hotel=hotelRepository.findById(id).orElseThrow(()->new RuntimeException("Hotelnot found with ID:"+id));
        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("Updating the hotel with ID: {}", id);
        Hotel hotel=hotelRepository.findById(id).orElseThrow(()->new RuntimeException("Hotelnot found with ID:"+id));
        modelMapper.map(hotelDto,hotel);
        hotel.setId(id);
       hotel=hotelRepository.save(hotel);
       return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    public void deleteHotelById(Long id) {
        boolean exists=hotelRepository.existsById(id);
        if(!exists) throw new ResourceNotFoundException("Hotel Not Found with ID:"+id);
        hotelRepository.deleteById(id);
        //TODO:delete the future inventories for this hotel
    }


}
