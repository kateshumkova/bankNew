package com.example.banknew.repository;
import com.example.banknew.entities.TrxEntity;
import com.example.banknew.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrxRepository extends JpaRepository<TrxEntity, Long> {
    List<TrxEntity> findByStatus(Status status);

    Optional<TrxEntity> findById(Long id);

    // @Query(value = "SELECT ce FROM ClientEntity ce where ce.email=:email")
    List<TrxEntity> findByAccountId(Long id);
}
