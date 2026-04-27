package com.kafka.gateway.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class BookingDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;

    @NotBlank(message = "Ім'я клієнта не може бути порожнім")
    @Size(min = 2, max = 100, message = "Ім'я має бути від 2 до 100 символів")
    @JsonProperty("customer_name")
    private String customerName;

    @NotNull(message = "Тип номера обов'язковий")
    private String roomType;

    @NotNull(message = "Дата заїзду обов'язкова")
    @FutureOrPresent(message = "Дата заїзду не може бути в минулому")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkInDate;

    @NotNull(message = "Дата виїзду обов'язкова")
    @Future(message = "Дата виїзду має бути в майбутньому")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkOutDate;

    @Positive(message = "Ціна має бути більшою за нуль")
    private double totalPrice;

    public BookingDTO() {}

    // Гетери та сетери...

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public LocalDateTime getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDateTime checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDateTime getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDateTime checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}

