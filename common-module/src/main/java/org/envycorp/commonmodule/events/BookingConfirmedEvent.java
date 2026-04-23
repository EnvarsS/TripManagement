package org.envycorp.commonmodule.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingConfirmedEvent {
    private UUID bookingId;
    private UUID userId;
    private UUID tripId;
    private String bookingType;
    private String details;
    private String status;
    private LocalDateTime confirmedAt;
    private String confirmationNumber;
}
