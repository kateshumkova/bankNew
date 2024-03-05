package com.example.banknew.service.impl;

import com.example.banknew.entities.AccountEntity;
import com.example.banknew.entities.AgreementEntity;
import com.example.banknew.entities.ProductEntity;
import com.example.banknew.enums.Status;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.repository.AgreementRepository;
import com.example.banknew.repository.ProductRepository;
import com.example.banknew.repository.ScheduleRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ScheduleServiceImplTest {
    @InjectMocks
    private ScheduleServiceImpl scheduleService;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private AgreementRepository agreementRepository;
    @Mock
    private ProductRepository productRepository;
    @Test
    void createScheduleForInterestPayment_happyPath() {
        AgreementEntity agreementEntity = new AgreementEntity();
        ProductEntity productEntity = new ProductEntity();
        productEntity.setDepositPeriod(12);
        productEntity.setPaymentFrequency(3);
        agreementEntity.setProduct(productEntity);
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        agreementEntity.setAccount(accountEntity);
        when(agreementRepository.findByAccountId(any())).thenReturn(Optional.of(agreementEntity));
        when(productRepository.findById(any())).thenReturn(Optional.of(productEntity));

        //вызов метода
       scheduleService.createPaymentSchedule(accountEntity);

        //проверка результата
        verify(scheduleRepository, atLeast(1)).save(any());


    }
    @Test
    void createScheduleForInterestPayment_shouldNotFoundException_ifAgreementEmpty() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        accountEntity.setStatus(Status.ACTIVE);
        accountEntity.setCreatedAt(Instant.now());
        when(agreementRepository.findByAccountId(any())).thenReturn(Optional.ofNullable(null));
     //   when(productRepository.findById(any())).thenReturn(Optional.of(new ProductEntity()));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> scheduleService.createPaymentSchedule(accountEntity));
        assertEquals("No agreement with such accountId", exception.getMessage());
    }
    @Test
    void createScheduleForInterestPayment_shouldNotFoundException_ifProductEmpty() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        accountEntity.setStatus(Status.ACTIVE);
        accountEntity.setCreatedAt(Instant.now());
        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setProduct(new ProductEntity());
        when(agreementRepository.findByAccountId(any())).thenReturn( Optional.of(agreementEntity));
        when(productRepository.findById(any())).thenReturn(Optional.ofNullable(null));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> scheduleService.createPaymentSchedule(accountEntity));
        assertEquals("No product with such productId", exception.getMessage());
    }
}