package com.example.banknew.service.impl;

import com.example.banknew.dtos.AgreementDto;
import com.example.banknew.entities.RoleEntity;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringBootTest
class AuthServiceImplTest {
    @InjectMocks
    private AuthServiceImpl authService;
    @Mock
    private AccountServiceImpl accountService;

    @Test
    void testCheckRole_shouldBeTrue() {
        //заглушки
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("ROLE_USER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));

        //вызов метода
        boolean actual = authService.checkRole(authentication, "ROLE_USER");
        //проверка результата
        assertTrue(actual);
    }

    @Test
    void testCheckRole_shouldBeFalse() {
        //заглушки
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("ROLE_USER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));

        //вызов метода
        boolean actual = authService.checkRole(authentication, "ROLE_MANAGER");
        //проверка результата
        assertFalse(actual);
    }
}