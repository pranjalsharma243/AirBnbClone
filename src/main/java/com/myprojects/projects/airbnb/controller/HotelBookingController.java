package com.myprojects.projects.airbnb.controller;

import com.myprojects.projects.airbnb.dto.*;
import com.myprojects.projects.airbnb.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {

    private final BookingService bookingService;

    @Operation(
            summary = "Initialize a hotel booking",
            description = "Creates a new booking with the provided booking request."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking initialized successfully",
                    content = @Content(schema = @Schema(implementation = BookingDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid booking data")
    })
    @PostMapping("/init")
    public ResponseEntity<BookingDto> initializeBooking(
            @RequestBody BookingRequest bookingRequest
    ) {
        return ResponseEntity.ok(bookingService.initializeBooking(bookingRequest));
    }

    @Operation(
            summary = "Add guests to an existing booking",
            description = "Adds one or more guests to a booking with the given ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Guests added successfully",
                    content = @Content(schema = @Schema(implementation = BookingDto.class))),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(
            @Parameter(description = "ID of the booking") @PathVariable Long bookingId,
            @RequestBody List<GuestDto> guestDtoList
    ) {
        return ResponseEntity.ok(bookingService.addGuests(bookingId, guestDtoList));
    }

    @Operation(
            summary = "Initiate payment session for a booking",
            description = "Creates a Stripe payment session and returns the session URL."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment session initiated",
                    content = @Content(schema = @Schema(implementation = BookingPaymentInitResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Booking not found or expired")
    })
    @PostMapping("/{bookingId}/payments")
    public ResponseEntity<BookingPaymentInitResponseDto> initiatePayment(
            @Parameter(description = "ID of the booking") @PathVariable Long bookingId
    ) {
        String sessionUrl = bookingService.initiatePayments(bookingId);
        return ResponseEntity.ok(new BookingPaymentInitResponseDto(sessionUrl));
    }

    @Operation(
            summary = "Cancel an existing booking",
            description = "Cancels the booking with the given ID. No content is returned on success."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Booking cancelled"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(
            @Parameter(description = "ID of the booking") @PathVariable Long bookingId
    ) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Check the current status of a booking",
            description = "Returns the status (e.g., INITIATED, CONFIRMED, CANCELLED, etc.) of the booking."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking status retrieved",
                    content = @Content(schema = @Schema(implementation = BookingStatusResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @GetMapping("/{bookingId}/status")
    public ResponseEntity<BookingStatusResponseDto> getBookingStatus(
            @Parameter(description = "ID of the booking") @PathVariable Long bookingId
    ) {
        return ResponseEntity.ok(new BookingStatusResponseDto(bookingService.getBookingStatus(bookingId)));
    }
}
