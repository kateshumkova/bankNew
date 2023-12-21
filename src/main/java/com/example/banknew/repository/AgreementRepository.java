package com.example.banknew.repository;

import com.example.banknew.dtos.ClientDto;
import com.example.banknew.entities.AgreementEntity;
import com.example.banknew.entities.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgreementRepository extends JpaRepository<AgreementEntity, Long> {
    List<AgreementEntity> findByClientId(Long id);
    List<AgreementEntity> findByStatus(int status);
    Optional<AgreementEntity> findById(Long id);


    List<AgreementEntity> findByAccountId(Long id);

    List<AgreementEntity> findByProductId(Long id);

    List<AgreementEntity> findByManagerId(Long id);
}

