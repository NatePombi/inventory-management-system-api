package com.nate.inventorymanagementsystemapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nate.inventorymanagementsystemapi.dto.JwtResponse;
import com.nate.inventorymanagementsystemapi.dto.LoginDto;
import com.nate.inventorymanagementsystemapi.dto.RegisterDto;
import com.nate.inventorymanagementsystemapi.dto.UserDto;
import com.nate.inventorymanagementsystemapi.exception.UserNotFoundException;
import com.nate.inventorymanagementsystemapi.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerIntegrationTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private LoginDto dtoLog;
    private RegisterDto dtoReg;


    @BeforeEach
    void startUp() throws Exception {
        dtoReg = new RegisterDto("tester", "admin@gmail.com", "test123");
        dtoLog = new LoginDto("tester","test123");

    }

    private void reg() throws Exception {
        //Keeps a User constantly registered for the login tests
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dtoReg)))
                .andExpect(status().isCreated());
    }

    @DisplayName("Test Register: All results")
    @Nested
    class RegisterTests {
        @Test
        void testRegisterUser_Success() throws Exception {
            RegisterDto reg = new RegisterDto("tester", "admin@gmail.com", "test123");
            mvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(reg)))
                    .andExpect(status().isCreated());
        }

        @Test
        void testRegisterUser_FailBadRequestNoUsername() throws Exception {
            RegisterDto reg = new RegisterDto();
            reg.setPassword("123Pass");
            reg.setEmail("tester@gmail.com");
            mvc.perform(post("/auth/register")
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
            mvc.perform(post("/auth/register")
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
            mvc.perform(post("/auth/register")
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
            reg();
            mvc.perform(post("/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dtoLog)))
                    .andExpect(status().isOk());
        }

        @Test
        void testLogin_FailBadRequestNoUsername() throws Exception {
            LoginDto log = new LoginDto();
            log.setPassword("tester123");
            mvc.perform(post("/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(log)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testLogin_FailBadRequestNoPassword() throws Exception {
            LoginDto log = new LoginDto();
            log.setUsername("tester");
            mvc.perform(post("/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(log)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testLogin_FailUserNotFound() throws Exception {
            LoginDto log = new LoginDto("testing","tes");
            mvc.perform(post("/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(log)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void testLogin_FailWrongPassword() throws Exception {
            reg();
            dtoLog.setPassword("testing");
            mvc.perform(post("/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dtoLog)))
                    .andExpect(status().isUnauthorized());
        }
    }

}
