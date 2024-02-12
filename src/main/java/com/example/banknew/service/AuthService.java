package com.example.banknew.service;

import com.example.banknew.dtos.ClientDto;
import com.example.banknew.dtos.CreateClientRequest;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface AuthService {
    boolean checkRole(Authentication authentication,String roleName);

}
