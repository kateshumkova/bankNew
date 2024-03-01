package com.example.banknew.job;

import com.example.banknew.dtos.AgreementDto;
import com.example.banknew.entities.AccountEntity;
import com.example.banknew.entities.AgreementEntity;
import com.example.banknew.entities.InterestRatePaymentScheduleEntity;
import com.example.banknew.entities.TrxEntity;
import com.example.banknew.enums.PaymentStatus;
import com.example.banknew.enums.Status;
import com.example.banknew.enums.TrxType;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.exception.ValidationException;
import com.example.banknew.mappers.AgreementMapper;
import com.example.banknew.repository.AccountRepository;
import com.example.banknew.repository.AgreementRepository;
import com.example.banknew.repository.ScheduleRepository;
import com.example.banknew.repository.TrxRepository;
import com.example.banknew.service.AccountService;
import com.example.banknew.service.AgreementService;
import com.example.banknew.service.ScheduleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

import static com.example.banknew.enums.PaymentStatus.NOT_PAID_OUT;
import static com.example.banknew.enums.PaymentStatus.PAID_OUT;
import static java.time.Instant.now;


@Slf4j
@RequiredArgsConstructor
@Component
@Configuration
@EnableScheduling
public class InterestRatePayment {
    private final AgreementService agreementService;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final AgreementMapper agreementMapper;
    private final AgreementRepository agreementRepository;
    private final TrxRepository trxRepository;
    private final ScheduleRepository scheduleRepository;
    private static final String PATTERN_FORMAT = "yyyy-MM-dd";
//    @Scheduled(cron = "0 0 L * * ?")
//    @Transactional
//    public void run() {
//        try { //to Entity
//            List<AgreementDto> agreementDtos = agreementService.findAllActive();
//            for (int i = 0; i < agreementDtos.size(); i++) {
//                AgreementDto agreementDto = agreementDtos.get(i);
//                Optional<AccountEntity> optAccountEntity = accountRepository.findById(agreementDto.getAccountId());
//                AccountEntity accountEntity = optAccountEntity.get();
//                BigDecimal currentBalance = accountEntity.getBalance();
//                BigDecimal interestRate = currentBalance.multiply(BigDecimal.valueOf(agreementMapper.toEntity(agreementDto).getInterestRate() / 100 / 12))
//                        .multiply(BigDecimal.valueOf(agreementMapper.toEntity(agreementDto).getProduct().getPaymentFrequency()));
//                TrxEntity trxEntity = new TrxEntity();
//                trxEntity.setAccount(accountEntity);
//                trxEntity.setStatus(Status.ACTIVE);
//                trxEntity.setTrxType(TrxType.DEBIT);
//                trxEntity.setDescription("Interest Rate Payout");
//                trxEntity.setAmount(interestRate);
//                TrxEntity savedTrxEntity = trxRepository.saveAndFlush(trxEntity);
//
//                accountEntity.setBalance(currentBalance.add(interestRate));
//                AccountEntity savedAccountEntity = accountRepository.saveAndFlush(accountEntity);
//
//            }
//
//        } catch (Exception e) {
//            log.error("Error during interest rate payout", e);
//        }
//    }

    // @Scheduled(cron = "0 0 L * * ?")
    // @Scheduled(cron =  "0 * * * * *")
    @Transactional
    // @Scheduled(cron = "@daily")
    @Scheduled(cron = "0 * * * * *")
    public void run_interest_payment() {
        //  try {
        LocalDate date = LocalDate.now();

        List<InterestRatePaymentScheduleEntity> scheduleEntities = scheduleRepository.findByDateOfPayment(date);
        for (int i = 0; i < scheduleEntities.size(); i++) {
            InterestRatePaymentScheduleEntity scheduleEntity = scheduleEntities.get(i);
            AccountEntity accountEntity = scheduleEntity.getAccount();
            if (accountEntity.getStatus() == Status.INACTIVE) {
                throw new ValidationException("This account is unactive");
            }

            Optional<AgreementEntity> optAgreementEntity = agreementRepository.findByAccountId(accountEntity.getId());
            if (optAgreementEntity.isEmpty()) {
                throw new NotFoundException("Agreement is not found");
            }

            if (scheduleEntity.getPaymentStatus() == NOT_PAID_OUT) {

                BigDecimal currentBalance = accountEntity.getBalance();
                AgreementEntity agreementEntity = optAgreementEntity.get();
                BigDecimal interestAmount = currentBalance.multiply(BigDecimal.valueOf(agreementEntity.getInterestRate()));
                scheduleEntity.setInterestAmount(interestAmount);

                TrxEntity trxEntity = new TrxEntity();
                trxEntity.setAccount(accountEntity);
                trxEntity.setStatus(Status.ACTIVE);
                trxEntity.setTrxType(TrxType.DEBIT);
                trxEntity.setDescription("Interest Rate Payout");
                trxEntity.setAmount(interestAmount);
                TrxEntity savedTrxEntity = trxRepository.saveAndFlush(trxEntity);

                accountEntity.setBalance(currentBalance.add(interestAmount));
                AccountEntity savedAccountEntity = accountRepository.saveAndFlush(accountEntity);
                scheduleEntity.setPaymentStatus(PAID_OUT);
                scheduleRepository.save(scheduleEntity);
            }

            //    } catch (Exception e) {
            //        log.error("Error during interest rate payout", e);
            //    }
        }
    }

}
