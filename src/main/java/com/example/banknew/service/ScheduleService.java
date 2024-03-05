package com.example.banknew.service;

import com.example.banknew.entities.AccountEntity;
import com.example.banknew.entities.ScheduleEntity;

import java.util.List;

public interface ScheduleService {
    void createPaymentSchedule(AccountEntity accountEntity);
    List<ScheduleEntity> getByAccountId(Long accountId);

}
