package com.office.api.model.dto.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.office.api.model.Address;

public record AddressDTO(
        @JsonProperty("zip_code")
        String zipCode,
        String number,
        String street,
        String neighborhood,
        String city,
        String state) {

    public static AddressDTO toDTO(Address address) {
        return new AddressDTO(
                address.getZipCode(),
                address.getNumber(),
                address.getStreet(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState());
    }
}
