package com.nate.inventorymanagementsystemapi.repository;

import com.nate.inventorymanagementsystemapi.model.Product;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

public interface ProductRepository extends JpaRepository<Product,Long> {
    Page<Product> findByUserUsername(String username, Pageable pageable);


    @Query("""
            SELECT p FROM Product p
            WHERE p.user.username = :username
            AND LOWER(p.name) LIKE LOWER(CONCAT('%',:search,'%'))
        """)
    Page<Product> searchProductByUserAndName(@RequestParam("username") String username, @RequestParam("search") String search, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String search, Pageable pageable);
}
