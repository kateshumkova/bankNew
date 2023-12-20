package com.example.bank_project.service.impl;

import com.example.bank_project.dtos.CreateClientUserDto;
import com.example.bank_project.dtos.UserDto;
import com.example.bank_project.entities.ClientEntity;
import com.example.bank_project.entities.RoleEntity;
import com.example.bank_project.entities.UserEntity;
import com.example.bank_project.entities.UserRoleEntity;
import com.example.bank_project.exception.NotFoundException;
import com.example.bank_project.repository.ClientRepository;
import com.example.bank_project.repository.RoleRepository;
import com.example.bank_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    public final UserRepository userRepository;
    public final RoleRepository roleRepository;
    public final PasswordEncoder passwordEncoder;
    public final ClientRepository clientRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optUserEntity = userRepository.findByUsername(username);
        if (optUserEntity.isEmpty()) {
            throw new NotFoundException( "User not found");
        }
        UserEntity user = optUserEntity.get();
        return new User(user.getUsername(),
                user.getPassword(),
                user.getAuthorities());
    }
    public void createClientUser(CreateClientUserDto createClientUserDto) {
        Optional<RoleEntity> roleFromDb = roleRepository.findByName(createClientUserDto.getRoleName());
        if (roleFromDb.isEmpty()) {
            throw new NotFoundException("Role not found, user cannot be created");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(createClientUserDto.getUsername());
        userEntity.setPassword(passwordEncoder.encode(createClientUserDto.getPassword()));
        userEntity.setRole(roleFromDb.get());
        Optional<ClientEntity> optClientFromDb = clientRepository.findById(createClientUserDto.getClientId());
        if (optClientFromDb.isEmpty()) {
            throw new NotFoundException("Client not found, user cannot be created");
        }
        ClientEntity clientEntity = optClientFromDb.get();

        UserEntity savedUser = userRepository.save(userEntity);
        clientEntity.setUser(savedUser);
        log.info("User with id {} is created", savedUser.getId() );
    }

    public void createUser(UserDto userDto) {
        Optional<RoleEntity> roleFromDb = roleRepository.findByName(userDto.getRoleName());
        if (roleFromDb.isEmpty()) {
            throw new NotFoundException("Role not found, user cannot be created");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userDto.getUsername());
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userEntity.setRole(roleFromDb.get());
       // Optional<ClientEntity> optClientFromDb = clientRepository.findById(userDto.getClientId());
      //  if (optClientFromDb.isEmpty()) {
      //      throw new NotFoundException("Client not found, user cannot be created");
      //  }
      //  ClientEntity clientEntity = optClientFromDb.get();

        UserEntity savedUser = userRepository.save(userEntity);
       // clientEntity.setUser(savedUser);
        log.info("User with id {} is created", savedUser.getId() );
    }
}
