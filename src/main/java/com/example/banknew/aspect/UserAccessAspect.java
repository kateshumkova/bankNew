package com.example.banknew.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class UserAccessAspect {
    @Pointcut("@annotation(com.example.banknew.aspect.UserAccess) && args(authentication)")
    public void processLog(Authentication authentication) {
        //advice After before
    }

    @Before("processLog(authentication)")
    public void beforeProcessLog(JoinPoint jp, Authentication authentication) {
      log.info(authentication.getName());
    }
}
