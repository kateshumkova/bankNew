package com.example.banknew.repository;

import com.example.banknew.entities.AccountEntity;
import com.example.banknew.entities.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    List<AccountEntity> findByName(String name);

    Optional<AccountEntity> findById(Long id);

    // @Query(value = "SELECT ce FROM ClientEntity ce where ce.email=:email")
   // Optional<AccountEntity> getByClientId(Long id);
}
