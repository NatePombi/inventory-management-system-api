package com.nate.inventorymanagementsystemapi.controll;

import com.nate.inventorymanagementsystemapi.dto.JwtResponse;
import com.nate.inventorymanagementsystemapi.dto.LoginDto;
import com.nate.inventorymanagementsystemapi.dto.RegisterDto;
import com.nate.inventorymanagementsystemapi.dto.UserDto;
import com.nate.inventorymanagementsystemapi.model.User;
import com.nate.inventorymanagementsystemapi.service.IUserService;
import com.nate.inventorymanagementsystemapi.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "User Controller", description = "End points for managing Users")
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class UserController {

    private final IUserService service;

    @Operation(summary = "Registering User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered, returns success message"),
            @ApiResponse(responseCode =  "400", description = "Bad Request")
    })

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid RegisterDto registerDto){
        service.register(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Successfully registered");
    }

    @Operation(summary = "Log in User")
    @ApiResponses(value = {
            @ApiResponse( responseCode = "200" , description = " User logged in, returns success message"),
            @ApiResponse( responseCode = "400" , description = "Bad Request"),
            @ApiResponse( responseCode = "404" , description = "User Not Found")
    })

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid LoginDto loginDto){
        try{

            UserDto user = service.getByUsername(loginDto.getUsername());

            String token = JwtUtil.generateToken(user.getUsername(),user.getRole());

            return ResponseEntity.ok(new JwtResponse(token));
        }
        catch (AuthenticationException e){
            return ResponseEntity.status(401).body("Invalid Username or Password");
        }
    }

    @Operation(summary = "Getting User by Username")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "returns specified user"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")

    })

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username){
        return ResponseEntity.ok(service.getByUsername(username));
    }

    @Operation(summary = "Getting all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "returns a list of users"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(){
        return ResponseEntity.ok(service.getUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{username}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable String username){
        return ResponseEntity.ok(service.deleteUser(username));
    }

}
