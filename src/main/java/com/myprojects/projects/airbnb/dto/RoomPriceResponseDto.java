package com.myprojects.projects.airbnb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomPriceResponseDto {

    private Long id;

    private String type;

    private String[] photos;

    private String[] amenities;

    private Double price;




}
