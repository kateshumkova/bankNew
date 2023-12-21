package com.example.banknew.service;

import com.example.banknew.dtos.CreateAgreementRequest;
import com.example.banknew.dtos.AccountDto;
import com.example.banknew.entities.AccountEntity;

import java.util.List;

public interface AccountService {
    List<AccountDto> getAll();
    AccountDto getById(Long id);
    List<AccountDto> findByName(String name);
    AccountEntity createAccount();
    AccountEntity updateAccount(Long id, AccountDto accountDto);
    void deleteAccount(Long id);
}
