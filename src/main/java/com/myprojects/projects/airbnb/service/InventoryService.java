package com.myprojects.projects.airbnb.service;

import com.myprojects.projects.airbnb.dto.HotelDto;
import com.myprojects.projects.airbnb.dto.HotelSearchRequest;
import com.myprojects.projects.airbnb.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {


    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);


    Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
