package com.kafka.gateway.dto;

import java.io.Serializable;

/**
 * Клас для передачі даних користувача.
 */
public class UserDTO implements Serializable {

    private Long id;
    private String name;
    private String email;

    // Конструктор за замовчуванням (необхідний для серіалізації)
    public UserDTO() {
    }

    // Конструктор зі всіма параметрами
    public UserDTO(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Геттери та Сеттери
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

