package com.example.bookingservice.graphql;

import com.example.bookingservice.dto.BookingRequest;
import com.example.bookingservice.dto.BookingResponse;
import com.example.bookingservice.entity.Booking;
import com.example.bookingservice.enums.BookingStatus;
import com.example.bookingservice.mapper.BookingMapper;
import com.example.bookingservice.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingMutationTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingMutation bookingMutation;

    @Test
    void shouldCreateBooking() {
        BookingRequest request = BookingRequest.builder()
                .tripId(11L)
                .userId(101L)
                .build();

        Booking booking = Booking.builder()
                .tripId(11L)
                .userId(101L)
                .build();

        Booking savedBooking = Booking.builder()
                .id(1L)
                .tripId(11L)
                .userId(101L)
                .status(BookingStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        BookingResponse response = BookingResponse.builder()
                .id(1L)
                .tripId(11L)
                .userId(101L)
                .status(BookingStatus.CREATED)
                .build();

        when(bookingMapper.toEntity(request)).thenReturn(booking);
        when(bookingService.createBooking(booking)).thenReturn(savedBooking);
        when(bookingMapper.toResponse(savedBooking)).thenReturn(response);

        BookingResponse result = bookingMutation.createBooking(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void shouldConfirmBooking() {
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.CONFIRMED)
                .build();
        BookingResponse response = BookingResponse.builder()
                .id(1L)
                .status(BookingStatus.CONFIRMED)
                .build();

        when(bookingService.confirmBooking(1L)).thenReturn(booking);
        when(bookingMapper.toResponse(booking)).thenReturn(response);

        BookingResponse result = bookingMutation.confirmBooking(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void shouldCancelBooking() {
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.CANCELED)
                .build();
        BookingResponse response = BookingResponse.builder()
                .id(1L)
                .status(BookingStatus.CANCELED)
                .build();

        when(bookingService.cancelBooking(1L)).thenReturn(booking);
        when(bookingMapper.toResponse(booking)).thenReturn(response);

        BookingResponse result = bookingMutation.cancelBooking(1L);

        assertThat(result).isEqualTo(response);
    }
}
