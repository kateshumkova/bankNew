package com.example.banknew.service;
import com.example.banknew.dtos.ClientDto;
import com.example.banknew.dtos.ManagerDto;
import com.example.banknew.entities.ClientEntity;
import com.example.banknew.entities.ManagerEntity;

import java.util.List;

public interface ManagerService {
    List<ManagerDto> getAll();
    ManagerDto getById(Long id);
    List<ManagerDto> findByLastName(String lastName);
    ManagerDto createManager(ManagerDto managerDto);
    ManagerDto updateManager(Long id, ManagerDto managerDto);
    void deleteManager(Long id);

}
