package com.myprojects.projects.airbnb.strategy;

import com.myprojects.projects.airbnb.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OccupancyPricingStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;
    public OccupancyPricingStrategy(PricingStrategy wrapped) {
        this.wrapped = wrapped;
    }



    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        double occupancyRate = inventory.getBookedCount() / inventory.getTotalCount();
        if(occupancyRate> 0.8){
            price=price.multiply(BigDecimal.valueOf(1.2));
        }
        return price;
    }
}
