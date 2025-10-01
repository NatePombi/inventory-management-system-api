package com.nate.inventorymanagementsystemapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Schema(description = "Login request")
public class LoginDto {
    @Schema(description = "Username of the user",example = "john")
    @NotBlank(message = "Username cannot be empty")
    private String username;
    @Schema(description = "Password of user",example = "doe123")
    @NotBlank(message = "password cannot be empty")
    private String password;
}
