package com.example.banknew.service;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.entities.AccountEntity;

import java.util.List;

public interface ScheduleService {
    void createScheduleForInterestPayment(AccountEntity accountEntity);

}
