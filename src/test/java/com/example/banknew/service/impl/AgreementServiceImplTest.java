package com.example.banknew.service.impl;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.AgreementDto;
import com.example.banknew.entities.AccountEntity;
import com.example.banknew.entities.AgreementEntity;
import com.example.banknew.enums.Status;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.mappers.AgreementMapper;
import com.example.banknew.repository.AgreementRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static com.example.banknew.enums.Status.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
class AgreementServiceImplTest {

    @InjectMocks
    private AgreementServiceImpl agreementService;

    @Mock
    private AgreementRepository agreementRepository;
    @Mock
    private AgreementMapper agreementMapper;

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
        when(agreementRepository.findByAccountId(any())).thenReturn(agreementEntity);

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
}