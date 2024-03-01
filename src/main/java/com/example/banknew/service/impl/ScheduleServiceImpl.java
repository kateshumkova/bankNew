package com.example.banknew.service.impl;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.entities.AccountEntity;
import com.example.banknew.entities.AgreementEntity;
import com.example.banknew.entities.InterestRatePaymentScheduleEntity;
import com.example.banknew.entities.ProductEntity;
import com.example.banknew.enums.PaymentStatus;
import com.example.banknew.enums.Status;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.mappers.AccountMapper;
import com.example.banknew.mappers.AgreementMapper;
import com.example.banknew.mappers.ClientMapper;
import com.example.banknew.repository.*;
import com.example.banknew.service.AccountService;
import com.example.banknew.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.time.LocalTime.now;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AgreementRepository agreementRepository;
    private final AgreementMapper agreementMapper;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final ClientMapper clientMapper;
    private final ScheduleRepository scheduleRepository;

    @Override
    public void createScheduleForInterestPayment(AccountEntity accountEntity) {
        var dateOfCreationOfAccount = accountEntity.getCreatedAt();
        Optional<AgreementEntity> optionalAgreementEntity = agreementRepository.findByAccountId(accountEntity.getId());
        if (optionalAgreementEntity.isEmpty()) {
            throw new NotFoundException("No agreement with such accountId");
        }
        AgreementEntity agreementEntity = optionalAgreementEntity.get();
        var maturityDate = agreementEntity.getMaturityDate();
        Optional<ProductEntity> optProductEntity = productRepository.findById(agreementEntity.getProduct().getId());
        if (optProductEntity.isEmpty()) {
            throw new NotFoundException("No product with such productId");
        }
        ProductEntity productEntity = optProductEntity.get();

        for (int i = 0; i < productEntity.getDepositPeriod(); i += productEntity.getPaymentFrequency()) {
            productEntity.getPaymentFrequency();
            InterestRatePaymentScheduleEntity interestRatePaymentScheduleEntity = new InterestRatePaymentScheduleEntity();
            interestRatePaymentScheduleEntity.setPaymentStatus(PaymentStatus.NOT_PAID_OUT);
            interestRatePaymentScheduleEntity.setInterestAmount(BigDecimal.ZERO);
            interestRatePaymentScheduleEntity.setDateOfPayment(LocalDate.now().plusMonths(i));
            interestRatePaymentScheduleEntity.setAccount(accountEntity);
            InterestRatePaymentScheduleEntity entity = scheduleRepository.save(interestRatePaymentScheduleEntity);

        }
    }

}
