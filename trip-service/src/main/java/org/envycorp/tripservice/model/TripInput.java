package org.envycorp.tripservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TripInput {
    @NotNull(message = "ID користувача є обов'язковим")
    private Long userId;

    @NotBlank(message = "Пункт відправлення не може бути порожнім")
    private String origin;

    @NotBlank(message = "Пункт призначення не може бути порожнім")
    private String destination;

    @NotBlank(message = "Час відправлення є обов'язковим")
    private String departureTime; // Очікується ISO-8601 (напр. "2026-05-15T10:00:00")
}

