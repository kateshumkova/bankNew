package com.example.banknew.controller;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.ClientDto;
import com.example.banknew.dtos.ProductDto;
import com.example.banknew.dtos.TrxDto;
import com.example.banknew.enums.Status;
import com.example.banknew.enums.TrxType;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.exception.ValidationException;
import com.example.banknew.service.ClientService;
import com.example.banknew.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @Autowired
    public MockMvc mvc;
    @Autowired
    public ObjectMapper objectMapper;
    @MockBean
    public ProductService productService;


    @WithMockUser(roles = "MANAGER")
    @Test
    void getAll_shouldReturn200andFilledJson() throws Exception {
        when(productService.getAll()).thenReturn(List.of(new ProductDto(), new ProductDto()));
        mvc.perform(get("/api/product/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
    @WithMockUser(roles = "MANAGER")
    @Test
    void getAll_shouldReturn200withEmptyResponse() throws Exception {
        when(productService.getAll()).thenReturn(Collections.emptyList());
        mvc.perform(get("/api/product/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @WithMockUser(roles = "MANAGER")
    @Test
    void getByName_shouldReturn200() throws Exception {
        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setStatus(Status.ACTIVE);
        when(productService.findByName("Vklad")).thenReturn(List.of(productDto));
        mvc.perform(get("/api/product/search?name=Vklad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @WithMockUser(roles = "MANAGER")
    @Test
    void getByName_shouldReturn400() throws Exception {
        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setStatus(Status.ACTIVE);
        when(productService.findByName("Vklad")).thenThrow(new ValidationException(""));
        mvc.perform(get("/api/product/search?name=Vklad"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "MANAGER")
    @Test
    void getByName_shouldReturn404() throws Exception {

        when(productService.findByName("VKLAD")).thenThrow(new NotFoundException("Product 1 is not found"));
        mvc.perform(get("/api/product/search/"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(roles = "MANAGER")
    @Test
    void getById_shouldReturn200() throws Exception {
        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setStatus(Status.ACTIVE);
        when(productService.getById(1L)).thenReturn(productDto);
        mvc.perform(get("/api/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(9))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @WithMockUser(roles = "MANAGER")
    @Test
    void getById_shouldReturn404() throws Exception {

        when(productService.getById(1L)).thenThrow(new NotFoundException("Product 1 is not found"));
        mvc.perform(get("/api/product/1"))
                .andExpect(status().isNotFound());
    }


    @WithMockUser(roles = "ADMIN")
    @Test
    void add_shouldReturn200() throws Exception {

        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setStatus(Status.ACTIVE);
        when(productService.createProduct(any())).thenReturn(productDto);
        mvc.perform(post("/api/product/")
                        //тело запроса
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(9));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void add_shouldReturn400() throws Exception {
        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setStatus(Status.ACTIVE);
        productDto.setDepositPeriod(-12);
        when(productService.createProduct(any())).thenThrow(new ValidationException(""));
        mvc.perform(post("/api/product/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = {"USER"})
    @Test
    void add_shouldReturn403() throws Exception {

        mvc.perform(post("/api/product/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductDto())))
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    void add_shouldReturn401() throws Exception {
        ProductDto productDto = new ProductDto();
        mvc.perform(post("/api/product/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void update_shouldReturn200() throws Exception {
        ProductDto productDto = new ProductDto();

        mvc.perform(put("/api/product/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isOk());
        //  verify(productService).deleteProduct(1L);
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void update_shouldReturn404() throws Exception {
        ProductDto productDto = new ProductDto();

        when(productService.updateProduct(any(), any())).thenThrow(new NotFoundException(""));

        mvc.perform(put("/api/product/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isNotFound());
    }

    @WithMockUser()
    @Test
    void update_shouldReturn403() throws Exception {
        ProductDto productDto = new ProductDto();
        mvc.perform(put("/api/product/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void update_shouldReturn400() throws Exception {
        ProductDto productDto = new ProductDto();
        productDto.setInterestRate(-0.4);
        productDto.setDepositPeriod(-12);
        when(productService.updateProduct(any(), any())).thenThrow(new ValidationException(""));

        mvc.perform(put("/api/product/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void delete_shouldReturn200() throws Exception {

        mvc.perform(delete("/api/product/1"))
                .andExpect(status().isOk());
        verify(productService).deleteProduct(1L);
    }


    @WithMockUser()
    @Test
    void delete_shouldReturn403() throws Exception {

        mvc.perform(delete("/api/product/1"))
                .andExpect(status().isForbidden());
    }
}