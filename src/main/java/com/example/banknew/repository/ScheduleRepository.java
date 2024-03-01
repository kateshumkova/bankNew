package com.example.banknew.repository;

import com.example.banknew.entities.AccountEntity;
import com.example.banknew.entities.InterestRatePaymentScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<InterestRatePaymentScheduleEntity, Long> {
    List<InterestRatePaymentScheduleEntity> findByDateOfPayment(LocalDate date);

}
