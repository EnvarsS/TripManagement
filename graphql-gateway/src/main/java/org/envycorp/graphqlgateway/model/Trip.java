package org.envycorp.graphqlgateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trip {
    private Long id;
    private String destination;
    private List<Booking> bookings;
}
