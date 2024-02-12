package com.example.banknew.controller;


import com.example.banknew.dtos.TrxDto;
import com.example.banknew.enums.TrxType;
import com.example.banknew.exception.AccessDeniedException;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.exception.ValidationException;
import com.example.banknew.mappers.TrxMapper;
import com.example.banknew.repository.ClientRepository;
import com.example.banknew.repository.TrxRepository;
import com.example.banknew.repository.UserRepository;
import com.example.banknew.service.TrxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest(TrxController.class)
class TrxControllerTest {
    @Autowired
    public MockMvc mvc;
    @Autowired
    public ObjectMapper objectMapper;
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
    void getAll_shouldReturn200() throws Exception {
        when(trxService.getAll()).thenReturn(List.of(new TrxDto(), new TrxDto()));
        mvc.perform(get("/api/trx/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
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

    @WithMockUser(roles = "V")
    @Test
    void getById_shouldReturn403() throws Exception {

        //  when(trxService.getById(any(), any())).thenThrow(new AccessDeniedException("Access with such role is impossible"));
        mvc.perform(get("/api/trx/1"))
                .andExpect(status().isForbidden());
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
        mvc.perform(post("/api/trx/")
                        //тело запроса
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trxDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @WithMockUser(roles = "USER")
    @Test
    void add_shouldReturn400() throws Exception {

        when(trxService.createTrx(any(), any())).thenThrow(new ValidationException(""));
        mvc.perform(post("/api/trx/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TrxDto())))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = {"ADMIN", "MANAGER"})
    @Test
    void add_shouldReturn403() throws Exception {

        mvc.perform(post("/api/trx/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TrxDto())))
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    void add_shouldReturn401() throws Exception {

        mvc.perform(post("/api/trx/"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void update_shouldReturn200() throws Exception {
        TrxDto trxDto = new TrxDto();
        trxDto.setAccountId(1L);
        trxDto.setTrxType(TrxType.DEBIT);
        trxDto.setAmount(BigDecimal.valueOf(100));
        trxDto.setDescription("Popolnenie");

        //trxService.deleteTrx(any());
        mvc.perform(put("/api/trx/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trxDto)))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void update_shouldReturn404() throws Exception {
        TrxDto trxDto = new TrxDto();
        trxDto.setAccountId(1L);
        trxDto.setTrxType(TrxType.DEBIT);
        trxDto.setAmount(BigDecimal.valueOf(100));
        trxDto.setDescription("Popolnenie");
        when(trxService.updateTrx(any(), any())).thenThrow(new NotFoundException(""));
        //trxService.deleteTrx(any());
        mvc.perform(put("/api/trx/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trxDto)))
                .andExpect(status().isNotFound());
    }

    @WithMockUser()
    @Test
    void update_shouldReturn403() throws Exception {
        TrxDto trxDto = new TrxDto();
        trxDto.setAccountId(1L);
        trxDto.setTrxType(TrxType.DEBIT);
        trxDto.setAmount(BigDecimal.valueOf(100));
         trxDto.setDescription("Popolnenie");
        mvc.perform(put("/api/trx/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trxDto)))
                .andExpect(status().isForbidden());
    }
    @WithMockUser()
    @Test
    void update_shouldReturn400() throws Exception {
        String json = "{\"accountId\":1,\"trxType\":\"5\",\"amount\":100,\"description\":\"Popolnenie\"}";
        mvc.perform(put("/api/trx/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void delete_shouldReturn200() throws Exception {

        //trxService.deleteTrx(any());
        mvc.perform(delete("/api/trx/1"))
                .andExpect(status().isOk());
        verify(trxService).deleteTrx(1L);
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

        mvc.perform(delete("/api/trx/1"))
                .andExpect(status().isForbidden());
    }
}