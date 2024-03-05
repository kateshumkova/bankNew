package com.example.banknew.job;

import com.example.banknew.entities.AccountEntity;
import com.example.banknew.entities.AgreementEntity;
import com.example.banknew.entities.ScheduleEntity;
import com.example.banknew.enums.Status;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.repository.AccountRepository;
import com.example.banknew.repository.AgreementRepository;
import com.example.banknew.repository.ScheduleRepository;
import com.example.banknew.repository.TrxRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.example.banknew.enums.PaymentStatus.NOT_PAID_OUT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class InterestRatePaymentTest {
    @InjectMocks
    private InterestRatePayment interestRatePayment;

    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private AgreementRepository agreementRepository;
    @Mock
    private TrxRepository trxRepository;
    @Mock
    private AccountRepository accountRepository;

    @Test
    void runInterestPayment_happyPath() {
        ScheduleEntity scheduleEntity = new ScheduleEntity();
        ScheduleEntity scheduleEntity1 = new ScheduleEntity();
        AccountEntity accountEntity = new AccountEntity();
        AccountEntity accountEntity1 = new AccountEntity();
        accountEntity.setId(1L);
        accountEntity.setBalance(BigDecimal.valueOf(1000));
        accountEntity1.setBalance(BigDecimal.valueOf(1000));
        accountEntity.setStatus(Status.ACTIVE);
        accountEntity1.setId(2L);
        accountEntity.setStatus(Status.ACTIVE);
        scheduleEntity.setAccount(accountEntity);
        scheduleEntity1.setAccount(accountEntity1);
        when(scheduleRepository.findByDateOfPayment(any())).thenReturn(List.of(scheduleEntity, scheduleEntity1));

        AgreementEntity agreementEntity = new AgreementEntity();
        when(agreementRepository.findByAccountId(any())).thenReturn(Optional.of(agreementEntity));
        scheduleEntity.setPaymentStatus(NOT_PAID_OUT);

        //вызов метода
        interestRatePayment.runInterestPayment();

        //проверка результата
        verify(scheduleRepository, atLeast(1)).save(any());
    }
  @Test
    void runInterestPayment_shouldNotFoundException_whenEmptyAgreement() {
        ScheduleEntity scheduleEntity = new ScheduleEntity();
        ScheduleEntity scheduleEntity1 = new ScheduleEntity();
        AccountEntity accountEntity = new AccountEntity();
        AccountEntity accountEntity1 = new AccountEntity();
        accountEntity.setId(1L);
        accountEntity.setBalance(BigDecimal.valueOf(1000));
        accountEntity1.setBalance(BigDecimal.valueOf(1000));
        accountEntity.setStatus(Status.ACTIVE);
        accountEntity.setId(2L);
        accountEntity.setStatus(Status.ACTIVE);
        scheduleEntity.setAccount(accountEntity);
        scheduleEntity1.setAccount(accountEntity1);
        when(scheduleRepository.findByDateOfPayment(any())).thenReturn(List.of(scheduleEntity, scheduleEntity1));
        AgreementEntity agreementEntity = new AgreementEntity();
        when(agreementRepository.findByAccountId(any())).thenReturn(Optional.of(agreementEntity));
        scheduleEntity.setPaymentStatus(NOT_PAID_OUT);

        when(agreementRepository.findByAccountId(any())).thenReturn(Optional.ofNullable(null));


        NotFoundException exception = assertThrows(NotFoundException.class, () -> interestRatePayment.runInterestPayment());
        assertEquals("Agreement is not found", exception.getMessage());
    }
}