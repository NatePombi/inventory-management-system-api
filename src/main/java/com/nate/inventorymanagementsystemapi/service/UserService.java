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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    /**
     * Get User by specified username
     *
     * @param username the specified username
     * @return a {@link UserDto} object
     * @throws UserNotFoundException if user with given username not found
     */
    @Override
    public UserDto getByUsername(String username) {
        log.info("Fetches user by username: {}",username);

        //Finds user by username, throws exception if not found
        User user = repo.findByUsername(username)
                .orElseThrow(()-> {
                    log.error("User not found : {}",username);
                    return new UserNotFoundException(username);
                });

        //Map User entity as UserDto object using mapper
        return UserMapper.toDto(user);
    }

    /**
     * Registering User
     *
     * @param registerDto the register object given with user details for registration
     * @return a {@link UserDto} object
     * @throws RuntimeException if username has already exists
     */
    @Override
    public UserDto register(RegisterDto registerDto) {
        log.info("Registering user : {}", registerDto.getUsername());

        //if username already exists, throws exception
        if(repo.existsByUsername(registerDto.getUsername())){
            log.error("Username already exists: {}", registerDto.getUsername());
            throw new RuntimeException("Username Already exists");
        }

        //Creating a User entity to store registered user
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(encoder.encode(registerDto.getPassword()));
        user.setRole(Role.USER);

        //Saves User entity to repo
        log.debug("Saves registered user: {}", user.getUsername());
        repo.save(user);

        //Map User entity to UserDto object using mapper
        return UserMapper.toDto(user);
    }

    /**
     * Gets all Users
     *
     * @param page the page number that the user wants to retrieve(0-based)
     * @param size the amount of items per page
     * @param sortBy the field the page is sorted by (e.g username,role etc)
     * @param direction the way the pages are sorted( ascending or descending)
     * @return a paginated page {@link Page} of {@link UserDto} objects
     */
    @Override
    public Page<UserDto> getUsers(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page,size,sort);

        Page<User> userPage = repo.findAll(pageable);

        return userPage.map(UserMapper::toDto);

    }

    /**
     * Deletes User by username
     *
     * @param username the username of the specified user
     * @return boolean that indicates whether the user was deleted successfully or not
     * @throws UserNotFoundException if user with the given username was not found
     */
    @Override
    public boolean deleteUser(String username) {
        log.info("Deletes user by username: {}", username);

        //Fetches user by username, throws exception if not found
        User user = repo.findByUsername(username)
                .orElseThrow(()-> {
                    log.error("User not found: {}", username);
                    return new UserNotFoundException(username);
                });

        //Deletes user
        log.debug("Found user {} and deleting",username);
        repo.delete(user);
        return true;
    }

    /**
     * Logs in User
     *
     * @param loginDto the {@link LoginDto} object with login details
     * @return a {@link JwtResponse}
     * @throws RuntimeException if password or username are invalid
     */
    @Override
    public JwtResponse login(LoginDto loginDto) {
        log.info("Logging user: {}",loginDto.getUsername());

        //Fetching  user by username
        CustomerDetails details = (CustomerDetails) loadUserByUsername(loginDto.getUsername());

        //If password does not match the encoded password throws an exception
        if(!encoder.matches(loginDto.getPassword(),details.getPassword())){
            log.error("Invalid password or username");
            throw new RuntimeException("Invalid Username or Password");
        }

        //Generating token
        String token = JwtUtil.generateToken(details.getUsername(),details.getUser().getRole());

        //Returns new JwtResponse
        return new JwtResponse(token);
    }


    /**
     * Loading user by username
     *
     * @param username the specified username
     * @return a {@link UserDetails} object
     * @throws UsernameNotFoundException if user with the given username was not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Fetches user by username: {}",username);

        //Fetches user by username in database, throws exception if not found
        User user = repo.findByUsername(username)
                .orElseThrow(()-> {
                    log.info("User not found: {}",username);
                    return new UserNotFoundException(username);
                });

        // returns new CustomerDetails
        return new CustomerDetails(user);
    }
}
