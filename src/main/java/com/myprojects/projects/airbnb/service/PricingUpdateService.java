package com.myprojects.projects.airbnb.service;

import com.myprojects.projects.airbnb.entity.Hotel;
import com.myprojects.projects.airbnb.entity.HotelMinPrice;
import com.myprojects.projects.airbnb.entity.Inventory;
import com.myprojects.projects.airbnb.repository.HotelMinPriceRepository;
import com.myprojects.projects.airbnb.repository.HotelRepository;
import com.myprojects.projects.airbnb.repository.InventoryRepository;
import com.myprojects.projects.airbnb.strategy.PricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PricingUpdateService {

    private final HotelRepository hotelRepository;

    private final InventoryRepository inventoryRepository;

    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final PricingService pricingService;

    //scheduler to update inventory and hotelMinPrice tables every hour
    @Scheduled(cron = "0 0 * * * *")
    public void updatePrices(){

        int page = 0;
        int batchSize = 100;
        while(true){
            Page<Hotel> hotelPage = hotelRepository.findAll(PageRequest.of(page, batchSize));
            if(hotelPage.isEmpty()){
                break;
            }
            hotelPage.getContent().forEach(this::updateHotelPrices);
            page++;
        }

    }

    private void updateHotelPrices(Hotel hotel) {
        log.info("Updating prices for hotel: {}", hotel.getId());
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);
        List<Inventory> inventoryList = inventoryRepository.findByHotelAndDateBetween(hotel, startDate, endDate);
        updateInventoryPrices(inventoryList);
        updateHotelMinPrice(hotel, inventoryList,startDate, endDate);


    }

    private void updateHotelMinPrice(Hotel hotel, List<Inventory> inventoryList, LocalDate startDate, LocalDate endDate) {

        Map<LocalDate, BigDecimal> dailyMinPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(
                       Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice, Collectors.minBy(Comparator.naturalOrder()))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().orElse(BigDecimal.ZERO)
                ));
        List<HotelMinPrice> hotelPrices =new ArrayList<>();
        dailyMinPrices.forEach((date, price) -> {
            HotelMinPrice hotelPrice = hotelMinPriceRepository.findByHotelAndDate(hotel, date)
                    .orElse(new HotelMinPrice(hotel, date));
            hotelPrice.setPrice(price);
            hotelPrices.add(hotelPrice);
        });
        hotelMinPriceRepository.saveAll(hotelPrices);
    }

    private void updateInventoryPrices(List<Inventory> inventoryList) {
        inventoryList.forEach(inventory -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);
        });
       inventoryRepository.saveAll(inventoryList);
    }


}
