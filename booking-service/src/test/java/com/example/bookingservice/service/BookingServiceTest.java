package com.example.bookingservice.service;

import com.example.bookingservice.entity.Booking;
import com.example.bookingservice.enums.BookingStatus;
import com.example.bookingservice.exception.BookingNotFoundException;
import com.example.bookingservice.kafka.producer.BookingEventProducer;
import com.example.bookingservice.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingEventProducer bookingEventProducer;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void shouldCreateBooking() {
        Booking booking = Booking.builder()
                .tripId(1L)
                .userId(100L)
                .build();

        Booking savedBooking = Booking.builder()
                .id(1L)
                .tripId(1L)
                .userId(100L)
                .status(BookingStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
        doNothing().when(bookingEventProducer).sendBookingCreatedEvent(any());

        Booking result = bookingService.createBooking(booking);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BookingStatus.CREATED, result.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void shouldReturnBookingById() {
        Booking booking = Booking.builder()
                .id(1L)
                .tripId(1L)
                .userId(100L)
                .status(BookingStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenBookingNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        BookingNotFoundException exception = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.getBookingById(99L)
        );

        assertEquals("Booking not found with id: 99", exception.getMessage());
        verify(bookingRepository, times(1)).findById(99L);
    }

    @Test
    void shouldConfirmBooking() {
        Booking booking = Booking.builder()
                .id(1L)
                .tripId(1L)
                .userId(100L)
                .status(BookingStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        Booking confirmedBooking = Booking.builder()
                .id(1L)
                .tripId(1L)
                .userId(100L)
                .status(BookingStatus.CONFIRMED)
                .createdAt(booking.getCreatedAt())
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(confirmedBooking);
        doNothing().when(bookingEventProducer).sendBookingConfirmedEvent(any());

        Booking result = bookingService.confirmBooking(1L);

        assertEquals(BookingStatus.CONFIRMED, result.getStatus());
        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void shouldCancelBooking() {
        Booking booking = Booking.builder()
                .id(1L)
                .tripId(1L)
                .userId(100L)
                .status(BookingStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        Booking canceledBooking = Booking.builder()
                .id(1L)
                .tripId(1L)
                .userId(100L)
                .status(BookingStatus.CANCELED)
                .createdAt(booking.getCreatedAt())
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(canceledBooking);

        Booking result = bookingService.cancelBooking(1L);

        assertEquals(BookingStatus.CANCELED, result.getStatus());
        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }
}
