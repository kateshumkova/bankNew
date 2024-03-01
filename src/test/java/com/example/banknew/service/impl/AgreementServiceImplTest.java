package com.example.banknew.service.impl;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.AgreementDto;
import com.example.banknew.dtos.CreateAgreementRequest;
import com.example.banknew.dtos.CreateAgreementResponse;
import com.example.banknew.entities.*;
import com.example.banknew.enums.Status;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.exception.ValidationException;
import com.example.banknew.mappers.*;
import com.example.banknew.repository.AgreementRepository;
import com.example.banknew.repository.ClientRepository;
import com.example.banknew.repository.ManagerRepository;
import com.example.banknew.repository.ProductRepository;
import org.hibernate.dialect.function.PostgreSQLTruncRoundFunction;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static com.example.banknew.enums.Status.ACTIVE;
import static com.example.banknew.enums.Status.INACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
class AgreementServiceImplTest {

    @InjectMocks
    private AgreementServiceImpl agreementService;
    @Mock
    private AccountServiceImpl accountService;

    @Mock
    private AgreementRepository agreementRepository;
    @Mock
    private AgreementMapper agreementMapper;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private ClientMapper clientMapper;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private ManagerMapper managerMapper;
    @Mock
    private AccountMapper accountMapper;

    @Test
    void testGetAll_shouldReturnListAgreementDto() {
        //заглушки
        // AccountEntity accountEntity = new AccountEntity();
        // when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntity));

        //вызов метода
        List<AgreementDto> actual = agreementService.getAll();

        //проверка результата
//        verify(accountMapper,atLeast(1)).toDto(any());
        verify(agreementRepository, atLeast(1)).findAll();
        assertNotNull(actual);
    }

    @Test
    void testFindByClientId_shouldNotFoundException_ifEmptyAgreements() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> agreementService.findByClientId(1L));
        assertEquals("Agreement for client id doesn't exist", exception.getMessage());
    }

    @Test
    void testFindByClientId_shouldReturnListAgreementDto_ifListOfAgreementsNotEmpty() {
        //заглушки
        AgreementEntity agreementEntity = new AgreementEntity();
        when(agreementRepository.findByClientId(any())).thenReturn(List.of(agreementEntity));

        //вызов метода
        List<AgreementDto> actual = agreementService.findByClientId(1L);

        //проверка результата
        verify(agreementMapper, atLeast(1)).toDto(any());
        assertNotNull(actual);
    }

    @Test
    void testFindById_shouldNotFoundException_ifEmptyAgreements() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> agreementService.findById(1L));
        assertEquals("Agreement not found", exception.getMessage());
    }

    @Test
    void testFindById_shouldReturnAgreementDto_ifNotEmptyAgreement() {
        //заглушки
        AgreementEntity agreementEntity = new AgreementEntity();
        when(agreementRepository.findById(any())).thenReturn(Optional.of(agreementEntity));

        //вызов метода
        AgreementDto actual = agreementService.findById(1L);

        //проверка результата
        verify(agreementMapper, atLeast(1)).toDto(any());
        //?????  assertNotNull(actual);
    }

    @Test
    void testFindAllActive_shouldNotFoundException_ifEmptyAgreements() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> agreementService.findAllActive());
        assertEquals("There are no active Agreements", exception.getMessage());
    }

    @Test
    void testFindAllActive_shouldReturnListAgreementsDto_ifNotEmptyAgreements() {
        //заглушки
        AgreementEntity agreementEntity = new AgreementEntity();
        when(agreementRepository.findByStatus(ACTIVE.ordinal())).thenReturn(List.of(agreementEntity));

        //вызов метода
        List<AgreementDto> actual = agreementService.findAllActive();

        //проверка результата
        verify(agreementMapper, atLeast(1)).toDto(any());
        assertNotNull(actual);
    }

    @Test
    void testFindByAccountId_shouldNotFoundException_ifEmptyAgreements() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> agreementService.findByAccountId(1L));
        assertEquals("Agreement for this account id doesn't exist", exception.getMessage());
    }

    @Test
    void testFindByAccountId_shouldReturnAgreementDto_ifNotEmptyAgreement() {
        //заглушки
        AgreementEntity agreementEntity = new AgreementEntity();
        when(agreementRepository.findByAccountId(any())).thenReturn(Optional.of(agreementEntity));

        //вызов метода
        AgreementDto actual = agreementService.findByAccountId(1L);

        //проверка результата
        verify(agreementMapper, atLeast(1)).toDto(any());
        // assertNotNull(actual);
    }

    @Test
    void testFindByProductId_shouldNotFoundException_ifEmptyAgreements() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> agreementService.findByProductId(1L));
        assertEquals("Agreement with this product id doesn't exist", exception.getMessage());
    }

    @Test
    void testFindByProductId_shouldReturnAgreementDto_ifNotEmptyAgreement() {
        //заглушки
        AgreementEntity agreementEntity = new AgreementEntity();
        when(agreementRepository.findByProductId(any())).thenReturn(List.of(agreementEntity));

        //вызов метода
        List<AgreementDto> actual = agreementService.findByProductId(1L);

        //проверка результата
        verify(agreementMapper, atLeast(1)).toDto(any());
        // assertNotNull(actual);
    }

    @Test
    void testFindByManagerId_shouldNotFoundException_ifEmptyAgreements() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> agreementService.findByManagerId(1L));
        assertEquals("There are no Agreements with this manager id", exception.getMessage());
    }

    @Test
    void testFindByManagerId_shouldReturnListAgreementDto_ifNotEmptyAgreement() {
        //заглушки
        AgreementEntity agreementEntity = new AgreementEntity();
        when(agreementRepository.findByManagerId(any())).thenReturn(List.of(agreementEntity));

        //вызов метода
        List<AgreementDto> actual = agreementService.findByManagerId(1L);

        //проверка результата
        verify(agreementMapper, atLeast(1)).toDto(any());
        // assertNotNull(actual);
    }

    @Test
    void testUpdateAgreement_shouldReturnAgreementEntity_ifNotEmptyAgreement() {
        //заглушки
        AgreementEntity agreementEntity = new AgreementEntity();
        when(agreementRepository.findById(any())).thenReturn(Optional.of(agreementEntity));

        //вызов метода
        AgreementDto actual = agreementService.updateAgreement(1L, new AgreementDto());

        //проверка результата
        verify(agreementMapper, atLeast(1)).updateEntity(any(), any());
        verify(agreementRepository).save(any());
//        assertNotNull(actual);
    }

    @Test
    void testUpdateAgreement_shouldNotFoundException_ifEmptyAgreement() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> agreementService.updateAgreement(1L, new AgreementDto()));
        assertEquals("Agreement cannot be updated, 1 is not found", exception.getMessage());
    }
//todo

//    @Test
//    void testCreateAgreement_shouldCreateAgreementResponse_happyPAth() {
//        //заглушки
//        AgreementEntity agreementEntity = new AgreementEntity();
//        ClientEntity clientEntity = new ClientEntity();
//        ProductEntity productEntity = new ProductEntity();
//        ManagerEntity managerEntity = new ManagerEntity();
//        AgreementEntity savedAgreementEntity = new AgreementEntity();
//
//        when(agreementRepository.findById(any())).thenReturn(Optional.of(agreementEntity));
//        CreateAgreementRequest createAgreementRequest = new CreateAgreementRequest();
//        createAgreementRequest.setSum(BigDecimal.valueOf(500));
//        productEntity.setLimitMax(BigDecimal.valueOf(50000));
//        productEntity.setLimitMin(BigDecimal.valueOf(5));
//        when(clientRepository.findById(any())).thenReturn(Optional.of(clientEntity));
//        when(productRepository.findById(any())).thenReturn(Optional.of(productEntity));
//        when(managerRepository.findById(any())).thenReturn(Optional.of(managerEntity));
//        when(agreementRepository.saveAndFlush(any())).thenReturn(savedAgreementEntity);
//
//
//        //вызов метода
//        CreateAgreementResponse actual = agreementService.createAgreement(createAgreementRequest);
//
//        //проверка результата
//
//        verify(agreementRepository, atLeastOnce()).saveAndFlush(any());
//        assertNotNull(actual);
//    }

    @Test
    void testCreateAgreement_shouldNotFoundException_ifEmptyClientEntity() {
        when(clientRepository.findById(any())).thenReturn(Optional.ofNullable(null));
        NotFoundException exception = assertThrows(NotFoundException.class, () -> agreementService.createAgreement(new CreateAgreementRequest()));
        assertEquals("Some value is empty", exception.getMessage());
    }

    @Test
    void testCreateAgreement_shouldNotFoundException_ifEmptyProductEntity() {
        when(productRepository.findById(any())).thenReturn(Optional.ofNullable(null));
        NotFoundException exception = assertThrows(NotFoundException.class, () -> agreementService.createAgreement(new CreateAgreementRequest()));
        assertEquals("Some value is empty", exception.getMessage());
    }

    @Test
    void testCreateAgreement_shouldNotFoundException_ifEmptyManagerEntity() {
        when(managerRepository.findById(any())).thenReturn(Optional.ofNullable(null));
        NotFoundException exception = assertThrows(NotFoundException.class, () -> agreementService.createAgreement(new CreateAgreementRequest()));
        assertEquals("Some value is empty", exception.getMessage());
    }

    @Test
    void testCreateAgreement_shouldValidationException_SumIsGreaterThanLimitMax() {
        //заглушки
        AgreementEntity agreementEntity = new AgreementEntity();
        ClientEntity clientEntity = new ClientEntity();
        ProductEntity productEntity = new ProductEntity();
        ManagerEntity managerEntity = new ManagerEntity();
        AgreementEntity savedAgreementEntity = new AgreementEntity();

        when(agreementRepository.findById(any())).thenReturn(Optional.of(agreementEntity));
        CreateAgreementRequest createAgreementRequest = new CreateAgreementRequest();
        createAgreementRequest.setSum(BigDecimal.valueOf(500));
        productEntity.setLimitMax(BigDecimal.valueOf(5));
        // productEntity.setLimitMin(BigDecimal.valueOf(5));
        when(clientRepository.findById(any())).thenReturn(Optional.of(clientEntity));
        when(productRepository.findById(any())).thenReturn(Optional.of(productEntity));
        when(managerRepository.findById(any())).thenReturn(Optional.of(managerEntity));
       // when(createAgreementRequest.getSum()).thenReturn(BigDecimal.valueOf(500));//  when(agreementRepository.saveAndFlush(any())).thenReturn(savedAgreementEntity);
//when(createAgreementRequest.getSum().compareTo(productEntity.getLimitMax()) > 0).thenReturn(true);

        ValidationException exception = assertThrows(ValidationException.class, () -> agreementService.createAgreement(createAgreementRequest));
        assertEquals("Sum of agreement is less or greater than than limits of the product", exception.getMessage());
    }

    @Test
    void testDeleteAgreement_shouldNotFoundException_ifEmptyAgreement() {
        //заглушки
        when(agreementRepository.findById(any())).thenReturn(Optional.ofNullable(null));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> agreementService.deleteAgreement(1L));
        assertEquals("Agreement cannot be deleted, 1 is not found", exception.getMessage());
    }
    @Test
    void testDeleteAgreement_shouldSaveAgreementEntityWithStatusInactive() {
        //заглушки
        AgreementEntity agreementEntity = new AgreementEntity();
        AccountEntity accountEntity = new AccountEntity();
        agreementEntity.setAccount(accountEntity);
        when(agreementRepository.findById(any())).thenReturn(Optional.of(agreementEntity));

        agreementService.deleteAgreement(1L);
       verify(agreementRepository,atLeastOnce()).save(any());
       verify(accountService,atLeastOnce()).deleteAccount(any());
       assertEquals(agreementEntity.getStatus(),INACTIVE);
    }
}