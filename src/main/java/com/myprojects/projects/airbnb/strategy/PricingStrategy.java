package com.myprojects.projects.airbnb.strategy;

import com.myprojects.projects.airbnb.entity.Inventory;


import java.math.BigDecimal;


public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);

}
