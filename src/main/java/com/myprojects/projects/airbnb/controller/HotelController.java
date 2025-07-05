package com.myprojects.projects.airbnb.controller;

import com.myprojects.projects.airbnb.dto.BookingDto;
import com.myprojects.projects.airbnb.dto.HotelDto;
import com.myprojects.projects.airbnb.dto.HotelReportDto;
import com.myprojects.projects.airbnb.exception.ResourceNotFoundException;
import com.myprojects.projects.airbnb.repository.HotelRepository;
import com.myprojects.projects.airbnb.service.BookingService;
import com.myprojects.projects.airbnb.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
public class HotelController {

    private static final Logger log = LoggerFactory.getLogger(HotelController.class);

    private final HotelService hotelService;
    private final HotelRepository hotelRepository;
    private final BookingService bookingService;

    @Operation(summary = "Create a new hotel")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Hotel created successfully",
                    content = @Content(schema = @Schema(implementation = HotelDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(@RequestBody HotelDto hotelDto) {
        HotelDto hotel = hotelService.createNewHotel(hotelDto);
        return new ResponseEntity<>(hotel, HttpStatus.CREATED);
    }

    @Operation(summary = "Get hotel by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hotel found",
                    content = @Content(schema = @Schema(implementation = HotelDto.class))),
            @ApiResponse(responseCode = "404", description = "Hotel not found", content = @Content)
    })
    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@Parameter(description = "ID of the hotel to fetch") @PathVariable Long hotelId) {
        HotelDto hotelDto = hotelService.getHotelById(hotelId);
        return new ResponseEntity<>(hotelDto, HttpStatus.OK);
    }

    @Operation(summary = "Update hotel details by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hotel updated successfully",
                    content = @Content(schema = @Schema(implementation = HotelDto.class))),
            @ApiResponse(responseCode = "404", description = "Hotel not found", content = @Content)
    })
    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDto> updateHotelById(@Parameter(description = "ID of the hotel to update") @PathVariable Long hotelId,
                                                    @RequestBody HotelDto hotelDto) {
        HotelDto hotel = hotelService.updateHotelById(hotelId, hotelDto);
        return new ResponseEntity<>(hotel, HttpStatus.OK);
    }

    @Operation(summary = "Delete hotel by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Hotel deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Hotel not found", content = @Content)
    })
    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotelById(@Parameter(description = "ID of the hotel to delete") @PathVariable Long hotelId) {
        boolean exists = hotelRepository.existsById(hotelId);
        if (!exists) throw new ResourceNotFoundException("Hotel not found with ID: " + hotelId);
        hotelService.deleteHotelById(hotelId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activate a hotel by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Hotel activated successfully"),
            @ApiResponse(responseCode = "404", description = "Hotel not found", content = @Content)
    })
    @PatchMapping("/{hotelId}/activate")
    public ResponseEntity<Void> activateHotel(@Parameter(description = "ID of the hotel to activate") @PathVariable Long hotelId) {
        hotelService.activateHotel(hotelId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all hotels")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of all hotels",
                    content = @Content(schema = @Schema(implementation = HotelDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels() {
        List<HotelDto> hotelDtos = hotelService.getAllHotels();
        return new ResponseEntity<>(hotelDtos, HttpStatus.OK);
    }

    @Operation(summary = "Get all bookings for a hotel")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of bookings for the hotel",
                    content = @Content(schema = @Schema(implementation = BookingDto.class))),
            @ApiResponse(responseCode = "404", description = "Hotel not found", content = @Content)
    })
    @GetMapping("/{hotelId}/bookings")
    public ResponseEntity<List<BookingDto>> getAllBookingsByHotelId(@Parameter(description = "ID of the hotel") @PathVariable Long hotelId) {
        return ResponseEntity.ok(bookingService.getAllBookingsByHotelId(hotelId));
    }

    @Operation(summary = "Get booking report for a hotel within a date range")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hotel booking report",
                    content = @Content(schema = @Schema(implementation = HotelReportDto.class))),
            @ApiResponse(responseCode = "404", description = "Hotel not found", content = @Content)
    })
    @GetMapping("/{hotelId}/reports")
    public ResponseEntity<HotelReportDto> getHotelReport(
            @Parameter(description = "ID of the hotel") @PathVariable Long hotelId,
            @Parameter(description = "Start date for the report (optional)") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "End date for the report (optional)") @RequestParam(required = false) LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now();

        return ResponseEntity.ok(bookingService.getHotelReport(hotelId, startDate, endDate));
    }
}




