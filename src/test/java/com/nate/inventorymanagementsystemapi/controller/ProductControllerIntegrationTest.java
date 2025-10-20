package com.nate.inventorymanagementsystemapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nate.inventorymanagementsystemapi.dto.PostProduct;
import com.nate.inventorymanagementsystemapi.dto.ProductDto;
import com.nate.inventorymanagementsystemapi.dto.RegisterDto;
import com.nate.inventorymanagementsystemapi.model.Product;
import com.nate.inventorymanagementsystemapi.model.Role;
import com.nate.inventorymanagementsystemapi.model.User;
import com.nate.inventorymanagementsystemapi.repository.ProductRepository;
import com.nate.inventorymanagementsystemapi.repository.UserRepository;
import com.nate.inventorymanagementsystemapi.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private ProductRepository repository;


    private User testUser;
    private User testUser2;


    private String token;

    private Product testProduct;
    private Product testProduct2;



    @BeforeEach
    void startUp() {

        testUser = new User();
        testUser.setUsername("tester");
        testUser.setRole(Role.USER);
        testUser.setPassword(encoder.encode("tester123"));

        testUser2 = new User();
        testUser2.setUsername("tester2");
        testUser2.setRole(Role.ADMIN);
        testUser2.setPassword(encoder.encode("tester123"));

        repo.save(testUser);
        repo.save(testUser2);

        testProduct = new Product();
        testProduct.setQuantity(4);
        testProduct.setUser(testUser);
        testProduct.setPrice(BigDecimal.valueOf(300));
        testProduct.setName("Laptop");

        testProduct2 = new Product();
        testProduct2.setQuantity(4);
        testProduct2.setPrice(BigDecimal.valueOf(300));
        testProduct2.setName("Laptop");
        testProduct2.setUser(testUser2);

        repository.save(testProduct);
        repository.save(testProduct2);

         token = JwtUtil.generateToken(testUser.getUsername(),testUser.getRole());


    }



    @DisplayName("Testing Create Product: All results")
    @Nested
    class CreateProdTest {
        @Test
        void testCreateProduct() throws Exception {
            PostProduct postProduct = new PostProduct("TV", 3, BigDecimal.valueOf(100));


            mvc.perform(post("/product")
                            .header("Authorization", "Bearer "+ token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(postProduct)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("TV"))
                    .andExpect(jsonPath("$.quantity").value(3))
                    .andExpect(jsonPath("$.price").value(100));

            assertTrue(repository.findById(3L).isPresent());
        }

        @Test
        void testCreateProduct_FailBadRequest() throws Exception {
            PostProduct postProduct = new PostProduct();
            postProduct.setPrice(BigDecimal.valueOf(100));
            postProduct.setName("TV");

            mvc.perform(post("/product")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(postProduct)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testCreateProduct_FailNoAccess() throws Exception {
            PostProduct postProduct = new PostProduct("TV", 3, BigDecimal.valueOf(100));

            mvc.perform(post("/product")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(postProduct)))
                    .andExpect(status().isUnauthorized());

        }

        @Test
        void testCreateProduct_FailInvalidToken() throws Exception {
            PostProduct postProduct = new PostProduct("TV", 3, BigDecimal.valueOf(100));


            mvc.perform(post("/product")
                            .header("Authorization", "Bearer Invalid-Token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(postProduct)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("Testing Get Product By ID: All results")
    @Nested
    class TestGetProductByID {
        @Test
        void testGetProductByID() throws Exception {
            mvc.perform(get("/product/1")
                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Laptop"))
                    .andExpect(jsonPath("$.price").value(300))
                    .andExpect(jsonPath("$.quantity").value(4));
        }


        @Test
        void testGetProductByID_FailNotFound() throws Exception {
            mvc.perform(get("/product/5")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        void testGetProductByID_FailBadRequest() throws Exception {
            mvc.perform(get("/product/Not-Valid-ID")
                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testGetProductByID_Unauthorized() throws Exception {
            mvc.perform(get("/product/2")
                    .header("Authorization", "Bearer "+token))
                    .andExpect(status().isForbidden());
        }

        @Test
        void testGetProductById_SuccessAdmin() throws Exception {
            String token2 = JwtUtil.generateToken(testUser2.getUsername(),testUser2.getRole());
            mvc.perform(get("/product/1")
                    .header("Authorization", "Bearer " + token2))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Laptop"))
                    .andExpect(jsonPath("$.price").value(300))
                    .andExpect(jsonPath("$.quantity").value(4));
        }
    }

    @DisplayName("Testing Get All Products of User: All results")
    @Nested
    class TestGetAllUserProduct {
        @Test
        void testGetAllUserProduct_Success() throws Exception {
            mvc.perform(get("/product")
                    .param("page","0")
                    .param("size","5")
                    .param("sortBy","name")
                    .param("direction","desc")
                    .header("Authorization", "Bearer "+token))
                    .andExpect(status().isOk());
        }

        @Test
        void testGetAllUserProduct_SuccessSearchByName() throws Exception {
            mvc.perform(get("/product")
                            .param("page","0")
                            .param("size","5")
                            .param("sortBy","name")
                            .param("direction","desc")
                            .param("search","Laptop")
                            .header("Authorization", "Bearer "+token))
                    .andExpect(status().isOk());
        }

        @Test
        void testGetAllUserProduct_SuccessSearchByNameAdmin() throws Exception {
            String token2 = JwtUtil.generateToken(testUser2.getUsername(),testUser2.getRole());
            mvc.perform(get("/product")
                            .param("page","0")
                            .param("size","5")
                            .param("sortBy","name")
                            .param("direction","desc")
                            .param("search","Laptop")
                            .header("Authorization", "Bearer "+token2))
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("Testing Update Product: All Results")
    @Nested
    class UpdateProductTests {
        @Test
        void testUpdateProduct_Success() throws Exception {
            PostProduct postProduct = new PostProduct("Laptop",11,BigDecimal.valueOf(700));

            mvc.perform(patch("/product/1")
                    .header("Authorization", "Bearer "+token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(postProduct)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Laptop"))
                    .andExpect(jsonPath("$.quantity").value(11))
                    .andExpect(jsonPath("$.price").value(700));
        }


        @Test
        void testUpdateProduct_FailBadRequest() throws Exception {
            mvc.perform(patch("/product/Invalid-id")
                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testUpdateProduct_FailNotFound() throws Exception {
            PostProduct postProduct = new PostProduct("Laptop",11,BigDecimal.valueOf(700));

            mvc.perform(patch("/product/3")
                            .header("Authorization", "Bearer "+token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(postProduct)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void testUpdateProduct_FailUnauthorized() throws Exception {
            PostProduct postProduct = new PostProduct("Laptop",11,BigDecimal.valueOf(700));


            mvc.perform(patch("/product/2")
                            .header("Authorization", "Bearer "+token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(postProduct)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void testUpdateProduct_SuccessAdmin() throws Exception {
            PostProduct postProduct = new PostProduct("Laptop",11,BigDecimal.valueOf(700));
            String token2 = JwtUtil.generateToken(testUser2.getUsername(),testUser2.getRole());

            mvc.perform(patch("/product/1")
                            .header("Authorization", "Bearer "+token2)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(postProduct)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Laptop"))
                    .andExpect(jsonPath("$.quantity").value(11))
                    .andExpect(jsonPath("$.price").value(700));
        }
    }


    @DisplayName("Testing Delete Product: All results")
    @Nested
    class DeleteProductTests {
        @Test
        void testDeleteProduct_Success() throws Exception {

            mvc.perform(delete("/product/1")
                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));

            assertFalse(repository.findById(1L).isPresent());

        }

        @Test
        void testDeleteProduct_FailNotFound() throws Exception {

            mvc.perform(delete("/product/4")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound());

        }

        @Test
        void testDeleteProduct_FailBadRequest() throws Exception {

            mvc.perform(delete("/product/Invalid-ID")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isBadRequest());

        }

        @Test
        void testDeleteProduct_FailUnauthorized() throws Exception {
            mvc.perform(delete("/product/2")
                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isForbidden());
        }

        @Test
        void testDeleteProduct_SuccessAdmin() throws Exception {
            String token2 = JwtUtil.generateToken(testUser2.getUsername(),testUser2.getRole());
            mvc.perform(delete("/product/1")
                            .header("Authorization", "Bearer " + token2))
                    .andExpect(status().isOk());

            assertFalse(repository.findById(1L).isPresent());
        }
    }
}
