package com.myprojects.projects.airbnb.service;

import com.myprojects.projects.airbnb.dto.*;
import com.myprojects.projects.airbnb.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface InventoryService {


    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);


    Page<HotelPriceResponseDto> searchHotels(HotelSearchRequest hotelSearchRequest);

    List<InventoryDto> getAllInventoryByRoom(Long roomId);

    void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto);
}
