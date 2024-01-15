package com.example.banknew.service.impl;

import com.example.banknew.dtos.ClientDto;
import com.example.banknew.dtos.CreateClientRequest;
import com.example.banknew.entities.ClientEntity;
import com.example.banknew.enums.Status;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.exception.ValidationException;
import com.example.banknew.mappers.ClientMapper;
import com.example.banknew.repository.ClientRepository;
import com.example.banknew.repository.UserRepository;
import com.example.banknew.service.ClientService;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ClientMapper clientMapper;

    @Override
    public List<ClientDto> getAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toDto)
                .toList();
    }

    @Override
    public ClientDto getById(Long id) {
        Optional<ClientEntity> optClientEntity = clientRepository.findById(id);
        if (optClientEntity.isEmpty()) {
            throw new NotFoundException("Client " + id + " is not found");
        }
        ClientEntity clientEntity = optClientEntity.get();
        var user = clientEntity.getUser();
        return clientMapper.toDto(optClientEntity.get());
    }

    @Override
    public List<ClientDto> findByLastName(String lastName) {
        List<ClientEntity> clientEntities = clientRepository.findByLastName(lastName);
        if (clientEntities.isEmpty()) {
            throw new NotFoundException("Client with lastName " + lastName + " is not found");
        }
        return clientEntities.stream()
                .map(clientMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public ClientDto createClient(CreateClientRequest creationRequestClientDto) {
        //сначала создается юзер, потом клиент
        Optional<ClientEntity> optClientEntity = clientRepository.findByEmail(creationRequestClientDto.getEmail());
        if (optClientEntity.isEmpty()) {
            ClientEntity clientEntity = clientMapper.toEntity(creationRequestClientDto);
            clientEntity.setCreatedAt(Instant.now());
            userRepository.findByUsername(creationRequestClientDto.getEmail()).ifPresentOrElse(clientEntity::setUser, () -> {
                throw new NotFoundException("No such username-email in repository");
            });
            ClientEntity savedClient = clientRepository.save(clientEntity);
            log.info("Created and saved client with ID= {}", savedClient.getId());
            return clientMapper.toDto(savedClient);
        } else {
            throw new ValidationException("Client cannot be created, email " + creationRequestClientDto.getEmail() + " is occupied");
        }
    }

    @Override
    public ClientDto updateClient(Long id, ClientDto clientDto) {
        Optional<ClientEntity> optClientEntity = clientRepository.findById(id);
        if (optClientEntity.isEmpty()) {
            throw new NotFoundException("Client " + id + " cannot be updated, id is not found");
        }
        ClientEntity clientEntity = optClientEntity.get();
        clientMapper.updateEntity(clientEntity, clientDto);
        clientRepository.save(clientEntity);
        log.info("Client with ID {} is updated", id);
        return clientMapper.toDto(clientEntity);
    }


    @Override
    public void deleteClient(Long id) {
        Optional<ClientEntity> optClientEntity = clientRepository.findById(id);
        if (optClientEntity.isEmpty()) {
            throw new NotFoundException("Client " + id + " is not found");
        }
        ClientEntity clientEntity = optClientEntity.get();
        clientEntity.setStatus(Status.INACTIVE);
        clientRepository.save(clientEntity);

        //   clientRepository.deleteById(id);
    }
}

