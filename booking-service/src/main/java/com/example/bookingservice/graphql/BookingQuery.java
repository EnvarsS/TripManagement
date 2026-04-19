package com.example.bookingservice.graphql;


import com.example.bookingservice.dto.BookingResponse;
import com.example.bookingservice.mapper.BookingMapper;
import com.example.bookingservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookingQuery {
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @QueryMapping
    public List<BookingResponse> getAllBookings() {
        return bookingService.getAllBookings()
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }
    @QueryMapping
    public BookingResponse getBookingById(@Argument Long id){
        return bookingMapper.toResponse(bookingService.getBookingById(id));
    }
}
