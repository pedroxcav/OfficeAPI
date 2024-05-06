package com.office.api.model.dto.address;

import com.office.api.model.Address;

public record AddressDTO(
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
