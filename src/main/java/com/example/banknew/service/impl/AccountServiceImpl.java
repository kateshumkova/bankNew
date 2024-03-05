package com.example.banknew.service.impl;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.entities.*;
import com.example.banknew.enums.Status;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.mappers.AccountMapper;
import com.example.banknew.mappers.AgreementMapper;
import com.example.banknew.mappers.ClientMapper;
import com.example.banknew.repository.*;
import com.example.banknew.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.example.banknew.enums.PaymentStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AgreementRepository agreementRepository;
    private final AgreementMapper agreementMapper;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final ClientMapper clientMapper;
    private final ScheduleRepository scheduleRepository;

    @Override
    public List<AccountDto> getAll() {
        return accountRepository.findAll().stream().map(accountMapper::toDto).toList();
    }

    @Override
    public AccountDto getById(Long id) {
        Optional<AccountEntity> optAccountEntity = accountRepository.findById(id);
        if (optAccountEntity.isEmpty()) {
            throw new NotFoundException("Account id = " + id + " is not found");
        }
        return accountMapper.toDto(optAccountEntity.get());
    }

    @Override
    public List<AccountDto> findByName(String name) {
        List<AccountEntity> accountEntities = accountRepository.findByName(name);
        if (accountEntities.isEmpty()) {
            throw new NotFoundException("Account with name = " + name + " is not found");
        }
        return accountEntities.stream().map(accountMapper::toDto).toList();
    }

    @Transactional
    public AccountEntity createAccount() {

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setBalance(new BigDecimal(0));
        accountEntity.setName("Name");//productEntity.getName() + "_" + clientEntity.getId());
        accountEntity.setStatus(Status.ACTIVE);

        AccountEntity savedAccountEntity = accountRepository.saveAndFlush(accountEntity);
        log.info("Account with ID {} is created ", savedAccountEntity.getId());
        return savedAccountEntity;
    }

    @Override
    public AccountDto updateAccount(Long id, AccountDto accountDto) {
        Optional<AccountEntity> optAccountEntity = accountRepository.findById(id);
        if (optAccountEntity.isEmpty()) {
            throw new NotFoundException("Account id = " + id + " ,cannot be updated, id is not found");
        }
        AccountEntity accountEntity = optAccountEntity.get();
        accountMapper.updateEntity(accountEntity, accountDto);
        accountRepository.save(accountEntity);
        log.info("Account with ID {} is updated ", id);
        return accountMapper.toDto(accountEntity);
    }

    @Transactional
    @Override
    public void deleteAccount(Long id) {
        Optional<AccountEntity> optAccountEntity = accountRepository.findById(id);
        if (optAccountEntity.isEmpty()) {
            throw new NotFoundException("Account id is not found");
        }
        AccountEntity accountEntity = optAccountEntity.get();
        accountEntity.setStatus(Status.INACTIVE);
        accountRepository.save(accountEntity);
        List<ScheduleEntity> scheduleEntities = scheduleRepository.findByAccountId(id);

        for (int i = 0; i < scheduleEntities.size(); i++) {
            ScheduleEntity scheduleEntity = scheduleEntities.get(i);
            if ((scheduleEntity.getAccount().getStatus() == Status.INACTIVE) && (scheduleEntity.getDateOfPayment().isAfter(LocalDate.now()))) {
                scheduleEntity.setPaymentStatus(CANCELED);
                scheduleRepository.save(scheduleEntity);
                log.info("Status of account id = {} is changed to inactive or 0", id);
                log.info("Schedule of Interest Payments is changed, because of account closing");
            }
        }
    }
}
