package com.example.banknew.service.impl;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.ManagerDto;
import com.example.banknew.entities.AccountEntity;
import com.example.banknew.entities.ManagerEntity;
import com.example.banknew.enums.Status;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.mappers.AccountMapper;
import com.example.banknew.repository.AccountRepository;
import com.example.banknew.repository.AgreementRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
class AccountServiceImplTest {
    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper accountMapper;

    @Test
    void testGetAll_shouldReturnListAccountDto() {
        //заглушки
        // AccountEntity accountEntity = new AccountEntity();
        // when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntity));

        //вызов метода
        List<AccountDto> actual = accountService.getAll();

        //проверка результата
//        verify(accountMapper,atLeast(1)).toDto(any());
        verify(accountRepository, atLeast(1)).findAll();
        assertNotNull(actual);
    }


    @Test
    void testGetById_shouldNotFoundException_ifEmptyAccount() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> accountService.getById(1L));
        assertEquals("Account id = 1 is not found", exception.getMessage());
    }

    @Test
    void testGetById_shouldReturnAccountDto_ifNotEmptyAccount() {
        //заглушки
        AccountEntity accountEntity = new AccountEntity();
        when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntity));

        //вызов метода
        AccountDto actual = accountService.getById(1L);

        //проверка результата
        verify(accountMapper, atLeast(1)).toDto(any());
//        assertNotNull(actual);
    }

    @Test
    void testFindByName_shouldNotFoundException_ifEmptyAccount() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> accountService.findByName("Name"));
        assertEquals("Account with name = Name is not found", exception.getMessage());
    }

    @Test
    void testFindByName_shouldReturnListAccountDto_ifNotEmptyAccount() {
        //заглушки
        AccountEntity accountEntity = new AccountEntity();
        when(accountRepository.findByName(any())).thenReturn(List.of(accountEntity));

        //вызов метода
        List<AccountDto> actual = accountService.findByName("Name");

        //проверка результата
        verify(accountMapper, atLeast(1)).toDto(any());
//        assertNotNull(actual);
    }

    //    @Test
//    void testCreateAccount_shouldNotFoundException_ifEmptyAccount() {
//        NotFoundException exception = assertThrows(NotFoundException.class, () -> accountService.createAccount());
//        assertEquals("Account id = 1 is not found", exception.getMessage());
//    }
    @Test
    void testCreateAccount_shouldReturnAccountEntity() {
        //zaglushki
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        accountEntity.setBalance(new BigDecimal(0));
        accountEntity.setName("Name");//productEntity.getName() + "_" + clientEntity.getId());
        accountEntity.setStatus(Status.ACTIVE);
        AccountEntity savedAccountEntity = accountRepository.saveAndFlush(accountEntity);
        //  when(accountRepository.saveAndFlush(accountEntity)).thenReturn(savedAccountEntity);

        //вызов метода
        AccountEntity actual = accountService.createAccount();

        //проверка результата

        //  verify(managerMapper,atLeast(1)).toDto(any());
        verify(accountRepository).saveAndFlush(any());
        // assertNotNull(actual);
    }

    @Test
    void testUpdateAccount_shouldNotFoundException_ifEmptyAccount() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> accountService.updateAccount(1L, new AccountDto()));
        assertEquals("Account id = 1 ,cannot be updated, id is not found", exception.getMessage());
    }

    @Test
    void testUpdateAccount_shouldReturnAccountEntity_ifNotEmptyAccount() {
        //заглушки
        AccountEntity accountEntity = new AccountEntity();
        when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntity));

        //вызов метода
        AccountDto actual = accountService.updateAccount(1L, new AccountDto());

        //проверка результата
        verify(accountMapper, atLeast(1)).updateEntity(any(), any());
        verify(accountRepository).save(any());
//        assertNotNull(actual);
    }

    @Test
    void testDeleteAccount_shouldNotFoundException_ifEmptyAccount() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> accountService.deleteAccount(1L));
        assertEquals("Account id is not found", exception.getMessage());
    }

    @Test
    void testDeleteAccount_shouldChangeStatusToInactive_ifPresentAccount() {
        //заглушки
        AccountEntity accountEntity = new AccountEntity();
        when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntity));

        //вызов метода
        accountService.deleteAccount(1L);

        //проверка результата
        // verify(accountMapper,atLeast(1)).updateEntity(any(), any());
        verify(accountRepository).save(any());
//        assertNotNull(actual);
    }
}