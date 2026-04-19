package com.example.bookingservice.dto;

import com.example.bookingservice.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponse {

    private Long id;
    private Long tripId;
    private Long userId;
    private BookingStatus status;
    private LocalDateTime createdAt;
}
