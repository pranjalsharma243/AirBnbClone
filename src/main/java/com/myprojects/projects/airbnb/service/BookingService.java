package com.myprojects.projects.airbnb.service;

import com.myprojects.projects.airbnb.dto.BookingDto;
import com.myprojects.projects.airbnb.dto.BookingRequest;
import com.myprojects.projects.airbnb.dto.GuestDto;
import com.myprojects.projects.airbnb.dto.HotelReportDto;
import com.myprojects.projects.airbnb.entity.enums.BookingStatus;
import com.stripe.model.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BookingService {


    BookingDto initializeBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);

    String initiatePayments(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    BookingStatus getBookingStatus(Long bookingId);

    List<BookingDto> getAllBookingsByHotelId(Long hotelId);

    HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);

    List<BookingDto> getMyBookings();
}
