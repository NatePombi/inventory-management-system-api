package com.nate.inventorymanagementsystemapi.service;

import com.nate.inventorymanagementsystemapi.dto.PostProduct;
import com.nate.inventorymanagementsystemapi.dto.ProductDto;
import com.nate.inventorymanagementsystemapi.dto.UserDto;
import com.nate.inventorymanagementsystemapi.exception.ProductNotFoundException;
import com.nate.inventorymanagementsystemapi.exception.UserNotFoundException;
import com.nate.inventorymanagementsystemapi.model.Product;
import com.nate.inventorymanagementsystemapi.model.Role;
import com.nate.inventorymanagementsystemapi.model.User;
import com.nate.inventorymanagementsystemapi.repository.ProductRepository;
import com.nate.inventorymanagementsystemapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.convert.DataSizeUnit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository repo;
    @Mock
    private UserRepository repoU;

    private IProductService service;

    private Product mockProduct;
    private User mockUser;

    @BeforeEach
    void startUp(){
        service = new ProductService(repo,repoU);

        mockProduct = new Product();
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("Tester");
        mockUser.setRole(Role.USER);

        mockProduct.setUser(mockUser);
    }

    @Test
    @DisplayName("Get all User Product by Username Test: Success")
    void testGetAllUserProductByUsername_Success(){
        Product prod = mock(Product.class);
        when(prod.getUser()).thenReturn(mockUser);
        when(repo.findAll()).thenReturn(List.of(mockProduct,prod));
        when(repoU.findByUsername("Tester")).thenReturn(Optional.of(mockUser));

        List<ProductDto> productDtos = service.getAllUserProductsByUsername("Tester");

        assertNotNull(productDtos);

        assertEquals(2, productDtos.size(),"Should have 2 products present");
    }

    @Test
    @DisplayName("Get all User Product by Username Test: No Items present")
    void testGetAllUserProductByUsername_NoItemsPresent(){

        when(repoU.findByUsername("Tester")).thenReturn(Optional.of(mockUser));
        List<ProductDto> productDtos = service.getAllUserProductsByUsername("Tester");

        assertTrue(productDtos.isEmpty(),"Should be empty");
    }

    @Test
    @DisplayName("Add Product Test")
    void testAddProduct_Success(){
        PostProduct post = new PostProduct("TV",2, new BigDecimal("2000"));
        mockProduct.setName(post.getName());
        mockProduct.setPrice(post.getPrice());
        mockProduct.setQuantity(post.getQuantity());

        when(repoU.findByUsername("Tester")).thenReturn(Optional.of(mockUser));
        when(repo.save(any(Product.class))).thenReturn(mockProduct);
        ProductDto prod = service.addProduct(post,"Tester");

        assertEquals("TV",prod.getName(),"Should be TV");
        assertEquals(2,prod.getQuantity(),"should have a quantity of 2");
        assertEquals(new BigDecimal("2000"),prod.getPrice(),"Should have the same price");
        assertEquals(mockUser.getId(),prod.getUserId());
    }

    @Test
    @DisplayName("Add Product Test: Fail, User Not Found")
    void testAddProduct_FailShouldThrowException(){

       Exception ex = assertThrows(UserNotFoundException.class,()->{
            service.addProduct(new PostProduct("TV",2,new BigDecimal("1000")),"Tester");
        });

       assertTrue(ex.getMessage().contains("Tester"));
    }

    @Test
    @DisplayName("Get Product Test: Success")
    void testGetProduct_Success(){
        mockProduct.setName("Dryer");
        mockProduct.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(repoU.findByUsername("Tester")).thenReturn(Optional.of(mockUser));

        ProductDto prod = service.getProduct(1L,mockUser.getUsername());

        assertEquals(1L,prod.getId(),"should have the specified id");
        assertEquals("Dryer",prod.getName(),"should have the same name");

    }


    @Test
    @DisplayName("Get Product Test: Fail")
    void testGetProduct_FailProductNotFound(){
        when(repoU.findByUsername("Tester")).thenReturn(Optional.of(mockUser));
        Exception ex = assertThrows(ProductNotFoundException.class,()->{
            service.getProduct(1L,mockUser.getUsername());
        });

        assertTrue(ex.getMessage().contains("1"));
    }

    @Test
    @DisplayName("Get Product Test: Fail")
    void testGetProduct_FailUserNotFound(){
        Exception ex = assertThrows(UserNotFoundException.class,()->{
            service.getProduct(1L,mockUser.getUsername());
        });

        assertTrue(ex.getMessage().contains(mockUser.getUsername()));
    }


    @Test
    @DisplayName("Delete Product Test: Success")
    void testDeleteProduct(){
        when(repoU.findByUsername("Tester")).thenReturn(Optional.of(mockUser));
        when(repo.findById(2L)).thenReturn(Optional.of(mockProduct));

        mockProduct.setId(2L);

        boolean verify = service.deleteProduct(2L,"Tester");

        assertTrue(verify);

    }

    @Test
    @DisplayName("Delete Product Test: Fail user not found, should throw an exception")
    void testDeleteProduct_FailUserNotFoundShouldThrowException(){
        Exception ex = assertThrows(UserNotFoundException.class,()->{
            service.deleteProduct(2L,"Tester");
        });

        assertTrue(ex.getMessage().contains("Tester"));
    }

    @Test
    @DisplayName("Delete Product Test: Fail product not found, should throw an exception")
    void testDeleteProduct_FailProductNotFoundShouldThrowException(){
        when(repoU.findByUsername("Tester")).thenReturn(Optional.of(mockUser));
        Exception ex = assertThrows(ProductNotFoundException.class,()->{
            service.deleteProduct(2L,"Tester");
        });

        assertTrue(ex.getMessage().contains("2"));
    }

    @Test
    @DisplayName("Update Product Test: Success")
    void testUpdateProduct_Success(){
        ProductDto dto = new ProductDto();
        dto.setName("PS5");
        dto.setQuantity(2);
        dto.setPrice(new BigDecimal("9000"));

        Product prod = new Product();
        prod.setId(3L);
        prod.setName(dto.getName());
        prod.setPrice(dto.getPrice());
        prod.setQuantity(dto.getQuantity());

        mockProduct.setId(3L);


        when(repo.findById(3L)).thenReturn(Optional.of(mockProduct));
        when(repo.save(any(Product.class))).thenReturn(prod);
        when(repoU.findByUsername("Tester")).thenReturn(Optional.of(mockUser));

        ProductDto updated = service.udpateProduct(3L,dto,mockUser.getUsername());

        assertEquals(3L,updated.getId());
        assertEquals("PS5",updated.getName());
        assertEquals(2,updated.getQuantity());
        assertEquals(new BigDecimal("9000"),updated.getPrice());

    }

    @Test
    @DisplayName("Update Product Test: Fail, Product Not Found")
    void testUpdateProduct_FailProductNotFoundShouldThrowException(){
        ProductDto dto = new ProductDto();
        when(repoU.findByUsername("Tester")).thenReturn(Optional.of(mockUser));

        Exception ex = assertThrows(ProductNotFoundException.class,()->{
            service.udpateProduct(3L,dto,mockUser.getUsername());
        });

        assertTrue(ex.getMessage().contains("3"));
    }

    @Test
    @DisplayName("Update Product Test: Fail, User Not Found")
    void testUpdateProduct_FailUserNotFoundShouldThrowException(){
        ProductDto dto = new ProductDto();

        Exception ex = assertThrows(UserNotFoundException.class,()->{
            service.udpateProduct(3L,dto,mockUser.getUsername());
        });

        assertTrue(ex.getMessage().contains(mockUser.getUsername()));
    }
}
