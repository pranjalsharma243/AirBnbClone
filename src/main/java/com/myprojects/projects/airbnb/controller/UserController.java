package com.myprojects.projects.airbnb.controller;

import com.myprojects.projects.airbnb.dto.BookingDto;
import com.myprojects.projects.airbnb.dto.ProfileUpdateRequestDto;
import com.myprojects.projects.airbnb.dto.UserDto;
import com.myprojects.projects.airbnb.service.BookingService;
import com.myprojects.projects.airbnb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;

    @Operation(summary = "Update the current user's profile")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid profile update request", content = @Content)
    })
    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(
            @RequestBody(description = "Profile update data", required = true,
                    content = @Content(schema = @Schema(implementation = ProfileUpdateRequestDto.class)))
            ProfileUpdateRequestDto profileUpdateRequestDto) {
        userService.updateProfile(profileUpdateRequestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get current user's bookings")
    @ApiResponse(responseCode = "200", description = "List of user's bookings",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingDto.class)))
    @GetMapping("/mybookings")
    public ResponseEntity<List<BookingDto>> getMyBookings() {
        return ResponseEntity.ok(bookingService.getMyBookings());
    }

    @Operation(summary = "Get current user's profile")
    @ApiResponse(responseCode = "200", description = "User profile data",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }
}