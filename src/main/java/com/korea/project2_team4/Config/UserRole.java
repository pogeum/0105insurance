package com.korea.project2_team4.Config;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ROLE_ADMIN"),

    USER("ROLE_USER");

    UserRole(String value) {
        this.value = value;
    }

    private final String value;
}
