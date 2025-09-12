package com.nate.inventorymanagementsystemapi.repository;

import com.nate.inventorymanagementsystemapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
}
