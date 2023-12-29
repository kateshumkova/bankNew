package com.example.banknew.job;

import com.example.banknew.dtos.AgreementDto;
import com.example.banknew.entities.AccountEntity;
import com.example.banknew.entities.TrxEntity;
import com.example.banknew.enums.Status;
import com.example.banknew.enums.TrxType;
import com.example.banknew.mappers.AgreementMapper;
import com.example.banknew.repository.AccountRepository;
import com.example.banknew.repository.TrxRepository;
import com.example.banknew.service.AccountService;
import com.example.banknew.service.AgreementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class InterestRatePayment {
    private final AgreementService agreementService;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final AgreementMapper agreementMapper;
    private final TrxRepository trxRepository;

    @Scheduled(cron = "0 0 L * * ?")
    @Transactional
    public void run() {
        try { //to Entity
            List<AgreementDto> agreementDtos = agreementService.findAllActive();
            for (int i = 0; i < agreementDtos.size(); i++) {
                AgreementDto agreementDto = agreementDtos.get(i);
                Optional<AccountEntity> optAccountEntity = accountRepository.findById(agreementDto.getAccountId());
                AccountEntity accountEntity = optAccountEntity.get();
                BigDecimal currentBalance = accountEntity.getBalance();
                BigDecimal interestRate = currentBalance.multiply(BigDecimal.valueOf(agreementMapper.toEntity(agreementDto).getInterestRate() / 100 / 12))
                        .multiply(BigDecimal.valueOf(agreementMapper.toEntity(agreementDto).getProduct().getPaymentFrequency()));
                TrxEntity trxEntity = new TrxEntity();
                trxEntity.setAccount(accountEntity);
                trxEntity.setStatus(Status.ACTIVE);
                trxEntity.setTrxType(TrxType.DEBIT);
                trxEntity.setDescription("Interest Rate Payout");
                trxEntity.setAmount(interestRate);
                TrxEntity savedTrxEntity = trxRepository.saveAndFlush(trxEntity);

                accountEntity.setBalance(currentBalance.add(interestRate));
                AccountEntity savedAccountEntity = accountRepository.saveAndFlush(accountEntity);

            }

        } catch (Exception e) {
            log.error("Error during interest rate payout", e);
        }
    }
}
