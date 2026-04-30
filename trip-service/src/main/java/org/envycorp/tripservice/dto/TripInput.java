package org.envycorp.tripservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TripInput {
    @NotBlank(message = "Пункт призначення не може бути порожнім")
    private String destination;
}

