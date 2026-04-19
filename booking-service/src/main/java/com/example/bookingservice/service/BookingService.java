package com.example.bookingservice.service;

import com.example.bookingservice.entity.Booking;

import java.util.List;

public interface BookingService {

    Booking createBooking(Booking booking);

    List<Booking> getAllBookings();

    Booking getBookingById(Long id);

    Booking confirmBooking(Long id);

    Booking cancelBooking(Long id);

}
