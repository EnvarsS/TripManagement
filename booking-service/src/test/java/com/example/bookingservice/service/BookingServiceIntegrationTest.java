package com.example.bookingservice.service;

import com.example.bookingservice.entity.Booking;
import com.example.bookingservice.enums.BookingStatus;
import com.example.bookingservice.kafka.producer.BookingEventProducer;
import com.example.bookingservice.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@ActiveProfiles("test")
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @MockBean
    private BookingEventProducer bookingEventProducer;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        doNothing().when(bookingEventProducer).sendBookingCreatedEvent(org.mockito.ArgumentMatchers.any());
        doNothing().when(bookingEventProducer).sendBookingConfirmedEvent(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldCreateConfirmAndFetchBookingUsingRealSpringContext() {
        Booking booking = Booking.builder()
                .tripId(11L)
                .userId(101L)
                .build();

        Booking createdBooking = bookingService.createBooking(booking);

        assertThat(createdBooking.getId()).isNotNull();
        assertThat(createdBooking.getStatus()).isEqualTo(BookingStatus.CREATED);

        List<Booking> allBookings = bookingService.getAllBookings();
        assertThat(allBookings).hasSize(1);

        Booking fetchedBooking = bookingService.getBookingById(createdBooking.getId());
        assertThat(fetchedBooking.getTripId()).isEqualTo(11L);
        assertThat(fetchedBooking.getUserId()).isEqualTo(101L);

        Booking confirmedBooking = bookingService.confirmBooking(createdBooking.getId());
        assertThat(confirmedBooking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }
}
