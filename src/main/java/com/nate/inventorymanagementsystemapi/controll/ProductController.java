package com.nate.inventorymanagementsystemapi.controll;

import com.nate.inventorymanagementsystemapi.dto.PostProduct;
import com.nate.inventorymanagementsystemapi.dto.ProductDto;
import com.nate.inventorymanagementsystemapi.model.CustomerDetails;
import com.nate.inventorymanagementsystemapi.service.IProductService;
import com.nate.inventorymanagementsystemapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
            @ApiResponse(responseCode = "404", description = "Not Found")
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
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id){
        return ResponseEntity.ok(service.getProduct(id));
    }

    @Operation(summary = "Updates Product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product was returned"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductDto dto){
        return ResponseEntity.ok(service.udpateProduct(id,dto));
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
}
