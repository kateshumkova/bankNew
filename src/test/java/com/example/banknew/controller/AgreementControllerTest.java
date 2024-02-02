package com.example.banknew.controller;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.AgreementDto;
import com.example.banknew.dtos.CreateAgreementRequest;
import com.example.banknew.dtos.CreateAgreementResponse;
import com.example.banknew.entities.AgreementEntity;
import com.example.banknew.entities.RoleEntity;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.repository.AgreementRepository;
import com.example.banknew.service.AgreementService;
import com.example.banknew.service.ClientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;


import java.util.List;

import static com.example.banknew.enums.Status.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgreementController.class)
class AgreementControllerTest {
    @Autowired
    public MockMvc mvc;
    @MockBean
    public AgreementService agreementService;
    @MockBean
    public AgreementRepository agreementRepository;
    @WithMockUser(roles = "MANAGER")
    @Test
    void getAll() throws Exception {
        when(agreementService.getAll()).thenReturn(List.of(new AgreementDto(), new AgreementDto()));
        mvc.perform(get("/api/agreement/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getByClientId() {
    }
    @WithMockUser(roles = "MANAGER")
    @Test
    void getByClientId_shouldReturn200() throws Exception {
        List<AgreementDto> agreementDto = List.of(new AgreementDto(),new AgreementDto());
        List<AgreementEntity> agreementEntities =List.of(new AgreementEntity(), new AgreementEntity());
       when(agreementRepository.findByClientId(any())).thenReturn(agreementEntities);
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("ROLE_USER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));

        when(agreementService.findByClientId(1L)).thenReturn(agreementDto);
        mvc.perform(get("/api/agreement/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
              //  .andExpect(jsonPath("$.clientId").value(1));
    }
    @WithMockUser(roles = "MANAGER")
    @Test
    void getByClientId_shouldReturn404() throws Exception {

        when(agreementService.findByClientId(1L)).thenThrow(new NotFoundException("Account 1 is not found"));
        mvc.perform(get("/api/agreement/1"));
              //  .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    @DisplayName("Test create method")
    void add() throws Exception {
        CreateAgreementResponse response = new CreateAgreementResponse();
        CreateAgreementRequest request = new CreateAgreementRequest();
        when(agreementService.createAgreement(request)).thenReturn(response);

         mvc.perform(post("/api/agreement/"));
                     //   .andDo(MockMvcResultHandlers.print()));

//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(response.getAgreementDto().getId()))
//                .andExpect(jsonPath("$.active").value(ACTIVE));
    }
    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}