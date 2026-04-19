package com.example.bookingservice.mapper;


import com.example.bookingservice.dto.BookingRequest;
import com.example.bookingservice.dto.BookingResponse;
import com.example.bookingservice.entity.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public Booking toEntity(BookingRequest request){

        return Booking.builder()
                .tripId(request.getTripId())
                .userId(request.getUserId())
                .build();
    }

    public BookingResponse toResponse(Booking booking){

        return BookingResponse.builder()
                .id(booking.getId())
                .tripId(booking.getTripId())
                .userId(booking.getUserId())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
