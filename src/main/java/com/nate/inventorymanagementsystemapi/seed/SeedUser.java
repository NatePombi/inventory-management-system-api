package com.nate.inventorymanagementsystemapi.seed;

import com.nate.inventorymanagementsystemapi.model.Role;
import com.nate.inventorymanagementsystemapi.model.User;
import com.nate.inventorymanagementsystemapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.beans.Encoder;

@Component
@AllArgsConstructor
public class SeedUser implements CommandLineRunner {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    @Override
    public void run(String... args) throws Exception {
        if(repo.findByUsername("admin").isEmpty()){
            User admin = new User();
            admin.setUsername("admin");
            admin.setRole(Role.ADMIN);
            admin.setPassword(encoder.encode("admin123"));

            repo.save(admin);
            System.out.println("Default admin created: Username = admin and password = admin123");
        }
    }
}
