package com.myprojects.projects.airbnb.dto;

import com.myprojects.projects.airbnb.entity.Guest;
import com.myprojects.projects.airbnb.entity.Hotel;
import com.myprojects.projects.airbnb.entity.Room;
import com.myprojects.projects.airbnb.entity.User;
import com.myprojects.projects.airbnb.entity.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingDto {


    private Long id;
    private User user;
    private Integer roomsCount;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BookingStatus bookingStatus;
    private Set<Guest> guests;


}
