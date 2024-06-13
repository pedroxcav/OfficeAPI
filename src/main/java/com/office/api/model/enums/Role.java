package com.office.api.model.enums;

public enum Role {
    COMPANY(1L),
    MANAGER(2L),
    EMPLOYEE(3L);
    final Long role;
    Role(Long role) {
        this.role = role;
    }
}
