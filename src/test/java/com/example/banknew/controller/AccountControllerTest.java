package com.example.banknew.controller;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.ClientDto;
import com.example.banknew.enums.Status;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.service.AccountService;
import com.example.banknew.service.ClientService;
import net.bytebuddy.dynamic.TypeResolutionStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.example.banknew.enums.Status.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @Autowired
    public MockMvc mvc;
    @MockBean
    public AccountService accountService;
    @WithMockUser(roles = "MANAGER")
    @Test
    void getAll() throws Exception {
        when(accountService.getAll()).thenReturn(List.of(new AccountDto(), new AccountDto()));
        mvc.perform(get("/api/account/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getByName() {
    }

    @WithMockUser(roles = "MANAGER")
    @Test
    void getById_shouldReturn200() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setId(1L);
        accountDto.setStatus(ACTIVE);
        when(accountService.getById(1L)).thenReturn(accountDto);
        mvc.perform(get("/api/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
    @WithMockUser(roles = "MANAGER")
    @Test
    void getById_shouldReturn404() throws Exception {

        when(accountService.getById(1L)).thenThrow(new NotFoundException("Account 1 is not found"));
        mvc.perform(get("/api/account/1"))
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