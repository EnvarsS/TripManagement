package com.example.bookingservice.kafka.producer;

import org.envycorp.commonmodule.events.BookingConfirmedEvent;
import org.envycorp.commonmodule.events.BookingCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingEventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private BookingEventProducer bookingEventProducer;

    @Test
    void shouldSendBookingCreatedEvent() {
        BookingCreatedEvent event = BookingCreatedEvent.builder()
                .bookingId(1L)
                .tripId(11L)
                .userId(101L)
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .build();

        bookingEventProducer.sendBookingCreatedEvent(event);

        verify(kafkaTemplate).send("booking.created", "1", event);
    }

    @Test
    void shouldSendBookingConfirmedEvent() {
        BookingConfirmedEvent event = BookingConfirmedEvent.builder()
                .bookingId(1L)
                .tripId(11L)
                .userId(101L)
                .status("CONFIRMED")
                .confirmedAt(LocalDateTime.now())
                .build();

        bookingEventProducer.sendBookingConfirmedEvent(event);

        verify(kafkaTemplate).send("booking.confirmed", "1", event);
    }
}
