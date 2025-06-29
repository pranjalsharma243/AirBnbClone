package com.myprojects.projects.airbnb.strategy;

import com.myprojects.projects.airbnb.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class SurgePricingStrategy implements PricingStrategy{



    private final PricingStrategy wrapped;


    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        return price.multiply(inventory.getSurgeFactor());
    }
}
