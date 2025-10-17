package com.nate.inventorymanagementsystemapi.repository;

import com.nate.inventorymanagementsystemapi.model.Product;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
    Page<Product> findByUserUsername(String username, Pageable pageable);

}
