package com.nate.inventorymanagementsystemapi.service;

import com.nate.inventorymanagementsystemapi.dto.RegisterDto;
import com.nate.inventorymanagementsystemapi.dto.UserDto;

import java.util.List;

public interface IUserService {
    UserDto getByUsername(String username);
    UserDto register(RegisterDto registerDto);
    List<UserDto> getUsers();
    boolean deleteUser(String username);

}
