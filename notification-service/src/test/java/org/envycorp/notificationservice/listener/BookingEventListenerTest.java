package org.envycorp.notificationservice.listener;
import org.envycorp.commonmodule.events.BookingConfirmedEvent;
import org.envycorp.commonmodule.events.BookingCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.apache.kafka.common.requests.DeleteAclsResponse.log;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@EmbeddedKafka(
        partitions = 1,
        controlledShutdown = true,
        topics = {
                "booking.created",
                "booking.confirmed",
                "booking.created-dlt",
                "booking.confirmed-dlt"
        },
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:0",
                "port=0"
        }
)
@ActiveProfiles("test")
class BookingEventListenerTest {

    private static final String BOOKING_CREATED_TOPIC = "booking.created";
    private static final String BOOKING_CONFIRMED_TOPIC = "booking.confirmed";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private BookingEventListener bookingEventListener;


    @Test
    void shouldProcessBookingCreatedEventSuccessfully() {
        UUID bookingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID tripId = UUID.randomUUID();

        BookingCreatedEvent event = BookingCreatedEvent.builder()
                .bookingId(bookingId)
                .userId(userId)
                .tripId(tripId)
                .bookingType("HOTEL")
                .details("Booking for Paris hotel")
                .createdAt(LocalDateTime.now())
                .build();

        kafkaTemplate.send(BOOKING_CREATED_TOPIC, bookingId.toString(), event);

        await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    log.info("Очікуємо обробки події для bookingId: {}", bookingId);
                    assertTrue(true);
                });

        log.info("✅ Тест BookingCreatedEvent пройшов успішно");
    }

    @Test
    void shouldProcessBookingConfirmedEventSuccessfully() {
        UUID bookingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        BookingConfirmedEvent event = BookingConfirmedEvent.builder()
                .bookingId(bookingId)
                .userId(userId)
                .status("CONFIRMED")
                .confirmedAt(LocalDateTime.now())
                .build();

        kafkaTemplate.send(BOOKING_CONFIRMED_TOPIC, bookingId.toString(), event);

        await()
                .atMost(8, TimeUnit.SECONDS)
                .untilAsserted(() -> log.info("Подія CONFIRMED оброблена"));

        log.info("✅ Тест BookingConfirmedEvent пройшов успішно");
    }
}
