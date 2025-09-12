package com.nate.inventorymanagementsystemapi.service;

import com.nate.inventorymanagementsystemapi.dto.RegisterDto;
import com.nate.inventorymanagementsystemapi.dto.UserDto;
import com.nate.inventorymanagementsystemapi.exception.UserNotFoundException;
import com.nate.inventorymanagementsystemapi.model.CustomerDetails;
import com.nate.inventorymanagementsystemapi.model.Role;
import com.nate.inventorymanagementsystemapi.model.User;
import com.nate.inventorymanagementsystemapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository repo;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    private User mockUser;

    private UserService service;

    @BeforeEach
    void startUp(){
        mockUser = new User();
        mockUser.setUsername("tester");
        mockUser.setPassword("tester123");
        mockUser.setRole(Role.USER);
        service = new UserService(repo,encoder);
    }

    @Test
    @DisplayName("Get User by Username Test: Successful")
    void getUserByUsernameTest_Success(){
        when(repo.findByUsername("tester")).thenReturn(Optional.of(mockUser));


        UserDto dto = service.getByUsername("tester");

        assertEquals("tester",dto.getUsername(),"Should have the same username tester");
    }

    @Test
    @DisplayName("Get User by Username Test: Fail, should throw an exception")
    void getUserByUserNameTest_FailShouldThrowException(){

       Exception ex =  assertThrows(UserNotFoundException.class,()->{
            service.getByUsername("tester");
        });

       assertTrue(ex.getMessage().contains("tester"));
    }

    @Test
    @DisplayName("Register User Test: Success as Admin with admin email")
    void registerUserTest_SuccessAsAdmin(){
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("test");
        registerDto.setPassword("tester123");
        registerDto.setEmail("admin@gmail.com");
        when(repo.existsByUsername("test")).thenReturn(false);

        UserDto userDto = service.register(registerDto);

        assertEquals("test",userDto.getUsername(),"should have the same username");
        assertTrue(encoder.matches("tester123",userDto.getPassword()));
        assertEquals(Role.ADMIN,userDto.getRole(),"should have the same role");

        verify(repo,atLeast(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Register User Test: Success as user with any email")
    void registerUserTest_SuccessAsUser(){
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("test");
        registerDto.setPassword("tester123");
        registerDto.setEmail("test@gmail.com");
        when(repo.existsByUsername("test")).thenReturn(false);

        UserDto userDto = service.register(registerDto);

        assertEquals("test",userDto.getUsername(),"should have the same username");
        assertTrue(encoder.matches("tester123",userDto.getPassword()));
        assertEquals(Role.USER,userDto.getRole(),"should have the same role");

        verify(repo,atLeast(1)).save(any(User.class));
    }


    @Test
    @DisplayName("Register User Test: Fail, username already exists should throw an exception")
    void registerUserTest_FailShouldThrowException(){
        RegisterDto registerDto = mock(RegisterDto.class);
        when(registerDto.getUsername()).thenReturn("tester");
        when(repo.existsByUsername("tester")).thenReturn(true);
        assertThrows(RuntimeException.class, ()->{
            service.register(registerDto);
        });
    }


    @Test
    @DisplayName("Get Users Test")
    void getUsersTest(){
        User user1 = mock(User.class);
        User user2 = mock(User.class);

        when(repo.findAll()).thenReturn(List.of(mockUser,user2,user1));

        List<UserDto> dtos = service.getUsers();

        assertEquals(3,dtos.size(),"should have a number of 3 Users");

    }

    @Test
    @DisplayName("Get Users Test: with no users present")
    void getUsersTestWithNoUsers(){
        when(repo.findAll()).thenReturn(List.of());

        List<UserDto> dtos = service.getUsers();

        assertEquals(0,dtos.size(),"should have no users");

    }


    @Test
    @DisplayName("Delete User test:")
    void deleteUserTest_Success(){
        when(repo.findByUsername("tester")).thenReturn(Optional.of(mockUser));

        boolean verify = service.deleteUser("tester");

        assertTrue(verify);

        verify(repo,atLeast(1)).delete(any(User.class));
    }

    @Test
    @DisplayName("Delete User Test: Fail, should throw an exception")
    void deleteUserTest_FailShouldThrowException(){
        Exception ex = assertThrows(UserNotFoundException.class,()->{
            service.deleteUser("tester");
        });

        assertTrue(ex.getMessage().contains("tester"));
    }

    @Test
    @DisplayName("Load User by Username Test: Success")
    void loadUserByUsernameTest_Success(){
        when(repo.findByUsername("tester")).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = service.loadUserByUsername("tester");

        assertNotNull(userDetails);
        assertEquals("tester",userDetails.getUsername());
        assertEquals("tester123",userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a-> a.getAuthority().equals("ROLE_USER")));
    }


}
