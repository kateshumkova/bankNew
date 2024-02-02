package com.example.banknew.controller;


import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.ProductDto;
import com.example.banknew.dtos.TrxDto;
import com.example.banknew.entities.ClientEntity;
import com.example.banknew.entities.RoleEntity;
import com.example.banknew.entities.TrxEntity;
import com.example.banknew.entities.UserEntity;
import com.example.banknew.enums.Status;
import com.example.banknew.enums.TrxType;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.exception.ValidationException;
import com.example.banknew.mappers.TrxMapper;
import com.example.banknew.repository.ClientRepository;
import com.example.banknew.repository.TrxRepository;
import com.example.banknew.repository.UserRepository;
import com.example.banknew.service.TrxService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrxController.class)
class TrxControllerTest {
    @Autowired
    public MockMvc mvc;
    @MockBean
    public TrxService trxService;
    @MockBean
    public TrxMapper trxMapper;
    @MockBean
    public TrxRepository trxRepository;
    @MockBean
    public UserRepository userRepository;
    @MockBean
    public ClientRepository clientRepository;


    @WithMockUser(roles = "MANAGER")
    @Test
    void getAll() throws Exception {
        when(trxService.getAll()).thenReturn(List.of(new TrxDto(), new TrxDto()));
        mvc.perform(get("/api/trx/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getByAccountId() {
    }

    @Test
    void getById() {
    }

    @WithMockUser(roles = "USER")
    @Test
    void getById_shouldReturn200() throws Exception {

        TrxDto trxDto = new TrxDto();
        trxDto.setAccountId(1L);
        trxDto.setTrxType(TrxType.DEBIT);
        trxDto.setAmount(BigDecimal.valueOf(100));
        trxDto.setDescription("Popolnenie");
        when(trxService.getById(any(), any())).thenReturn(trxDto);
        mvc.perform(get("/api/trx/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @WithMockUser(roles = "MANAGER")
    @Test
    void getById_shouldReturn404() throws Exception {

        when(trxService.getById(any(), any())).thenThrow(new NotFoundException("Product 1 is not found"));
        mvc.perform(get("/api/trx/1"))
                .andExpect(status().isNotFound());
    }
    @WithMockUser()
    @Test
    void getById_shouldReturn403() throws Exception {

      //  when(trxService.getById(any(), any())).thenThrow(new AccessDeniedException("Access with such role is impossible"));
        mvc.perform(get("/api/trx/1"))
                .andExpect(status().isForbidden());
    }
    @Test
    void add() {
    }
    @WithMockUser(roles = "USER")
    @Test
    void add_shouldReturn200() throws Exception {

        TrxDto trxDto = new TrxDto();
        trxDto.setAccountId(1L);
        trxDto.setTrxType(TrxType.DEBIT);
        trxDto.setAmount(BigDecimal.valueOf(100));
        trxDto.setDescription("Popolnenie");
        when(trxService.createTrx(any(), any())).thenReturn(trxDto);
        mvc.perform(post("/api/trx/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }
    @WithMockUser(roles = "USER")
    @Test
    void add_shouldReturn400() throws Exception {

        when(trxService.getById(any(), any())).thenThrow(new ValidationException(""));
        mvc.perform(post("/api/trx/"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}