package com.example.banknew.service.impl;

import com.example.banknew.dtos.ClientDto;
import com.example.banknew.dtos.ManagerDto;
import com.example.banknew.entities.ClientEntity;
import com.example.banknew.entities.ManagerEntity;
import com.example.banknew.enums.Status;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.exception.ValidationException;
import com.example.banknew.mappers.ManagerMapper;
import com.example.banknew.repository.ManagerRepository;
import com.example.banknew.service.ManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManagerServiceImpl implements ManagerService {
    private final ManagerRepository managerRepository;
    private final ManagerMapper managerMapper;

    @Override
    public List<ManagerDto> getAll() {
        return managerRepository.findAll().stream()
                .map(managerMapper::toDto)
                .toList();
    }

    @Override
    public ManagerDto getById(Long id) {
        Optional<ManagerEntity> optManagerEntity = managerRepository.findById(id);
        if (optManagerEntity.isEmpty()) {
            log.info("Manager id ={} is not found", id);
            throw new NotFoundException("Manager not found");
        }
        return managerMapper.toDto(optManagerEntity.get());
    }

    @Override
    public List<ManagerDto> findByLastName(String lastName) {
        List<ManagerEntity> managerEntities = managerRepository.findByLastName(lastName);
        if (managerEntities.isEmpty()) {
            log.info("Manager with lastName ={} is not found", lastName);
            throw new NotFoundException("Manager with this lastName not found");
        }
        return managerEntities.stream()
                .map(managerMapper::toDto)
                .toList();
    }

    @Override
    public ManagerDto createManager(ManagerDto managerDto) {
        Optional<ManagerEntity> optManager = managerRepository.findByEmail(managerDto.getEmail());
        if (optManager.isPresent()) {
            throw new NotFoundException("Email is occupied, new manager cannot be created");
        }
        ManagerEntity savedManager = managerRepository.save(managerMapper.toEntity(managerDto));
        log.info("Created and saved manager with ID= {}", savedManager.getId());
        return managerMapper.toDto(savedManager);
    }

    @Override
    public ManagerDto updateManager(Long id, ManagerDto managerDto) {
        Optional<ManagerEntity> optManagerEntity = managerRepository.findById(id);
        if (optManagerEntity.isEmpty()) {
            throw new NotFoundException("Manager " + id + " cannot be updated, " +
                    "id is not found");
        }
        ManagerEntity managerEntity = optManagerEntity.get();
        managerMapper.updateEntity(managerEntity, managerDto);
        managerRepository.save(managerEntity);
        log.info("Manager with ID {} is updated", id);
        return managerMapper.toDto(managerEntity);
    }

    @Override
    public void deleteManager(Long id) {
        Optional<ManagerEntity> optManagerEntity = managerRepository.findById(id);
        if (optManagerEntity.isEmpty()) {
            throw new NotFoundException("Manager " + id + " not found");
        }
        //managerRepository.deleteById(id);
        ManagerEntity managerEntity = optManagerEntity.get();
        managerEntity.setStatus(Status.INACTIVE);
        managerRepository.save(managerEntity);
    }
}

