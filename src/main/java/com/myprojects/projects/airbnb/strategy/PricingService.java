package com.myprojects.projects.airbnb.strategy;

import com.myprojects.projects.airbnb.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PricingService {

    public BigDecimal calculateDynamicPricing(Inventory inventory){

        PricingStrategy pricingStrategy = new BasePricingStrategy();

        // Apply additional pricing strategy
        pricingStrategy = new SurgePricingStrategy(pricingStrategy);

        pricingStrategy = new OccupancyPricingStrategy(pricingStrategy);

        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);

        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);

        return pricingStrategy.calculatePrice(inventory);
    }
}
