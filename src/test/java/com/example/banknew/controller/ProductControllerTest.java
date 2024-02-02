package com.example.banknew.controller;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.ClientDto;
import com.example.banknew.dtos.ProductDto;
import com.example.banknew.enums.Status;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.service.ClientService;
import com.example.banknew.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @Autowired
    public MockMvc mvc;
    @MockBean
    public ProductService productService;
    @WithMockUser(roles = "MANAGER")
    @Test
    void getAll() throws Exception {
        when(productService.getAll()).thenReturn(List.of(new ProductDto(), new ProductDto()));
        mvc.perform(get("/api/product/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getByName() {
    }

    @Test
    void getById() {
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

    @Test
    void add() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}