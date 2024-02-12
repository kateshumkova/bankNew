package com.example.banknew.service;

import com.example.banknew.dtos.ClientDto;
import com.example.banknew.dtos.CreateClientRequest;
import com.example.banknew.entities.ClientEntity;

import java.util.List;

public interface ClientService {
    List<ClientDto> getAll();
    ClientDto getById(Long id);
    // ClientDto getByEmail(String email);
    List<ClientDto> findByLastName(String lastName);
    ClientDto createClient(CreateClientRequest creationRequestClientDto);
    ClientDto updateClient(Long id, ClientDto clientDto);
    void deleteClient(Long id);


}
