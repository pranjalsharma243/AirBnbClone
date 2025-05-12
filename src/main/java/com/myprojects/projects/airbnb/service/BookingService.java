package com.myprojects.projects.airbnb.service;

import com.myprojects.projects.airbnb.dto.BookingDto;
import com.myprojects.projects.airbnb.dto.BookingRequest;

public interface BookingService {


    BookingDto initializeBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, java.util.List<com.myprojects.projects.airbnb.dto.GuestDto> guestDtoList);
}
