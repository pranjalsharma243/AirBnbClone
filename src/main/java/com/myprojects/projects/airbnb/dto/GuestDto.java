package com.myprojects.projects.airbnb.dto;

import com.myprojects.projects.airbnb.entity.Booking;
import com.myprojects.projects.airbnb.entity.User;
import com.myprojects.projects.airbnb.entity.enums.Gender;
import lombok.Data;

import java.util.Set;

@Data
public class GuestDto {
    private Long id;


    private User user;

    private String name;


    private Gender gender;

    private Integer age;

    private Set<Booking> bookings;
}
