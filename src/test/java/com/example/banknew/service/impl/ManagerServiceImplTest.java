package com.example.banknew.service.impl;

import com.example.banknew.dtos.*;
import com.example.banknew.entities.*;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.mappers.ManagerMapper;
import com.example.banknew.repository.ClientRepository;
import com.example.banknew.repository.ManagerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ManagerServiceImplTest {
    @InjectMocks
    private ManagerServiceImpl managerService;

    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private ManagerMapper managerMapper;

    @Test
    void testGetAll_shouldReturnListManagerDto() {
        //заглушки
        // AccountEntity accountEntity = new AccountEntity();
        // when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntity));

        //вызов метода
        List<ManagerDto> actual = managerService.getAll();

        //проверка результата
//        verify(accountMapper,atLeast(1)).toDto(any());
        verify(managerRepository, atLeast(1)).findAll();
        assertNotNull(actual);
    }

    @Test
    void testGetById_shouldNotFoundException_ifEmptyManager() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> managerService.getById(1L));
        assertEquals("Manager not found", exception.getMessage());
    }
    @Test
    void testGetById_shouldReturnManagerDto_ifNotEmptyManager() {
        //заглушки
        ManagerEntity managerEntity = new ManagerEntity();
        when(managerRepository.findById(any())).thenReturn(Optional.of(managerEntity));

        //вызов метода
        ManagerDto actual = managerService.getById(1L);

        //проверка результата
        verify(managerMapper, atLeast(1)).toDto(any());
        //?????  assertNotNull(actual);
    }
    @Test
    void testFindByLastName_shouldNotFoundException_ifEmptyManager() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> managerService.findByLastName("Einstein"));
        assertEquals("Manager with this lastName not found", exception.getMessage());
    }

    @Test
    void testFindByLastName_shouldReturnManagerDto_ifNotEmptyManager() {
        //заглушки
        ManagerEntity managerEntity = new ManagerEntity();
        when(managerRepository.findByLastName(any())).thenReturn(List.of(managerEntity));

        //вызов метода
        List<ManagerDto> actual = managerService.findByLastName("Simpson");

        //проверка результата
        verify(managerMapper, atLeast(1)).toDto(any());
//        assertNotNull(actual);
    }
    @Test
    void testCreateManager_shouldNotFoundException_ifPresentOptManagerWithEmail() {
        ManagerDto managerDto = new ManagerDto();
        ManagerEntity managerEntity = new ManagerEntity();
        when(managerRepository.findByEmail(managerDto.getEmail())).thenReturn(Optional.of(managerEntity));

        //вызов метода
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                managerService.createManager(managerDto));

        //проверка результата
        assertEquals("Email is occupied, new manager cannot be created", exception.getMessage());
    }

    @Test
    void testCreateManager_shouldReturnManagerDto_ifOptManagerWithEmailIsEmpty() {
        ManagerDto managerDto = new ManagerDto();
        ManagerEntity savedManager =  new ManagerEntity();
        when(managerRepository.findByEmail(managerDto.getEmail())).thenReturn(Optional.ofNullable(null));
        when(managerRepository.save(managerMapper.toEntity(managerDto))).thenReturn(savedManager);

        //вызов метода
        ManagerDto actual = managerService.createManager(managerDto);

        //проверка результата
        verify(managerMapper,atLeast(1)).toEntity(any());
        verify(managerMapper,atLeast(1)).toDto(any());
        verify(managerRepository).save(any());
       //  assertNotNull(actual);
        }

    // assertNotNull(actual);
    @Test
    void testUpdateManager_shouldNotFoundException_ifEmptyManager() {
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                managerService.updateManager(1L, new ManagerDto()));
        assertEquals("Manager 1 cannot be updated, id is not found", exception.getMessage());
    }

    @Test
    void testUpdateManager_shouldReturnManagerEntity_ifNotEmptyManager() {
        //заглушки
        ManagerEntity managerEntity = new ManagerEntity();
        when(managerRepository.findById(any())).thenReturn(Optional.of(managerEntity));

        //вызов метода
        ManagerDto actual = managerService.updateManager(1L, new ManagerDto());

        //проверка результата
        verify(managerMapper, atLeast(1)).updateEntity(any(), any());
        verify(managerRepository).save(any());
//        assertNotNull(actual);
    }

    @Test
    void testDeleteManager_shouldNotFoundException_ifEmptyManager() {
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                managerService.deleteManager(1L));
        assertEquals("Manager 1 not found", exception.getMessage());
    }
    @Test
    void testDeleteManager_shouldChangeStatusToInactive_ifPresentManager() {
        //заглушки
        ManagerEntity managerEntity = new ManagerEntity();
        when(managerRepository.findById(any())).thenReturn(Optional.of(managerEntity));

        //вызов метода
        managerService.deleteManager(1L);

        //проверка результата
        // verify(accountMapper,atLeast(1)).updateEntity(any(), any());
        verify(managerRepository).save(any());
//        assertNotNull(actual);
    }
}