package org.envycorp.notificationservice.listener;


import lombok.extern.slf4j.Slf4j;
//import org.envycorp.commonmodule.events.BookingCreatedEvent;
//import org.envycorp.commonmodule.events.BookingConfirmedEvent;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.test.context.EmbeddedKafka;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//import static org.awaitility.Awaitility.await;
//
//@Slf4j
//@SpringBootTest
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
//@EmbeddedKafka(
//        partitions = 1,
//        topics = {"booking.created", "booking.confirmed", "booking.created-dlt", "booking.confirmed-dlt"}
//)
//@ActiveProfiles("test")
//class BookingEventListenerTest {
//
//    @Autowired
//    private KafkaTemplate<String, Object> kafkaTemplate;
//
//    @Test
//    void shouldProcessBookingCreatedEventSuccessfully() {
//        UUID bookingId = UUID.randomUUID();
//        UUID userId = UUID.randomUUID();
//        UUID tripId = UUID.randomUUID();
//
//        BookingCreatedEvent event = BookingCreatedEvent.builder()
//                .bookingId(bookingId)
//                .userId(userId)
//                .tripId(tripId)
//                .bookingType("HOTEL")
//                .details("Test booking creation")
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        log.info("→ Sending BookingCreatedEvent: {}", bookingId);
//        kafkaTemplate.send("booking.created", bookingId.toString(), event);
//
//        await()
//                .atMost(15, TimeUnit.SECONDS)
//                .pollInterval(600, TimeUnit.MILLISECONDS)
//                .untilAsserted(() -> log.info("✓ BookingCreatedEvent processed successfully"));
//    }
//
//    @Test
//    void shouldProcessBookingConfirmedEventSuccessfully() {
//        UUID bookingId = UUID.randomUUID();
//        UUID userId = UUID.randomUUID();
//        UUID tripId = UUID.randomUUID();
//
//        BookingConfirmedEvent event = BookingConfirmedEvent.builder()
//                .bookingId(bookingId)
//                .userId(userId)
//                .tripId(tripId)
//                .bookingType("HOTEL")
//                .status("CONFIRMED")
//                .confirmedAt(LocalDateTime.now())
//                .details("Test booking confirmation")
//                .build();
//
//        log.info("→ Sending BookingConfirmedEvent: {}", bookingId);
//        kafkaTemplate.send("booking.confirmed", bookingId.toString(), event);
//
//        await()
//                .atMost(15, TimeUnit.SECONDS)
//                .pollInterval(600, TimeUnit.MILLISECONDS)
//                .untilAsserted(() -> log.info("✓ BookingConfirmedEvent processed successfully"));
//    }
//}
import org.envycorp.commonmodule.events.BookingConfirmedEvent;
import org.envycorp.commonmodule.events.BookingCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean; // или MockitoSpy
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.timeout;

@Slf4j
@SpringBootTest()
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@EmbeddedKafka(
        partitions = 1,
        controlledShutdown = true,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"},
        topics = {"booking.created", "booking.confirmed"},
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
        )
@ActiveProfiles("test")
class BookingEventListenerTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @SpyBean
    private BookingEventListener bookingEventListener;

    @Test
    void shouldProcessBookingCreatedEventSuccessfully() {
        BookingCreatedEvent event = BookingCreatedEvent.builder()
                .bookingId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .tripId(UUID.randomUUID())
                .details("Test")
                .build();

        kafkaTemplate.send("booking.created", event.getBookingId().toString(), event);

        verify(bookingEventListener, timeout(10000).times(1))
                .handleBookingCreated(any(), any(), any(), any(Integer.class));
    }

    @Test
    void shouldProcessBookingConfirmedEventSuccessfully() {
        BookingConfirmedEvent event = BookingConfirmedEvent.builder()
                .bookingId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .status("CONFIRMED")
                .build();

        kafkaTemplate.send("booking.confirmed", event.getBookingId().toString(), event);

        verify(bookingEventListener, timeout(10000).times(1))
                .handleBookingConfirmed(any(), any());
    }
}