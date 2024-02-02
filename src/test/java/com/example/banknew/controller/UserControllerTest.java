package com.example.banknew.controller;


import com.example.banknew.service.impl.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    public MockMvc mvc;
    @MockBean
    public UserService userService;
    @Test
    void add() {
    }
}