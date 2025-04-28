package com.myprojects.projects.airbnb.service.impl;

import com.myprojects.projects.airbnb.dto.BookingDto;
import com.myprojects.projects.airbnb.dto.BookingRequest;
import com.myprojects.projects.airbnb.dto.GuestDto;
import com.myprojects.projects.airbnb.entity.*;
import com.myprojects.projects.airbnb.entity.enums.BookingStatus;
import com.myprojects.projects.airbnb.exception.ResourceNotFoundException;
import com.myprojects.projects.airbnb.repository.BookingRepository;
import com.myprojects.projects.airbnb.repository.HotelRepository;
import com.myprojects.projects.airbnb.repository.InventoryRepository;
import com.myprojects.projects.airbnb.repository.RoomRepository;
import com.myprojects.projects.airbnb.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.http11.filters.GzipOutputFilter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public BookingDto initializeBooking(BookingRequest bookingRequest) {
        log.info("Initializing booking for hotel ID: {} and room ID: {}", bookingRequest.getHotelId(), bookingRequest.getRoomId());

        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId()).orElseThrow(() -> new ResourceNotFoundException("Hotel not found :" + bookingRequest.getHotelId()));

        Room room = roomRepository.findById(bookingRequest.getRoomId()).orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + bookingRequest.getRoomId()));

        List<Inventory> inventoryList =inventoryRepository.findAndLockAvailableInventory(room.getId(), bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(),bookingRequest.getRoomsCount());

        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate())+1;

        if (inventoryList.size() != daysCount) {
            log.error("No available inventory found for room ID: {} and hotel ID: {}", bookingRequest.getRoomId(), bookingRequest.getHotelId());
            throw new IllegalStateException("Room is not available anymore with room Id" + bookingRequest.getRoomId() + " and hotel ID: " + bookingRequest.getHotelId());
        }
        // Reserve the room and update the bookedCount of the inventory
        for (Inventory inventory : inventoryList) {
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
        }
        inventoryRepository.saveAll(inventoryList);
        // Create the booking


        //TODO: Dynamic Pricing
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(BigDecimal.TEN)
                .build();

        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {
        log.info("Adding guests to booking ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if(hasBookingExpired(booking)) {
            log.error("Booking with ID: {} has expired", bookingId);
            throw new IllegalStateException("Booking has expired");
        }
        if (booking.getBookingStatus() != BookingStatus.RESERVED) {
            log.error("Cannot add guests to booking with ID: {}. Current status: {}", bookingId, booking.getBookingStatus());
            throw new IllegalStateException("Cannot add guests to booking with ID, its not under RESERVED State: " + bookingId);
        }
        for(GuestDto guestDto : guestDtoList) {
            Guest guest = modelMapper.map(guestDto, Guest.class);
            guest.setUser(getCurrentUser());
            booking.getGuests().add(guest);
        }
        // Save the updated booking
        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking= bookingRepository.save(booking);
        log.info("Guests added to booking ID: {}", bookingId);
        return modelMapper.map(booking, BookingDto.class);
    }

    public boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt()
                .plusMinutes(10)
                .isBefore(LocalDateTime.now());
    }
    public User getCurrentUser(){
        User user = new User();
        user.setId(1L); // Temporarily set user ID to 1

        return user;
    }
}
