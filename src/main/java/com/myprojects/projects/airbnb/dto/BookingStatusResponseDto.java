package com.myprojects.projects.airbnb.dto;

import com.myprojects.projects.airbnb.entity.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatusResponseDto {
    private BookingStatus bookingStatus;
}
