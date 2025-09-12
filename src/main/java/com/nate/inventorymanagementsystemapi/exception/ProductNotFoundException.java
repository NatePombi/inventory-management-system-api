package com.nate.inventorymanagementsystemapi.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String name) {
        super("Product with name " + name + " is not found");
    }

    public ProductNotFoundException(Long id) {
        super("Product with ID " + id + " was not found");
    }
}
