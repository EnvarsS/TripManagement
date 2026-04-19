package com.example.bookingservice.graphql;

import com.example.bookingservice.dto.BookingRequest;
import com.example.bookingservice.dto.BookingResponse;
import com.example.bookingservice.entity.Booking;
import com.example.bookingservice.mapper.BookingMapper;
import com.example.bookingservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class BookingMutation {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;


    @MutationMapping
    public  BookingResponse createBooking(@Argument BookingRequest request){
        Booking booking = bookingMapper.toEntity(request);
        Booking savedBooking = bookingService.createBooking(booking);
        return bookingMapper.toResponse(savedBooking);
    }

    @MutationMapping
    public BookingResponse confirmBooking(@Argument Long id) {
        return bookingMapper.toResponse(bookingService.confirmBooking(id));
    }

    @MutationMapping
    public BookingResponse cancelBooking(@Argument Long id) {
        return bookingMapper.toResponse(bookingService.cancelBooking(id));
    }
}