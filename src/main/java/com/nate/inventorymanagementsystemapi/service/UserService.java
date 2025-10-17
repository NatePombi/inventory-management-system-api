package com.nate.inventorymanagementsystemapi.service;

import com.nate.inventorymanagementsystemapi.dto.JwtResponse;
import com.nate.inventorymanagementsystemapi.dto.LoginDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        user.setRole(Role.USER);

        repo.save(user);

        return UserMapper.toDto(user);
    }

    @Override
    public Page<UserDto> getUsers(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page,size,sort);

        Page<User> userPage = repo.findAll(pageable);

        return userPage.map(UserMapper::toDto);

    }

    @Override
    public boolean deleteUser(String username) {
        User user = repo.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(username));

        repo.delete(user);
        return true;
    }

    @Override
    public JwtResponse login(LoginDto loginDto) {
        CustomerDetails details = (CustomerDetails) loadUserByUsername(loginDto.getUsername());

        if(!encoder.matches(loginDto.getPassword(),details.getPassword())){
            throw new RuntimeException("Invalid Username or Password");
        }

        String token = JwtUtil.generateToken(details.getUsername(),details.getUser().getRole());

        return new JwtResponse(token);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(username));

        return new CustomerDetails(user);
    }
}
