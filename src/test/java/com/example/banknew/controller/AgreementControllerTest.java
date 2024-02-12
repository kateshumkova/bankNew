package com.example.banknew.controller;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.AgreementDto;
import com.example.banknew.dtos.CreateAgreementRequest;
import com.example.banknew.dtos.CreateAgreementResponse;
import com.example.banknew.entities.AgreementEntity;
import com.example.banknew.entities.RoleEntity;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.exception.ValidationException;
import com.example.banknew.repository.AgreementRepository;
import com.example.banknew.service.AgreementService;
import com.example.banknew.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;


import java.math.BigDecimal;
import java.util.List;

import static com.example.banknew.enums.Status.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest(AgreementController.class)
class AgreementControllerTest {
    @Autowired
    public MockMvc mvc;
    @Autowired
    public ObjectMapper objectMapper;
    @MockBean
    public AgreementService agreementService;
    @MockBean
    public AgreementRepository agreementRepository;

    @WithMockUser(roles = "MANAGER")
    @Test
    void getAll_shouldReturn200() throws Exception {
        when(agreementService.getAll()).thenReturn(List.of(new AgreementDto(), new AgreementDto()));
        mvc.perform(get("/api/agreement/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        verify(agreementService).getAll();
    }

    @WithMockUser(roles = "MANAGER")
    @Test
    void getByClientId_shouldReturn200() throws Exception {
        //when(agreementService.findByClientId(1L)).thenReturn(agreementDto);
        mvc.perform(get("/api/agreement/search?id=1"))
                .andExpect(status().isOk());

    }

    @WithMockUser(roles = "MANAGER")
    @Test
    void getByClientId_shouldReturn404() throws Exception {

        when(agreementService.findByClientId(1L)).thenThrow(new NotFoundException("Account 1 is not found"));
        mvc.perform(get("/api/agreement/search?id=1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void add_shouldReturn200() throws Exception {
        CreateAgreementResponse response = new CreateAgreementResponse();
        CreateAgreementRequest request = new CreateAgreementRequest();
        when(agreementService.createAgreement(request)).thenReturn(response);

        mvc.perform(post("/api/agreement/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void update_shouldReturn200() throws Exception {

        AgreementDto agreementDto = new AgreementDto();

        mvc.perform(put("/api/agreement/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(agreementDto)))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void update_shouldReturn404() throws Exception {
        AgreementDto agreementDto = new AgreementDto();

        when(agreementService.updateAgreement(any(), any())).thenThrow(new NotFoundException(""));

        mvc.perform(put("/api/agreement/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(agreementDto)))
                .andExpect(status().isNotFound());
    }

    @WithMockUser()
    @Test
    void update_shouldReturn403() throws Exception {
        AgreementDto agreementDto = new AgreementDto();
        mvc.perform(put("/api/agreement/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(agreementDto)))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void update_shouldReturn400() throws Exception {
        AgreementDto agreementDto = new AgreementDto();

        when(agreementService.updateAgreement(1L, agreementDto)).thenThrow(new ValidationException(""));

        // String json = "{\"accountId\":1,\"trxType\":\"5\",\"amount\":100,\"description\":\"Popolnenie\"}";
        mvc.perform(put("/api/agreement/1"))
                      //  .contentType(MediaType.APPLICATION_JSON)
                    //    .content(objectMapper.writeValueAsString(agreementDto)))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void delete_shouldReturn200() throws Exception {

        mvc.perform(delete("/api/agreement/1"))
                .andExpect(status().isOk());
        verify(agreementService).deleteAgreement(1L);
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

        mvc.perform(delete("/api/agreement/1"))
                .andExpect(status().isForbidden());
    }
}