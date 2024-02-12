package com.example.banknew.controller;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.ClientDto;
import com.example.banknew.dtos.TrxDto;
import com.example.banknew.entities.AccountEntity;
import com.example.banknew.enums.Status;
import com.example.banknew.enums.TrxType;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.service.AccountService;
import com.example.banknew.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.dynamic.TypeResolutionStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static com.example.banknew.enums.Status.ACTIVE;
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
//@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @Autowired
    public MockMvc mvc;
    @Autowired
    public ObjectMapper objectMapper;
    @MockBean
    public AccountService accountService;


    @WithMockUser(roles = "MANAGER")
    @Test
    void getAll_shouldReturn200() throws Exception {
        when(accountService.getAll()).thenReturn(List.of(new AccountDto(), new AccountDto()));
        mvc.perform(get("/api/account/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @WithMockUser(roles = "MANAGER")
    @Test
    void getByName_shouldReturn200() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setId(1L);
        accountDto.setName("Vklad");
        accountDto.setStatus(ACTIVE);
        accountDto.setBalance(BigDecimal.valueOf(100));
        when(accountService.findByName("Vklad")).thenReturn(List.of(accountDto));
        mvc.perform(get("/api/account/search?name=Vklad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
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
    @WithMockUser(roles = {"MANAGER"})
        // @DisplayName("Test create account")
    void createAccount_shouldReturn200() throws Exception {
        AccountEntity accountEntity = new AccountEntity();
        when(accountService.createAccount()).thenReturn(accountEntity);

        mvc.perform(post("/api/account/")
                        //тело запроса
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountEntity)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void update_shouldReturn200() throws Exception {

        AccountDto accountDto = new AccountDto();
        accountDto.setId(1L);
        accountDto.setName("VKlad");
        accountDto.setStatus(ACTIVE);
        accountDto.setBalance(BigDecimal.valueOf(500));

        mvc.perform(put("/api/account/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "MANAGER")
    @Test
    void update_shouldReturn404() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setId(1L);
        accountDto.setName("VKlad");
        accountDto.setStatus(ACTIVE);
        accountDto.setBalance(BigDecimal.valueOf(500));

        when(accountService.updateAccount(any(), any())).thenThrow(new NotFoundException(""));

        mvc.perform(put("/api/account/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(status().isNotFound());
    }

    @WithMockUser()
    @Test
    void update_shouldReturn403() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setId(1L);
        accountDto.setName("VKlad");
        accountDto.setStatus(ACTIVE);
        accountDto.setBalance(BigDecimal.valueOf(500));
        mvc.perform(put("/api/account/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void update_shouldReturn400() throws Exception {
        //   String json = "{\"accountId\":1,\"trxType\":\"5\",\"amount\":100,\"description\":\"Popolnenie\"}";
        mvc.perform(put("/api/account/1"))
                //                 .contentType(MediaType.APPLICATION_JSON)
                //                 .content(json))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void delete_shouldReturn200() throws Exception {

        mvc.perform(delete("/api/account/1"))
                .andExpect(status().isOk());
        verify(accountService).deleteAccount(1L);
    }

    //    @WithMockUser(roles = "ADMIN")
//    @Test
//    void delete_shouldReturn404() throws Exception {
//        OngoingStubbing<T> tOngoingStubbing = when(trxService.deleteTrx(any())).thenThrow(new NotFoundException(""));
//        mvc.perform(delete("/api/trx/1"))
//                .andExpect(status().isNotFound());
//        verify(trxService).deleteTrx(1L);
//    }
    @WithMockUser()
    @Test
    void delete_shouldReturn403() throws Exception {

        mvc.perform(delete("/api/account/1"))
                .andExpect(status().isForbidden());
    }
}