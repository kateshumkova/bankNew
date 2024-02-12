package com.example.banknew.service.impl;

import com.example.banknew.dtos.*;
import com.example.banknew.entities.AgreementEntity;
import com.example.banknew.entities.ClientEntity;
import com.example.banknew.entities.ManagerEntity;
import com.example.banknew.entities.UserEntity;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.exception.ValidationException;
import com.example.banknew.mappers.ClientMapper;
import com.example.banknew.repository.AccountRepository;
import com.example.banknew.repository.ClientRepository;
import com.example.banknew.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ClientServiceImplTest {
    @InjectMocks
    private ClientServiceImpl clientService;

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ClientMapper clientMapper;

    @Test
    void testGetAll_shouldReturnListClientsDto() {
        //заглушки
        // AccountEntity accountEntity = new AccountEntity();
        // when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntity));

        //вызов метода
        List<ClientDto> actual = clientService.getAll();

        //проверка результата
//        verify(accountMapper,atLeast(1)).toDto(any());
        verify(clientRepository, atLeast(1)).findAll();
        assertNotNull(actual);
    }

    @Test
    void testGetById_shouldNotFoundException_ifEmptyClient() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> clientService.getById(1L));
        assertEquals("Client 1 is not found", exception.getMessage());
    }

    @Test
    void testGetById_shouldReturnClientDto_ifNotEmptyClient() {
        //заглушки
        ClientEntity clientEntity = new ClientEntity();
        when(clientRepository.findById(any())).thenReturn(Optional.of(clientEntity));

        //вызов метода
        ClientDto actual = clientService.getById(1L);

        //проверка результата
        verify(clientMapper, atLeast(1)).toDto(any());
//        assertNotNull(actual);
    }

    @Test
    void testFindByLastName_shouldNotFoundException_ifEmptyClient() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> clientService.findByLastName("Einstein"));
        assertEquals("Client with lastName Einstein is not found", exception.getMessage());
    }

    @Test
    void testFindByLastName_shouldReturnClientDto_ifNotEmptyClient() {
        //заглушки
        ClientEntity clientEntity = new ClientEntity();
        when(clientRepository.findByLastName(any())).thenReturn(List.of(clientEntity));

        //вызов метода
        List<ClientDto> actual = clientService.findByLastName("Simpson");

        //проверка результата
        verify(clientMapper, atLeast(1)).toDto(any());
//        assertNotNull(actual);
    }

    //    @Override
//    public ClientDto createClient(CreateClientRequest creationRequestClientDto) {
//        //сначала создается юзер, потом клиент
//        Optional<ClientEntity> optClientEntity = clientRepository.findByEmail(creationRequestClientDto.getEmail());
//        if (optClientEntity.isEmpty()) {
//            ClientEntity clientEntity = clientMapper.toEntity(creationRequestClientDto);
//            clientEntity.setCreatedAt(Instant.now());
//            userRepository.findByUsername(creationRequestClientDto.getEmail()).ifPresentOrElse(clientEntity::setUser, () -> {
//                throw new NotFoundException("No such username-email in repository");
//            });
//            ClientEntity savedClient = clientRepository.save(clientEntity);
//            log.info("Created and saved client with ID= {}", savedClient.getId());
//            return clientMapper.toDto(savedClient);
//        } else {
//            throw new ValidationException("Client cannot be created, email " + creationRequestClientDto.getEmail() + " is occupied");
//        }
//    }
    @Test
    void testCreateClient_shouldNotFoundException_ifNoEmailInUserRepository() {
        ClientEntity clientEntity = new ClientEntity();
        CreateClientRequest request = new CreateClientRequest();
        when(clientRepository.findByEmail(request.getEmail())).thenReturn(Optional.ofNullable(null));
        when(clientMapper.toEntity(request)).thenReturn(clientEntity);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> clientService.createClient(request));
        assertEquals("No such username-email in repository", exception.getMessage());
    }

    @Test
    void testCreateClient_shouldReturnClientDto_ifNoEmailInClientRepositoryAndPresentEmailInUserRepository() {
        ClientEntity clientEntity = new ClientEntity();
        UserEntity userEntity = new UserEntity();
        ClientEntity savedClient = new ClientEntity();
        CreateClientRequest request = new CreateClientRequest();
        when(clientRepository.findByEmail(request.getEmail())).thenReturn(Optional.ofNullable(null));
        when(userRepository.findByUsername(request.getEmail())).thenReturn(Optional.of(userEntity));
        when(clientMapper.toEntity(request)).thenReturn(clientEntity);
        when(clientRepository.save(clientEntity)).thenReturn(savedClient);
        ClientDto actual = clientService.createClient(request);

        //проверка результата
        verify(clientMapper, atLeast(1)).toDto(any());
        verify(clientRepository).save(clientEntity); ///????
       // assertNotNull(actual);
    }

    @Test
    void testCreateClient_shouldValidation_ifEmailIsOccupied() {
        ClientDto clientDto = new ClientDto();
        ClientEntity clientEntity = new ClientEntity();
        CreateClientRequest request = new CreateClientRequest();
        when(clientRepository.findByEmail(clientDto.getEmail())).thenReturn(Optional.of(clientEntity));

        ValidationException exception = assertThrows(ValidationException.class, () -> clientService.createClient(request));
        assertEquals("Client cannot be created, email " + request.getEmail() + " is occupied", exception.getMessage());
    }

    @Test
    void testUpdateClient_shouldNotFoundException_ifClientIsEmpty() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> clientService.updateClient(1L, new ClientDto()));
        assertEquals("Client 1 cannot be updated, id is not found", exception.getMessage());
    }

    @Test
    void testUpdateClient_shouldReturnClientEntity_ifNotEmptyClient() {
        //заглушки
        ClientEntity clientEntity = new ClientEntity();
        when(clientRepository.findById(any())).thenReturn(Optional.of(clientEntity));

        //вызов метода
        ClientDto actual = clientService.updateClient(1L, new ClientDto());

        //проверка результата
        verify(clientMapper, atLeast(1)).updateEntity(any(), any());
        verify(clientRepository).save(any());
//        assertNotNull(actual);
    }

    @Test
    void testDeleteClient_shouldNotFoundException_ifClientIsEmpty() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> clientService.deleteClient(1L));
        assertEquals("Client 1 is not found", exception.getMessage());
    }

    @Test
    void testDeleteClient_shouldChangeStatusToInactive_ifPresentClient() {
        //заглушки
        ClientEntity clientEntity = new ClientEntity();
        when(clientRepository.findById(any())).thenReturn(Optional.of(clientEntity));

        //вызов метода
        clientService.deleteClient(1L);

        //проверка результата
        // verify(accountMapper,atLeast(1)).updateEntity(any(), any());
        verify(clientRepository).save(any());
//        assertNotNull(actual);
    }
}