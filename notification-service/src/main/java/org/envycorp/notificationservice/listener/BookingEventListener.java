package org.envycorp.notificationservice.listener;

import lombok.extern.slf4j.Slf4j;
import org.envycorp.commonmodule.events.BookingConfirmedEvent;
import org.envycorp.commonmodule.events.BookingCreatedEvent;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class BookingEventListener {


    @RetryableTopic(
            attempts = "4",
            backOff = @BackOff(
                    delay = 2000,
                    multiplier = 1.5,
                    maxDelay = 10000
            ),
            autoCreateTopics = "false",
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(
            topics = "booking.created",
            groupId = "notification-group"
    )
    public void handleBookingCreated(
            BookingCreatedEvent event,
            Acknowledgment ack,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition
    ) {
        try {
            log.info("=== [Notification Service] Received BOOKING CREATED ===\n" +
                            "BookingId: {}\nUserId: {}\nTripId: {}\nTopic: {}:{}",
                    event.getBookingId(), event.getUserId(), event.getTripId(), topic, partition);

            sendFakeNotification("CREATED", event.getUserId(), event.getBookingId(), event.getDetails());

            ack.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process BookingCreatedEvent: {}", event, e);
            throw e;
        }
    }


    @RetryableTopic(
            attempts = "4",
            backOff = @BackOff(delay = 2000, multiplier = 1.5, maxDelay = 10000),
            autoCreateTopics = "false",
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = "booking.confirmed", groupId = "notification-group")
    public void handleBookingConfirmed(
            BookingConfirmedEvent event,
            Acknowledgment ack
    ) {
        try {
            log.info("=== [Notification Service] Received BOOKING CONFIRMED ===\n" +
                    "BookingId: {}\nUserId: {}", event.getBookingId(), event.getUserId());

            sendFakeNotification("CONFIRMED", event.getUserId(), event.getBookingId(), "Booking status changed to CONFIRMED");

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process BookingConfirmedEvent", e);
            throw e;
        }
    }

    @DltHandler
    public void handleDlt(Object payload,
                          @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("=== [Notification Service] MESSAGE MOVED TO DLT ===\n" +
                "Topic: {}\nPayload: {}", topic, payload);

    }


    private void sendFakeNotification(String action, UUID userId, UUID bookingId, String details) {
        log.info("📧 [FAKE EMAIL / PUSH] Sent to user {}:\n" +
                        "   Action: Booking {}\n" +
                        "   Booking ID: {}\n" +
                        "   Details: {}",
                userId, action, bookingId, details);
    }
}
