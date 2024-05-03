package com.office.api.model.enums;

import lombok.Getter;

@Getter
public enum Role {
    COMPANY("company"),
    MANAGER("manager"),
    EMPLOYEE("employee");
    private final String role;
    Role(String role) {
        this.role = role;
    }
}
