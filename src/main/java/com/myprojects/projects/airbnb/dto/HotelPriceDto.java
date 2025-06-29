package com.myprojects.projects.airbnb.dto;

import com.myprojects.projects.airbnb.entity.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelPriceDto {

    private Hotel hotel;

    private double price;
}
