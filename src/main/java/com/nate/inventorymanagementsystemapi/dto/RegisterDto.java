package com.nate.inventorymanagementsystemapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Setter @Getter
public class RegisterDto {
    private String username;
    private String email;
    private String password;
}
