package com.nate.inventorymanagementsystemapi.repository;

import com.nate.inventorymanagementsystemapi.model.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(@NotBlank(message = "Username required") String username);
}
