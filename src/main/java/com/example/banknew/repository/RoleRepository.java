package com.example.banknew.repository;

import com.example.banknew.entities.RoleEntity;
import com.example.banknew.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(String username);
    Optional<RoleEntity> findById(Long id);
}
