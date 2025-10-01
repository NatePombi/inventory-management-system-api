package com.nate.inventorymanagementsystemapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Setter @Getter
@Schema(description = "Register request")
public class RegisterDto {
    @Schema(description = "Username of user",example = "john")
    @NotBlank(message = "Username cannot be empty")
    private String username;
    @Schema(description = "Email of user", example = "john@gmail.com")
    @NotBlank(message = "email cannot be empty")
    private String email;
    @Schema(description = "Password of user",example = "pass123")
    @NotEmpty(message = "Password cannot be empty")
    private String password;
}
