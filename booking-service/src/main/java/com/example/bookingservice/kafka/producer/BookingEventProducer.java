package com.example.bookingservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envycorp.commonmodule.events.BookingConfirmedEvent;
import org.envycorp.commonmodule.events.BookingCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingEventProducer {
    private static final String BOOKING_CREATED_TOPIC = "booking.created";
    private static final String BOOKING_CONFIRMED_TOPIC = "booking.confirmed";


    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendBookingCreatedEvent(BookingCreatedEvent event) {
        log.info("Sending booking created event for bookingId={}", event.getBookingId());
        kafkaTemplate.send(BOOKING_CREATED_TOPIC, event.getBookingId().toString(), event);
    }

    public void sendBookingConfirmedEvent(BookingConfirmedEvent event) {
        log.info("Sending booking confirmed event for bookingId={}", event.getBookingId());
        kafkaTemplate.send(BOOKING_CONFIRMED_TOPIC, event.getBookingId().toString(), event);
    }
}
