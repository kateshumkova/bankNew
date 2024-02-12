package com.example.banknew.service.impl;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.UserDto;
import com.example.banknew.entities.RoleEntity;
import com.example.banknew.entities.UserEntity;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.repository.RoleRepository;
import com.example.banknew.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    //todo
    @Test
    void testLoadUserByUsername_shouldReturnNewUserEntity() {
        //заглушки
        UserEntity userEntity = new UserEntity();
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(1L);
        roleEntity.setName("ROLE_ADMIN");
        userEntity.setUsername("viki@gmail.com");
        userEntity.setPassword("qwerty");
        userEntity.setRole(roleEntity);

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));

        //вызов метода
        UserDetails actual = userService.loadUserByUsername("viki@gmail.com");

        //проверка результата

        verify(userRepository, atLeast(1)).findByUsername(any());

        assertEquals(userEntity.getUsername(), actual.getUsername());
        assertEquals(userEntity.getPassword(), actual.getPassword());
        assertTrue(actual.getClass() == User.class);
        assertTrue(actual instanceof UserDetails);

    }

    @Test
    void testLoadUserByUsername_shouldReturnNotFoundException_ifUserIsEmpty() {
        //заглушки

        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        //проверка результата

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.loadUserByUsername("viki@gmail.com"));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testCreateUser_shouldReturnNotFoundException_ifRoleIsEmpty() {
        //заглушки
        UserDto userDto = new UserDto();

        when(roleRepository.findByName(any())).thenReturn(Optional.ofNullable(null));

        //проверка результата

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.createUser(userDto));
        assertEquals("Role not found, user cannot be created", exception.getMessage());
    }

    @Test
    void testCreateUser_HappyPath() {
        //заглушки
        UserDto userDto = new UserDto();
        RoleEntity roleEntity = new RoleEntity();
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new UserEntity()));
        when(roleRepository.findByName(any())).thenReturn(Optional.of(roleEntity));
        when(userRepository.saveAndFlush(any())).thenReturn(new UserEntity());
        //вызов метода
        userService.createUser(userDto);

        //проверка результата

        verify(userRepository, atLeast(1)).saveAndFlush(any());
    }

}