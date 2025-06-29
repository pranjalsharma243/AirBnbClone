package com.myprojects.projects.airbnb.strategy;

import com.myprojects.projects.airbnb.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class BasePricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        // Base pricing strategy: return the base price of the inventory using
        return inventory.getRoom().getBasePrice();
    }
}
