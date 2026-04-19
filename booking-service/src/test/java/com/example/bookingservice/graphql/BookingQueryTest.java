package com.example.bookingservice.graphql;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingQueryTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingQuery bookingQuery;

    @Test
    void shouldReturnAllBookings() {
        Booking firstBooking = Booking.builder().id(1L).build();
        Booking secondBooking = Booking.builder().id(2L).build();
        BookingResponse firstResponse = BookingResponse.builder().id(1L).build();
        BookingResponse secondResponse = BookingResponse.builder().id(2L).build();

        when(bookingService.getAllBookings()).thenReturn(List.of(firstBooking, secondBooking));
        when(bookingMapper.toResponse(firstBooking)).thenReturn(firstResponse);
        when(bookingMapper.toResponse(secondBooking)).thenReturn(secondResponse);

        List<BookingResponse> result = bookingQuery.getAllBookings();

        assertThat(result).containsExactly(firstResponse, secondResponse);
    }

    @Test
    void shouldReturnBookingById() {
        Booking booking = Booking.builder()
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

        when(bookingService.getBookingById(1L)).thenReturn(booking);
        when(bookingMapper.toResponse(booking)).thenReturn(response);

        BookingResponse result = bookingQuery.getBookingById(1L);

        assertThat(result).isEqualTo(response);
    }
}
