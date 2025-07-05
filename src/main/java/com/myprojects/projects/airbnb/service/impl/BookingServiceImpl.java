package com.myprojects.projects.airbnb.service.impl;

import com.myprojects.projects.airbnb.dto.BookingDto;
import com.myprojects.projects.airbnb.dto.BookingRequest;
import com.myprojects.projects.airbnb.dto.GuestDto;
import com.myprojects.projects.airbnb.dto.HotelReportDto;
import com.myprojects.projects.airbnb.entity.*;
import com.myprojects.projects.airbnb.entity.enums.BookingStatus;
import com.myprojects.projects.airbnb.exception.ResourceNotFoundException;
import com.myprojects.projects.airbnb.exception.UnAuthorizedException;
import com.myprojects.projects.airbnb.repository.*;
import com.myprojects.projects.airbnb.service.BookingService;
import com.myprojects.projects.airbnb.service.CheckoutService;
import com.myprojects.projects.airbnb.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.myprojects.projects.airbnb.util.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final CheckoutService checkoutService;
    private final GuestRepository guestRepository;
    private final PricingService pricingService;

    @Value("${frontend.url}")
    private String frontendUrl;

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
        inventoryRepository.initBooking(room.getId(), bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(),
                bookingRequest.getRoomsCount());

        //Dynamic Pricing
        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(totalPrice)
                .build();

        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {
        log.info("Adding guests to booking ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        User user =getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorizedException("Booking does not belong to this user with id: " + user.getId());
        }

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
            guest = guestRepository.save(guest);
            booking.getGuests().add(guest);
        }
        // Save the updated booking
        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking= bookingRepository.save(booking);
        log.info("Guests added to booking ID: {}", bookingId);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public String initiatePayments(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorizedException("Booking does not belong to this user with id: " + user.getId());
        }

        if(hasBookingExpired(booking)) {
            log.error("Booking with ID: {} has expired", bookingId);
            throw new IllegalStateException("Booking has expired");
        }
        String sessionUrl = checkoutService.getCheckoutSession(booking , frontendUrl+"/payments/success", frontendUrl+"/payments/failure" );

        log.info("Payment session URL generated: {}", sessionUrl);
        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);

        bookingRepository.save(booking);

        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        if("checkout.session.completed".equals(event.getType())){
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if(session == null)return;
            String sessionId = session.getId();
            Booking booking = bookingRepository.findByPaymentSessionId(sessionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found with session ID: " + sessionId));

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(),
                                   booking.getCheckInDate(), booking.getCheckOutDate(), booking.getRoomsCount());

            inventoryRepository.confirmBooking(booking.getRoom().getId(),
                                   booking.getCheckInDate(), booking.getCheckOutDate(), booking.getRoomsCount());
            log.info("Successfully captured payment for booking ID: {}", booking.getId());

        }
        else{
            log.warn("Unhandled event type: {}", event.getType());
        }
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorizedException("Booking does not belong to this user with id: " + user.getId());
        }

      if(booking.getBookingStatus() !=BookingStatus.CONFIRMED){
            log.error("Cannot cancel booking with ID: {}. Current status: {}", bookingId, booking.getBookingStatus());
            throw new IllegalStateException("Only confirmed bookings can be cancelled");
      }
      booking.setBookingStatus(BookingStatus.CANCELLED);
      bookingRepository.save(booking);

      inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(),
                booking.getCheckInDate(), booking.getCheckOutDate(), booking.getRoomsCount());

      inventoryRepository.cancelBooking(booking.getRoom().getId(),
                booking.getCheckInDate(), booking.getCheckOutDate(), booking.getRoomsCount());

      //handle refund
        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundCreateParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();
            Refund.create(refundCreateParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public BookingStatus getBookingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorizedException("Booking does not belong to this user with id: " + user.getId());
        }
        return booking.getBookingStatus();
    }

    @Override
    public List<BookingDto> getAllBookingsByHotelId(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));
        User user = getCurrentUser();
        if (!user.equals(hotel.getOwner()))
            throw new AccessDeniedException("This User does not own this hotel with ID: " + hotelId);

        List<Booking> bookings = bookingRepository.findByHotel(hotel);

        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: {}" + hotelId));
        User user = getCurrentUser();
        log.info("Generating Report  for hotel with ID: {}", hotelId);
        if(!user.equals(hotel.getOwner())) throw new AccessDeniedException("This User does not own this hotel with ID: " + hotelId);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Booking> bookings = bookingRepository.findByHotelAndCreatedAtBetween(hotel, startDateTime, endDateTime);

        Long totalConfirmedBookings = bookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .count();

        BigDecimal totalRevenue = bookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .map(Booking::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageRevenue = totalConfirmedBookings == 0 ? BigDecimal.ZERO : totalRevenue
                .divide(BigDecimal.valueOf(totalConfirmedBookings), RoundingMode.HALF_UP);


        return new HotelReportDto(totalConfirmedBookings, totalRevenue, averageRevenue);
    }

    @Override
    public List<BookingDto> getMyBookings() {
        User user = getCurrentUser();
        log.info("Fetching bookings for user with ID: {}", user.getId());

        List<Booking> bookings = bookingRepository.findByUser(user);

        if (bookings.isEmpty()) {
            log.warn("No bookings found for user with ID: {}", user.getId());
            return List.of();
        }

        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingDto.class))
                .collect(Collectors.toList());
    }

    public boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt()
                .plusMinutes(10)
                .isBefore(LocalDateTime.now());
    }

}
