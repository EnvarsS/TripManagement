package com.example.bookingservice.service;

import com.example.bookingservice.entity.Booking;
import com.example.bookingservice.enums.BookingStatus;
import com.example.bookingservice.exception.BookingNotFoundException;
import com.example.bookingservice.repository.BookingRepository;
import com.example.bookingservice.kafka.producer.BookingEventProducer;
import org.envycorp.commonmodule.events.BookingConfirmedEvent;
import org.envycorp.commonmodule.events.BookingCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingEventProducer bookingEventProducer;


    @Override
    public Booking createBooking(Booking booking) {
        log.info("Creating booking for tripId={}, userId={}", booking.getTripId(), booking.getUserId());

        booking.setStatus(BookingStatus.CREATED);
        booking.setCreatedAt(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created with id={}, status={}", savedBooking.getId(), savedBooking.getStatus());

        BookingCreatedEvent event = BookingCreatedEvent.builder()
                .bookingId(savedBooking.getId())
                .tripId(savedBooking.getTripId())
                .userId(savedBooking.getUserId())
                .status(savedBooking.getStatus().name())
                .createdAt(savedBooking.getCreatedAt())
                .build();

        bookingEventProducer.sendBookingCreatedEvent(event);

        return savedBooking;
    }

    @Override
    public List<Booking> getAllBookings() {

        log.info("Fetching all bookings");

        List<Booking> bookings = bookingRepository.findAll();

        log.info("Fetched {} bookings", bookings.size());
        return bookings;
    }

    @Override
    public Booking getBookingById(Long id) {
        log.info("Fetching booking by id={}", id);

        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));
    }

    @Override
    public Booking confirmBooking(Long id) {

        log.info("Confirming booking with id={}", id);

        Booking booking = getBookingById(id);
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking with id={} confirmed", updatedBooking.getId());


        BookingConfirmedEvent event = BookingConfirmedEvent.builder()
                .bookingId(updatedBooking.getId())
                .tripId(updatedBooking.getTripId())
                .userId(updatedBooking.getUserId())
                .status(updatedBooking.getStatus().name())
                .confirmedAt(LocalDateTime.now())
                .build();

        bookingEventProducer.sendBookingConfirmedEvent(event);

        return updatedBooking;
    }

    @Override
    public Booking cancelBooking(Long id) {
        log.info("Canceling booking with id={}", id);

        Booking booking = getBookingById(id);
        booking.setStatus(BookingStatus.CANCELED);

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking with id={} canceled", updatedBooking.getId());

        return updatedBooking;
    }
}
