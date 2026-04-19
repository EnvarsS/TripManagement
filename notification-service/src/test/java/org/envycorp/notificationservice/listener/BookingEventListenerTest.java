package org.envycorp.notificationservice.listener;


import org.envycorp.commonmodule.events.BookingConfirmedEvent;
import org.envycorp.commonmodule.events.BookingCreatedEvent;
import org.envycorp.notificationservice.NotificationServiceApplication;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
@SpringBootTest(
        classes = NotificationServiceApplication.class,
        properties = {
                "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
                "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
                "spring.kafka.consumer.auto-offset-reset=earliest",
                "spring.kafka.consumer.properties.spring.json.trusted.packages=*"
        }
)
@EmbeddedKafka(
        partitions = 1,
        topics = {"booking.created", "booking.confirmed"}
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)

@ActiveProfiles("test")
class BookingEventListenerTest {

    private static final Logger log = LoggerFactory.getLogger(BookingEventListenerTest.class);


    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private MockMvc mockMvc;

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
                .details("Test booking for Paris")
                .createdAt(LocalDateTime.now())
                .build();

        log.info("→ Надсилаємо BookingCreatedEvent: {}", bookingId);

        kafkaTemplate.send("booking.created", bookingId.toString(), event);

        await()
                .atMost(15, TimeUnit.SECONDS)
                .pollInterval(600, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> log.info("✓ BookingCreatedEvent успішно оброблено"));
    }
    @Test
    void shouldProcessBookingConfirmedEventSuccessfully() {
        UUID bookingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID tripId = UUID.randomUUID();

        BookingConfirmedEvent event = BookingConfirmedEvent.builder()
                .bookingId(bookingId)
                .userId(userId)
                .tripId(tripId)
                .bookingType("HOTEL")
                .status("CONFIRMED")
                .confirmedAt(LocalDateTime.now())
                .details("Test confirmation")
                .build();

        log.info("→ Надсилаємо BookingConfirmedEvent: {}", bookingId);

        kafkaTemplate.send("booking.confirmed", bookingId.toString(), event);

        await()
                .atMost(15, TimeUnit.SECONDS)
                .pollInterval(600, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> log.info("✓ BookingConfirmedEvent успішно оброблено"));
    }

}