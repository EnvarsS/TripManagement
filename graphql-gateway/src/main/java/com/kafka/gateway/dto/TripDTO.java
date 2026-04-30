package com.kafka.gateway.dto;

import java.io.Serializable;

public class TripDTO implements Serializable {
    // Поля класу (дані про подорож)
    private Long id;
    private String destination;
    private String departureDate;
    private String arrivalDate;
    private Double price;
    private boolean isAvailable;

    // Конструктор без параметрів (необхідний для серіалізації, наприклад, у JSON)
    public TripDTO() {
    }

    // Конструктор з усіма параметрами
    public TripDTO(Long id, String destination, String departureDate, String arrivalDate, Double price, boolean isAvailable) {
        this.id = id;
        this.destination = destination;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.price = price;
        this.isAvailable = isAvailable;
    }

    // Геттери та Сеттери (дозволяють отримувати та змінювати дані)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getDepartureDate() { return departureDate; }
    public void setDepartureDate(String departureDate) { this.departureDate = departureDate; }

    public String getArrivalDate() { return arrivalDate; }
    public void setArrivalDate(String arrivalDate) { this.arrivalDate = arrivalDate; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}

