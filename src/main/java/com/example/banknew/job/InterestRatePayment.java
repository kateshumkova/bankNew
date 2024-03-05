package com.example.banknew.job;

import com.example.banknew.entities.AccountEntity;
import com.example.banknew.entities.AgreementEntity;
import com.example.banknew.entities.ScheduleEntity;
import com.example.banknew.entities.TrxEntity;
import com.example.banknew.enums.Status;
import com.example.banknew.enums.TrxType;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.mappers.AgreementMapper;
import com.example.banknew.repository.AccountRepository;
import com.example.banknew.repository.AgreementRepository;
import com.example.banknew.repository.ScheduleRepository;
import com.example.banknew.repository.TrxRepository;
import com.example.banknew.service.AccountService;
import com.example.banknew.service.AgreementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

import static com.example.banknew.enums.PaymentStatus.NOT_PAID_OUT;
import static com.example.banknew.enums.PaymentStatus.PAID_OUT;
import static java.time.Instant.now;


@Slf4j
@RequiredArgsConstructor
@Component
public class InterestRatePayment {
    private final AgreementService agreementService;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final AgreementMapper agreementMapper;
    private final AgreementRepository agreementRepository;
    private final TrxRepository trxRepository;
    private final ScheduleRepository scheduleRepository;
    // private final RestTemplate restTemplate;

    @Transactional
    @Scheduled(cron = "0 * * * * *")//"${scheduler.cron}")
    public void runInterestPayment() {
        LocalDate date = LocalDate.now();

        List<ScheduleEntity> scheduleEntities = scheduleRepository.findByDateOfPayment(date);
        for (int i = 0; i < scheduleEntities.size(); i++) {
            ScheduleEntity scheduleEntity = scheduleEntities.get(i);
            AccountEntity accountEntity = scheduleEntity.getAccount();
            Optional<AgreementEntity> optAgreementEntity = agreementRepository.findByAccountId(accountEntity.getId());
            if (optAgreementEntity.isEmpty()) {
                throw new NotFoundException("Agreement is not found");
            }
            if (accountEntity.getStatus() == Status.ACTIVE && scheduleEntity.getPaymentStatus() == NOT_PAID_OUT) {

                BigDecimal currentBalance = accountEntity.getBalance();
                AgreementEntity agreementEntity = optAgreementEntity.get();
                BigDecimal interestAmount = currentBalance.multiply(BigDecimal.valueOf(agreementEntity.getInterestRate())).divide(BigDecimal.valueOf(100));
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
                // restTemplate.exchange()
            }
        }
    }
}
