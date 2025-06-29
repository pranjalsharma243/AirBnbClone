package com.myprojects.projects.airbnb.service;

import com.myprojects.projects.airbnb.dto.BookingDto;
import com.myprojects.projects.airbnb.dto.BookingRequest;
import com.myprojects.projects.airbnb.dto.GuestDto;
import java.util.List;

public interface BookingService {


    BookingDto initializeBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);
}
