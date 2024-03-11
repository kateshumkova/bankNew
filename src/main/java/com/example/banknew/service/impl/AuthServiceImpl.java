package com.example.banknew.service.impl;

import com.example.banknew.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    /**
     * Method checks if roleName is present in a stream of roles got from Authentication
     * @param authentication
     * @param roleName
     */
    @Override
    public boolean checkRole(Authentication authentication, String roleName) {
        return authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equalsIgnoreCase(roleName));
    }

}
