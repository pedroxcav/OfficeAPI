package com.office.api.model.dto.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewAddressDTO(
        @NotBlank
        @Size(min = 8, max = 8)
        @JsonProperty("zip_code")
        String zipCode,
        @NotBlank
        String number,
        @NotBlank
        String street,
        @NotBlank
        String neighborhood,
        @NotBlank
        String city,
        @NotBlank
        String state) {
}
