package com.nate.inventorymanagementsystemapi.service;

import com.nate.inventorymanagementsystemapi.dto.JwtResponse;
import com.nate.inventorymanagementsystemapi.dto.LoginDto;
import com.nate.inventorymanagementsystemapi.dto.RegisterDto;
import com.nate.inventorymanagementsystemapi.dto.UserDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IUserService {
    UserDto getByUsername(String username);
    UserDto register(RegisterDto registerDto);
    Page<UserDto> getUsers(int page, int size, String sortBy, String direction);
    boolean deleteUser(String username);
    JwtResponse login(LoginDto loginDto);

}
