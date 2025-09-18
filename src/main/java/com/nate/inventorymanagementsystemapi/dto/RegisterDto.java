package com.nate.inventorymanagementsystemapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Setter @Getter
public class RegisterDto {
    @NotBlank
    private String username;
    @NotBlank
    private String email;
    @NotEmpty
    private String password;
}
