package com.example.banknew.service.impl;

import com.example.banknew.dtos.CreateClientUserDto;
import com.example.banknew.dtos.UserDto;
import com.example.banknew.entities.ClientEntity;
import com.example.banknew.entities.RoleEntity;
import com.example.banknew.entities.UserEntity;
import com.example.banknew.entities.UserRoleEntity;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.repository.ClientRepository;
import com.example.banknew.repository.RoleRepository;
import com.example.banknew.repository.UserRepository;
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
            throw new NotFoundException("User not found");
        }
        UserEntity user = optUserEntity.get();
        return new User(user.getUsername(),
                user.getPassword(),
                user.getAuthorities());
    }
//    public void createClientUser(CreateClientUserDto createClientUserDto) {
//        Optional<RoleEntity> roleFromDb = roleRepository.findByName(createClientUserDto.getRoleName());
//        if (roleFromDb.isEmpty()) {
//            throw new NotFoundException("Role not found, user cannot be created");
//        }
//        UserEntity userEntity = new UserEntity();
//        userEntity.setUsername(createClientUserDto.getUsername());
//        userEntity.setPassword(passwordEncoder.encode(createClientUserDto.getPassword()));
//        userEntity.setRole(roleFromDb.get());
//        Optional<ClientEntity> optClientFromDb = clientRepository.findById(createClientUserDto.getClientId());
//        if (optClientFromDb.isEmpty()) {
//            throw new NotFoundException("Client not found, user cannot be created");
//        }
//        ClientEntity clientEntity = optClientFromDb.get();
//
//        UserEntity savedUser = userRepository.save(userEntity);
//        clientEntity.setUser(savedUser);
//        log.info("User with id {} is created", savedUser.getId() );
//    }

    public void createUser(UserDto userDto) {
        Optional<RoleEntity> roleFromDb = roleRepository.findByName(userDto.getRoleName());
        if (roleFromDb.isEmpty()) {
            throw new NotFoundException("Role not found, user cannot be created");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userDto.getUsername());
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userEntity.setRole(roleFromDb.get());
       // Optional<ClientEntity> optClientFromDb = clientRepository.findByEmail(userDto.getUsername());

        // userRepository.findById(clientDto.getUserId()).ifPresentOrElse(clientEntity::setUser, () -> {
        //     throw new NotFoundException("No such user id");
        // });

      //  if (optClientFromDb.isEmpty()) {
            UserEntity savedUser = userRepository.saveAndFlush(userEntity);
            log.info("User with id {} is created", savedUser.getId());
     //   } else {
         //   ClientEntity clientEntity = optClientFromDb.get();
        //    UserEntity savedUser = userRepository.saveAndFlush(userEntity);
        //    clientEntity.setUser(savedUser);
        //    ClientEntity savedClientEntity = clientRepository.save(clientEntity);
        //    log.info("User with id {} is created, it is client with email {}", savedUser.getId(), savedUser.getUsername());
        }
    }

