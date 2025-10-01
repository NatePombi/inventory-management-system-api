package com.nate.inventorymanagementsystemapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nate.inventorymanagementsystemapi.dto.PostProduct;
import com.nate.inventorymanagementsystemapi.dto.ProductDto;
import com.nate.inventorymanagementsystemapi.exception.ProductNotFoundException;
import com.nate.inventorymanagementsystemapi.model.CustomerDetails;
import com.nate.inventorymanagementsystemapi.security.JwtFilterAuth;
import com.nate.inventorymanagementsystemapi.service.IProductService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(ProductControllerTest.MockConfig.class)
@AutoConfigureMockMvc(addFilters = true)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@WithMockUser(username = "tester", roles = "USER")
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IProductService service;

    @Autowired
    private ObjectMapper mapper;

    @Mock
    private CustomerDetails details;

    @Autowired
    private JwtFilterAuth jwtFilterAuth;

    @BeforeEach
    void startUp() throws ServletException, IOException {
        SecurityContextHolder.clearContext();


        details = mock(CustomerDetails.class);
        Mockito.when(details.getUsername()).thenReturn("tester");

        Mockito.doAnswer(invocationOnMock -> {
            HttpServletRequest request = invocationOnMock.getArgument(0);
            HttpServletResponse response = invocationOnMock.getArgument(1);
            FilterChain filterChain = invocationOnMock.getArgument(2);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(details, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request,response);
            return null;
        }).when(jwtFilterAuth).doFilter(any(),any(),any());
    }


    @DisplayName("Testing Create Product: All results")
    @Nested
    class CreateProdTest {
        @Test
        void testCreateProduct() throws Exception {
            PostProduct postProduct = new PostProduct("TV", 3, BigDecimal.valueOf(100));
            ProductDto saved = new ProductDto(4L, "TV", 3, BigDecimal.valueOf(100), 3L);

            Mockito.when(service.addProduct(any(), Mockito.eq("tester"))).thenReturn(saved);

            mockMvc.perform(post("/product")
                            .header("Authorization", "Bearer fake-jwt-token")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(postProduct)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(4))
                    .andExpect(jsonPath("$.name").value("TV"))
                    .andExpect(jsonPath("$.quantity").value(3))
                    .andExpect(jsonPath("$.price").value(100))
                    .andExpect(jsonPath("$.userId").value(3));

        }

        @Test
        void testCreateProduct_Fail() throws Exception {
            Mockito.when(service.addProduct(any(), anyString())).thenThrow(new AccessDeniedException("Forbidden"));

            PostProduct postProduct = new PostProduct("TV", 3, BigDecimal.valueOf(100));


            mockMvc.perform(post("/product")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(postProduct)))
                    .andExpect(status().isForbidden());

        }

        @Test
        void testCreateProduct_FailBadRequestNoProductName() throws Exception {
            PostProduct postProduct = new PostProduct();
            postProduct.setQuantity(1);
            postProduct.setPrice(BigDecimal.valueOf(120));

            mockMvc.perform(post("/product")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(postProduct)))
                    .andExpect(status().isBadRequest());

        }

        @Test
        void testCreateProduct_FailBadRequestNoPrice() throws Exception {
            PostProduct postProduct = new PostProduct();
            postProduct.setName("TV");
            postProduct.setQuantity(1);

            mockMvc.perform(post("/product")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(postProduct)))
                    .andExpect(status().isBadRequest());

        }


        @Test
        void testCreateProduct_FailBadRequestNoQuantity() throws Exception {
            PostProduct postProduct = new PostProduct();
            postProduct.setName("TV");
            postProduct.setPrice(BigDecimal.valueOf(100));
            mockMvc.perform(post("/product")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(postProduct)))
                    .andExpect(status().isBadRequest());

        }



    }

    @DisplayName("Testing Get Product By ID: All results")
    @Nested
    class TestGetProductByID {
        @Test
        void testGetProductByID() throws Exception {
            ProductDto dto = new ProductDto(2L, "Laptop", 4, new BigDecimal("100"), 1L);

            Mockito.when(service.getProduct(2L,details.getUsername())).thenReturn(dto);

            mockMvc.perform(get("/product/2")
                            .header("Authorization", "Bearer fake-jwt-token")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(2))
                    .andExpect(jsonPath("$.name").value("Laptop"))
                    .andExpect(jsonPath("$.quantity").value(4))
                    .andExpect(jsonPath("$.price").value("100"))
                    .andExpect(jsonPath("$.userId").value(1));
        }


        @Test
        void testGetProductByID_FailBadRequest() throws Exception {

            mockMvc.perform(get("/product/not-valid-id")
                            .with(csrf()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testGetProductByID_FailAccessDenied() throws Exception {
            Mockito.when(service.getProduct(1L,details.getUsername())).thenThrow(new AccessDeniedException("Forbidden"));
            mockMvc.perform(get("/product/1")
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }


    @DisplayName("Testing Update Product: All Results")
    @Nested
    class UpdateProductTests {
        @Test
        void testUpdateProduct_Success() throws Exception {
            ProductDto productDto = new ProductDto(11L, "Monitor", 2, BigDecimal.valueOf(900), 2L);

            Mockito.when(service.udpateProduct(eq(11L), any(ProductDto.class),eq("tester"))).thenReturn(productDto);

            mockMvc.perform(patch("/product/11")
                            .with(csrf())
                            .header("Authorization", "Bearer fake-jwt-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(productDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(11))
                    .andExpect(jsonPath("$.name").value("Monitor"))
                    .andExpect(jsonPath("$.quantity").value(2))
                    .andExpect(jsonPath("$.price").value(900))
                    .andExpect(jsonPath("$.userId").value(2));
        }

        @Test
        void testUpdateProduct_FailBadRequest() throws Exception {

            mockMvc.perform(patch("/product/not-valid-id")
                            .with(csrf()))
                    .andExpect(status().isBadRequest());
        }


        @Test
        void testUpdateProduct_FailAccessDenied() throws Exception {
            Mockito.when(service.udpateProduct(eq(1L), any(ProductDto.class),eq("tester"))).thenThrow(new AccessDeniedException("Forbidden"));
            ProductDto productDto = new ProductDto(1L, "Monitor", 2, BigDecimal.valueOf(900), 2L);

            mockMvc.perform(patch("/product/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(productDto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void testUpdateProduct_FailNotFound() throws Exception {
            ProductDto productDto = new ProductDto(2L, "Monitor", 2, BigDecimal.valueOf(900), 2L);
            Mockito.when(service.udpateProduct(eq(2L), any(ProductDto.class),anyString())).thenThrow(new ProductNotFoundException(2L));

            mockMvc.perform(patch("/product/2")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(productDto)))
                    .andExpect(status().isNotFound());
        }
    }



    @DisplayName("Testing Delete Product: All results")
    @Nested
    class DeleteProductTests {
        @Test
        void testDeleteProduct_Success() throws Exception {
            Mockito.when(service.deleteProduct(eq(11L), Mockito.eq("tester"))).thenReturn(true);

            mockMvc.perform(delete("/product/11")
                            .with(csrf())
                            .header("Authorization", "Bearer fake-jwt-token"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        void testDeleteProduct_FailBadRequest() throws Exception {
            mockMvc.perform(delete("/product/no-valid-id")
                            .with(csrf()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testDeleteProduct_FailProductNotFound() throws Exception {
            Mockito.when(service.deleteProduct(eq(12L), Mockito.eq("tester"))).thenThrow(new ProductNotFoundException(12L));
            mockMvc.perform(delete("/product/12")
                            .with(csrf()))
                    .andExpect(status().isNotFound());
        }

        @Test
        void testDeleteProduct_FailAccessDenied() throws Exception {
            Mockito.when(service.deleteProduct(eq(12L), Mockito.eq("tester"))).thenThrow(new AccessDeniedException("Forbidden"));

            mockMvc.perform(delete("/product/12")
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }




        @TestConfiguration
        static class MockConfig {
            @Bean
            IProductService productService() {
                return mock(IProductService.class);
            }

            @Bean
            JwtFilterAuth jwtFilterAuth() {
                return mock(JwtFilterAuth.class);
            }
        }

}
