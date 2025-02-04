package org.example.practica3.enums;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("Administrador"),
    USER("Usuario");

    private final String description;

    Role(String description) {
        this.description = description;
    }
}
