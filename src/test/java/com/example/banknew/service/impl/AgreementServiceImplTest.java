package com.example.banknew.service.impl;

import com.example.banknew.exception.NotFoundException;
import com.example.banknew.repository.AgreementRepository;
import com.example.banknew.service.AgreementService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AgreementServiceImplTest {

    @InjectMocks
    private AgreementServiceImpl agreementService;

    @Mock
    private AgreementRepository agreementRepository;

    @Test
    void testFindByClientId_shouldNotFoundException_ifEmptyAgreements() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> agreementService.findByClientId(1L));
        assertEquals("Agreement for client id doesn't exist", exception.getMessage());
    }
}