package com.example.banknew.controller;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.AgreementDto;
import com.example.banknew.dtos.ManagerDto;
import com.example.banknew.dtos.TrxDto;
import com.example.banknew.entities.ManagerEntity;
import com.example.banknew.enums.TrxType;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.exception.ValidationException;
import com.example.banknew.repository.ManagerRepository;
import com.example.banknew.service.ClientService;
import com.example.banknew.service.ManagerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(ManagerController.class)
@SpringBootTest
@AutoConfigureMockMvc
class ManagerControllerTest {
    @Autowired
    public MockMvc mvc;
    @Autowired
    public ObjectMapper objectMapper;
    @MockBean
    public ManagerService managerService;
    @MockBean
    public ManagerRepository managerRepository;


    @WithMockUser(roles = "MANAGER")
    @Test
    void getAll_shouldReturn200() throws Exception {
        when(managerService.getAll()).thenReturn(List.of(new ManagerDto(), new ManagerDto()));
        mvc.perform(get("/api/manager/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @WithMockUser(roles = "MANAGER")
    @Test
    void getByLastName_shouldReturn200() throws Exception {

        ManagerDto managerDto = new ManagerDto();
        when(managerService.findByLastName(any())).thenReturn(List.of(managerDto));
        mvc.perform(get("/api/manager/search?lastName=Ahbcnjd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @WithMockUser(roles = "MANAGER")
    @Test
    void getByLastName_shouldReturn404() throws Exception {

        when(managerService.findByLastName(any())).thenThrow(new NotFoundException("Manager 1 is not found"));
        mvc.perform(get("/api/manager/search?lastName=Ahbcnjd"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(roles = "MANAGER")
    @Test
    void getById_shouldReturn200() throws Exception {

        ManagerDto managerDto = new ManagerDto();
        when(managerService.getById(any())).thenReturn(managerDto);
        mvc.perform(get("/api/manager/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @WithMockUser(roles = "MANAGER")
    @Test
    void getById_shouldReturn404() throws Exception {

        when(managerService.getById(any())).thenThrow(new NotFoundException("Manager 1 is not found"));
        mvc.perform(get("/api/manager/1"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser()
    @Test
    void getById_shouldReturn403() throws Exception {

        mvc.perform(get("/api/manager/1"))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void add_shouldReturn200() throws Exception {

        ManagerDto managerDto = new ManagerDto();
        managerDto.setId(1L);
        managerDto.setEmail("fjfjjf");
        when(managerService.createManager(any())).thenReturn(managerDto);
        mvc.perform(post("/api/manager/")
                        //тело запроса
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(managerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }


    @WithMockUser(roles = "ADMIN")
    @Test
    void update_shouldReturn200() throws Exception {

        AgreementDto agreementDto = new AgreementDto();

        mvc.perform(put("/api/manager/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(agreementDto)))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void update_shouldReturn404() throws Exception {
        ManagerDto managerDto = new ManagerDto();

        when(managerService.updateManager(any(), any())).thenThrow(new NotFoundException(""));

        mvc.perform(put("/api/manager/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(managerDto)))
                .andExpect(status().isNotFound());
    }

    @WithMockUser()
    @Test
    void update_shouldReturn403() throws Exception {
        ManagerDto managerDto = new ManagerDto();
        mvc.perform(put("/api/manager/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(managerDto)))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void update_shouldReturn400() throws Exception {
        ManagerDto managerDto = new ManagerDto();

        when(managerService.updateManager(1L, managerDto)).thenThrow(new ValidationException(""));

        // String json = "{\"accountId\":1,\"trxType\":\"5\",\"amount\":100,\"description\":\"Popolnenie\"}";
        mvc.perform(put("/api/manager/1"))
                //  .contentType(MediaType.APPLICATION_JSON)
                //    .content(objectMapper.writeValueAsString(agreementDto)))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void delete_shouldReturn200() throws Exception {

        mvc.perform(delete("/api/manager/1"))
                .andExpect(status().isOk());

    }

//    @WithMockUser(roles = "ADMIN")
//    @Test
//    void delete_shouldReturn404() throws Exception {
//        ManagerEntity managerEntity = new ManagerEntity();
//     //   given(managerService.deleteManager(1L)).willThrow(new NotFoundException("Not Found"));
//
//        // Mockito.verify(managerRepository, Mockito.times(1)).save(eq(managerEntity));
//        managerService.deleteManager(managerEntity.getId());
//        mvc.perform(delete("/api/manager/1"))
//                .andExpect(status().isNotFound());
//        // verify(managerService).deleteManager(1L);
//    }

    @WithMockUser()
    @Test
    void delete_shouldReturn403() throws Exception {
     //   Long id = 1L;
      //  doNothing().when(delete("/api/manager/{id}", id));
        mvc.perform(delete("/api/manager/{id}", 1L))
                .andExpect(status().isForbidden());
    }
}