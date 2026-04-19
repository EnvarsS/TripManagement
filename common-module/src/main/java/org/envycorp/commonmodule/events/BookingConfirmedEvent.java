package org.envycorp.commonmodule.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingConfirmedEvent {

    private Long bookingId;
    private Long tripId;
    private Long userId;
    private String status;
    private LocalDateTime confirmedAt;
}