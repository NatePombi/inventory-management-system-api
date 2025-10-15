package com.nate.inventorymanagementsystemapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nate.inventorymanagementsystemapi.dto.JwtResponse;
import com.nate.inventorymanagementsystemapi.dto.LoginDto;
import com.nate.inventorymanagementsystemapi.dto.RegisterDto;
import com.nate.inventorymanagementsystemapi.dto.UserDto;
import com.nate.inventorymanagementsystemapi.exception.UserNotFoundException;
import com.nate.inventorymanagementsystemapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
@Import(UserControllerTest.MockConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private UserService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private LoginDto dtoLog;


    @BeforeEach
    void startUp(){
        dtoLog = new LoginDto("tester","test123");
    }

    @DisplayName("Test Register: All results")
    @Nested
    class RegisterTests {
        @Test
        void testRegisterUser_Success() throws Exception {
            RegisterDto registerDto = new RegisterDto("tester", "admin@gmail.com", "test123");
            UserDto dto = new UserDto();
            Mockito.when(service.register(registerDto)).thenReturn(dto);

            mockMvc.perform(post("/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(registerDto)))
                    .andExpect(status().isCreated());
        }

        @Test
        void testRegisterUser_FailBadRequestNoUsername() throws Exception {
            RegisterDto reg = new RegisterDto();
            reg.setPassword("123Pass");
            reg.setEmail("tester@gmail.com");
            mockMvc.perform(post("/auth/register")
                    .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(reg)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testRegisterUser_FailBadRequestNoPassword() throws Exception {
            RegisterDto reg = new RegisterDto();
            reg.setUsername("tester");
            reg.setEmail("tester@gmail.com");
            mockMvc.perform(post("/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(reg)))
                    .andExpect(status().isBadRequest());
        }


        @Test
        void testRegisterUser_FailBadRequestEmail() throws Exception {
            RegisterDto reg = new RegisterDto();
            reg.setUsername("tester");
            reg.setPassword("123Pass");
            mockMvc.perform(post("/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(reg)))
                    .andExpect(status().isBadRequest());
        }

    }

    @DisplayName("Test Login: All results")
    @Nested
    class LoginTests{

        @Test
        void testLogin_Success() throws Exception {
            JwtResponse response = new JwtResponse("fake-token");
            Mockito.when(service.login(any(LoginDto.class))).thenReturn(response);

            mockMvc.perform(post("/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(dtoLog)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("fake-token"));

        }

        @Test
        void testLogin_FailBadRequestNoUsername() throws Exception {
           LoginDto log = new LoginDto();
           log.setPassword("tester123");
            mockMvc.perform(post("/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(log)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testLogin_FailBadRequestNoPassword() throws Exception {
            LoginDto log = new LoginDto();
            log.setUsername("tester");
            mockMvc.perform(post("/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(log)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testLogin_FailUserNotFound() throws Exception {
            Mockito.when(service.login(any(LoginDto.class))).thenThrow(new UserNotFoundException("Not Found"));

            mockMvc.perform(post("/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(dtoLog)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void testLogin_FailWrongPassword() throws Exception {
            Mockito.when(service.login(any(LoginDto.class))).thenThrow(new RuntimeException());

            mockMvc.perform(post("/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dtoLog)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("Get User test: All Results")
    @Nested
    class GetUserTests{

        @Test
        void testGetUserByUsername_Success() throws Exception {
            UserDto dto = new UserDto();
            Mockito.when(service.getByUsername(anyString())).thenReturn(dto);

            mockMvc.perform(get("/auth/tester")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());
        }


        @Test
        void testGetUserByUsername_FailUserNotFound() throws Exception {
            Mockito.when(service.getByUsername("tester")).thenThrow(new UserNotFoundException("tester"));

            mockMvc.perform(get("/auth/tester")
                    .with(csrf()))
                    .andExpect(status().isNotFound());
        }


    }

    @DisplayName("Get All User test: All Results")
    @Nested
    class GetUsersTests{

        @Test
        void testGetUsers_Success() throws Exception {
            Mockito.when(service.getUsers()).thenReturn(List.of());

            mockMvc.perform(get("/auth")
                            .with(csrf()))
                    .andExpect(status().isOk());
        }


    }

    @DisplayName("Delete User test: All Results")
    @Nested
    class DeleteUserTests{

        @Test
        void testDeleteUser_Success() throws Exception {
            Mockito.when(service.deleteUser(anyString())).thenReturn(true);
            mockMvc.perform(delete("/auth/tester")
                            .with(csrf()))
                    .andExpect(status().isOk());
        }

        @Test
        void testDeleteUser_FailUserNotFound() throws Exception {
            Mockito.when(service.deleteUser(anyString())).thenThrow(new UserNotFoundException("tester"));
            mockMvc.perform(delete("/auth/tester")
                            .with(csrf()))
                    .andExpect(status().isNotFound());
        }


    }





    @TestConfiguration
    static class MockConfig{
        @Bean
        UserService userService(){
            return mock(UserService.class);
        }
    }

}
