package com.myprojects.projects.airbnb.service.impl;

import com.myprojects.projects.airbnb.dto.HotelDto;
import com.myprojects.projects.airbnb.dto.HotelPriceDto;
import com.myprojects.projects.airbnb.dto.HotelSearchRequest;
import com.myprojects.projects.airbnb.entity.Hotel;
import com.myprojects.projects.airbnb.entity.Inventory;
import com.myprojects.projects.airbnb.entity.Room;
import com.myprojects.projects.airbnb.repository.HotelMinPriceRepository;
import com.myprojects.projects.airbnb.repository.InventoryRepository;
import com.myprojects.projects.airbnb.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    private final ModelMapper modelMapper;

    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;

    @Override
    public void initializeRoomForAYear(Room room){
        LocalDate today=LocalDate.now();
        LocalDate endDate=today.plusYears(1);

        for(;!today.isAfter(endDate); today=today.plusDays(1)) {
            Inventory inventory = Inventory.builder()
                            .hotel(room.getHotel())
                            .room(room)
                            .bookedCount(0)
                            .reservedCount(0)
                            .city(room.getHotel().getCity())
                            .date(today)
                            .price(room.getBasePrice())
                            .surgeFactor(BigDecimal.ONE)
                            .totalCount(room.getTotalCount())
                            .closed(false)
                            .build();
            inventoryRepository.save(inventory);
        }
    }
    @Override
    public void deleteAllInventories(Room room) {
        log.info("Deleting all inventories for room: {}", room.getId());
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest) {

        //criteria for inventory :
        //startDate<=date<=endDate
        //city should be one I am filtering for
        //closed=false
        //availability: (totalCount-bookedCount)>=roomsCount
        //group the response on the basis of room type as well
        //get response by unique hotel
        log.info("Searching for hotels in city: {} with start date: {} and end date: {}",
                hotelSearchRequest.getCity(), hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate());
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());
        long dateCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate())+1;
        // 90 days
        Page<HotelPriceDto> hotelPage=hotelMinPriceRepository.findHotelsWithAvailableInventory(hotelSearchRequest.getCity(),hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),hotelSearchRequest.getRoomCount(),dateCount,pageable);


        return hotelPage;
    }
}
