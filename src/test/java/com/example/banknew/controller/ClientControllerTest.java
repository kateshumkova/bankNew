package com.example.banknew.controller;

import com.example.banknew.dtos.ClientDto;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.service.ClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
class ClientControllerTest {
    @Autowired
    public MockMvc mvc;
    @MockBean
    public ClientService clientService;

    @WithMockUser(roles = "MANAGER")
    @Test
    void getAll() throws Exception {
        when(clientService.getAll()).thenReturn(List.of(new ClientDto(), new ClientDto()));
        mvc.perform(get("/api/client/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getByLastName() {
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
    void add() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}