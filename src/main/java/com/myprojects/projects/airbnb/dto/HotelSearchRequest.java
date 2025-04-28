package com.myprojects.projects.airbnb.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HotelSearchRequest {
    //we need all the hotels which have atleast one room type bewtween startDate and endDate

    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer roomCount;
    private Integer page=0;
    private Integer size=10;

}
