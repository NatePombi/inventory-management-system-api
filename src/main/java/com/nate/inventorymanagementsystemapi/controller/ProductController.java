package com.nate.inventorymanagementsystemapi.controller;

import com.nate.inventorymanagementsystemapi.dto.PaginatedResponse;
import com.nate.inventorymanagementsystemapi.dto.PostProduct;
import com.nate.inventorymanagementsystemapi.dto.ProductDto;
import com.nate.inventorymanagementsystemapi.model.CustomerDetails;
import com.nate.inventorymanagementsystemapi.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Product Controller", description = "End points for managing products")
@RestController
@RequestMapping("/product")
@AllArgsConstructor
public class ProductController {

    private IProductService service;

    @Operation(summary = "Create new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "product created , returns product"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody @Valid PostProduct dto, @AuthenticationPrincipal CustomerDetails customerDetails){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addProduct(dto, customerDetails.getUsername()));
    }

    @Operation(summary = "Retrieves product by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product was returned"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id, @AuthenticationPrincipal CustomerDetails details){
        return ResponseEntity.ok(service.getProduct(id, details.getUsername()));
    }

    @Operation(summary = "Updates Product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product was returned"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductDto dto, @AuthenticationPrincipal CustomerDetails details){
        return ResponseEntity.ok(service.udpateProduct(id,dto,details.getUsername()));
    }


    @Operation(summary = "Deletes Product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product was returned"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteProd(@PathVariable Long id, @AuthenticationPrincipal CustomerDetails customerDetails){
        return ResponseEntity.ok(service.deleteProduct(id, customerDetails.getUsername()));
    }


    @GetMapping
    public ResponseEntity<PaginatedResponse<ProductDto>> getAllProductsByUser(@AuthenticationPrincipal CustomerDetails customerDetails,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "5") int size,
                                                                  @RequestParam(defaultValue = "name") String sortBy,
                                                                  @RequestParam(defaultValue = "asc") String direction,
                                                                  @RequestParam(required = false) String search){

        Page<ProductDto> productDtoPage = service.getAllUserProductsByUsername(customerDetails.getUsername(),page,size,sortBy,direction,search);

        PaginatedResponse<ProductDto> response = new PaginatedResponse<>(
                productDtoPage.getContent(),
                productDtoPage.getNumber(),
                productDtoPage.getTotalPages(),
                productDtoPage.getTotalElements(),
                productDtoPage.isLast()
        );

        return ResponseEntity.ok().body(response);

    }
}
