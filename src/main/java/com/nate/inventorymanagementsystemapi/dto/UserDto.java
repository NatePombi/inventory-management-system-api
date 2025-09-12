package com.nate.inventorymanagementsystemapi.dto;

import com.nate.inventorymanagementsystemapi.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private Role role;
}
