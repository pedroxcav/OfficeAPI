package com.office.api.model.dto.company;

import com.office.api.model.dto.address.NewAddressDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CNPJ;

public record NewCompanyDTO(
        @NotBlank
        String name,
        @CNPJ
        String cnpj,
        @NotBlank
        String password,
        @Valid
        NewAddressDTO address) {
}
