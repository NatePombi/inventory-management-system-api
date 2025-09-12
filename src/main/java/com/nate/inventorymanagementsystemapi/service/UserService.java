package com.nate.inventorymanagementsystemapi.service;

import com.nate.inventorymanagementsystemapi.dto.RegisterDto;
import com.nate.inventorymanagementsystemapi.dto.UserDto;
import com.nate.inventorymanagementsystemapi.exception.UserNotFoundException;
import com.nate.inventorymanagementsystemapi.mapper.UserMapper;
import com.nate.inventorymanagementsystemapi.model.CustomerDetails;
import com.nate.inventorymanagementsystemapi.model.Role;
import com.nate.inventorymanagementsystemapi.model.User;
import com.nate.inventorymanagementsystemapi.repository.UserRepository;
import com.nate.inventorymanagementsystemapi.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
public class UserService implements IUserService, UserDetailsService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    @Override
    public UserDto getByUsername(String username) {
        User user = repo.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(username));
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto register(RegisterDto registerDto) {
        if(repo.existsByUsername(registerDto.getUsername())){
            throw new RuntimeException("Username Already exists");
        }

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(encoder.encode(registerDto.getPassword()));
        if(registerDto.getEmail().contains("admin")){
            user.setRole(Role.ADMIN);
        }else {
            user.setRole(Role.USER);
        }

        repo.save(user);

        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getUsers() {
        return repo.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public boolean deleteUser(String username) {
        User user = repo.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(username));

        repo.delete(user);
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(username));

        return new CustomerDetails(user);
    }
}
