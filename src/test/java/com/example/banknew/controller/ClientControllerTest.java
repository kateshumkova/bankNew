package com.example.banknew.controller;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.ClientDto;
import com.example.banknew.dtos.CreateAgreementRequest;
import com.example.banknew.dtos.CreateAgreementResponse;
import com.example.banknew.enums.Status;
import com.example.banknew.exception.AccessDeniedException;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.exception.ValidationException;
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
//@WebMvcTest(ClientController.class)
class ClientControllerTest {
    @Autowired
    public MockMvc mvc;
    @Autowired
    public ObjectMapper objectMapper;
    @MockBean
    public ClientService clientService;


    @WithMockUser(roles = "MANAGER")
    @Test
    void getAll_shouldReturn200() throws Exception {
        when(clientService.getAll()).thenReturn(List.of(new ClientDto(), new ClientDto()));
        mvc.perform(get("/api/client/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @WithMockUser(roles = "MANAGER")
    @Test
    void getByLastName_shouldReturn200() throws Exception {
        ClientDto clientDto = new ClientDto();
        clientDto.setId(1L);
        clientDto.setFirstName("Aleks");
        when(clientService.findByLastName("Samoilov")).thenReturn(List.of(clientDto));
        mvc.perform(get("/api/client/search?lastName=Samoilov"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @WithMockUser(roles = "MANAGER")
    @Test
    void getById_shouldReturn200() throws Exception {
        ClientDto clientDto = new ClientDto();
        clientDto.setId(1L);
        clientDto.setFirstName("Aleks");
        when(clientService.getById(1L)).thenReturn(clientDto);
        mvc.perform(get("/api/client/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(7))
                .andExpect(jsonPath("$.firstName").value("Aleks"));
    }

    @WithMockUser(roles = "MANAGER")
    @Test
    void getById_shouldReturn404() throws Exception {

        when(clientService.getById(1L)).thenThrow(new NotFoundException("Client 1 is not found"));
        mvc.perform(get("/api/client/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
        //  @DisplayName("Test create client")
    void createClient_shouldReturn200() throws Exception {
        ClientDto clientDto = new ClientDto();
        clientDto.setFirstName("aleks");
        clientDto.setId(1L);
        clientDto.setLastName("Sam");
        clientDto.setEmail("s@gmail.com");
        clientDto.setAddress("kfkfk");
        clientDto.setPhone("9494949");
        clientDto.setStatus(Status.ACTIVE);

        when(clientService.createClient(any())).thenReturn(clientDto);

        mvc.perform(post("/api/client/")
                        //тело запроса
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(7));
    }

    @Test
    @WithMockUser(roles = {"USER"})
        //  @DisplayName("Test create client")
    void createClient_shouldReturn403() throws Exception {
        ClientDto clientDto = new ClientDto();
        clientDto.setFirstName("aleks");
        clientDto.setId(1L);
        clientDto.setLastName("Sam");
        clientDto.setEmail("s@gmail.com");
        clientDto.setAddress("kfkfk");
        clientDto.setPhone("9494949");
        clientDto.setStatus(Status.ACTIVE);

        when(clientService.createClient(any())).thenThrow(new AccessDeniedException(""));

        mvc.perform(post("/api/client/")
                        //тело запроса
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDto)))
                .andExpect(status().isForbidden());
    }


    @WithMockUser(roles = "ADMIN")
    @Test
    void update_shouldReturn200() throws Exception {

        ClientDto clientDto = new ClientDto();
        clientDto.setFirstName("aleks");
        clientDto.setId(1L);
        clientDto.setLastName("Sam");
        clientDto.setEmail("s@gmail.com");
        clientDto.setAddress("kfkfk");
        clientDto.setPhone("9494949");
        clientDto.setStatus(Status.ACTIVE);
        mvc.perform(put("/api/client/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDto)))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void update_shouldReturn404() throws Exception {
        ClientDto clientDto = new ClientDto();

        when(clientService.updateClient(any(), any())).thenThrow(new NotFoundException(""));

        mvc.perform(put("/api/client/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDto)))
                .andExpect(status().isNotFound());
    }

    @WithMockUser()
    @Test
    void update_shouldReturn403() throws Exception {
        ClientDto clientDto = new ClientDto();
        mvc.perform(put("/api/client/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDto)))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void update_shouldReturn400() throws Exception {
        //  String json = "{\"accountId\":1,\"trxType\":\"5\",\"amount\":100,\"description\":\"Popolnenie\"}";
        mvc.perform(put("/api/client/1"))
//                                 .contentType(MediaType.APPLICATION_JSON)
//                                 .content(json))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void delete_shouldReturn200() throws Exception {

        mvc.perform(delete("/api/client/1"))
                .andExpect(status().isOk());
        verify(clientService).deleteClient(1L);
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

        mvc.perform(delete("/api/client/1"))
                .andExpect(status().isForbidden());
    }
}