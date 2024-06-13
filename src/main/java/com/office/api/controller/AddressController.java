package com.office.api.controller;

import com.office.api.model.dto.address.AddressDTO;
import com.office.api.model.dto.address.UpdateAddressDTO;
import com.office.api.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/adresses")
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid UpdateAddressDTO data, JwtAuthenticationToken token) {
        addressService.updateAddress(data, token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping
    public ResponseEntity<AddressDTO> getAddress(JwtAuthenticationToken token) {
        AddressDTO address = addressService.getAddress(token);
        return ResponseEntity.status(HttpStatus.OK).body(address);
    }
}
