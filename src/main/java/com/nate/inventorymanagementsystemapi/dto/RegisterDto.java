package com.nate.inventorymanagementsystemapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Setter @Getter
public class RegisterDto {
    @NotBlank(message = "Username cannot be empty")
    private String username;
    @NotBlank(message = "email cannot be empty")
    private String email;
    @NotEmpty(message = "Password cannot be empty")
    private String password;
}
