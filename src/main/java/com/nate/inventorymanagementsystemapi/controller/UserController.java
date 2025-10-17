package com.nate.inventorymanagementsystemapi.controller;

import com.nate.inventorymanagementsystemapi.dto.*;
import com.nate.inventorymanagementsystemapi.exception.UserNotFoundException;
import com.nate.inventorymanagementsystemapi.model.User;
import com.nate.inventorymanagementsystemapi.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
       try {
           return ResponseEntity.ok(service.login(loginDto));
       }
       catch (UserNotFoundException ex){
           return ResponseEntity.status(404).body(ex.getMessage());
       }
       catch (RuntimeException ex){
           return ResponseEntity.status(401).body(ex.getMessage());
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
    public ResponseEntity<PaginatedResponse<UserDto>> getUsers(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "5") int size,
                                                  @RequestParam(defaultValue = "name") String sortBy,
                                                  @RequestParam(defaultValue = "desc") String direction){
        Page<UserDto> userDtoPage = service.getUsers(page, size, sortBy, direction);

        PaginatedResponse<UserDto> response = new PaginatedResponse<>(
              userDtoPage.getContent(),
              userDtoPage.getNumber(),
              userDtoPage.getTotalPages(),
                userDtoPage.getTotalElements(),
                userDtoPage.isFirst()
        );


        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{username}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable String username){
        return ResponseEntity.ok(service.deleteUser(username));
    }

}
