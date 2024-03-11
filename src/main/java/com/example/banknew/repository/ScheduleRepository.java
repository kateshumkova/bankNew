package com.example.banknew.repository;

import com.example.banknew.entities.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
    List<ScheduleEntity> findByDateOfPayment(LocalDate date);
    List<ScheduleEntity> findByAccountId(Long accountId);

}
