package com.example.bookingservice.mapper;

import com.example.bookingservice.dto.BookingRequest;
import com.example.bookingservice.dto.BookingResponse;
import com.example.bookingservice.entity.Booking;
import com.example.bookingservice.enums.BookingStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    private final BookingMapper bookingMapper = new BookingMapper();

    @Test
    void shouldMapRequestToEntity() {
        BookingRequest request = BookingRequest.builder()
                .tripId(11L)
                .userId(101L)
                .build();

        Booking booking = bookingMapper.toEntity(request);

        assertThat(booking.getTripId()).isEqualTo(11L);
        assertThat(booking.getUserId()).isEqualTo(101L);
        assertThat(booking.getId()).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        LocalDateTime createdAt = LocalDateTime.now();
        Booking booking = Booking.builder()
                .id(1L)
                .tripId(11L)
                .userId(101L)
                .status(BookingStatus.CREATED)
                .createdAt(createdAt)
                .build();

        BookingResponse response = bookingMapper.toResponse(booking);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTripId()).isEqualTo(11L);
        assertThat(response.getUserId()).isEqualTo(101L);
        assertThat(response.getStatus()).isEqualTo(BookingStatus.CREATED);
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
    }
}
