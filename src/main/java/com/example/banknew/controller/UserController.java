package com.example.banknew.controller;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.CreateAgreementRequest;
import com.example.banknew.dtos.CreateClientUserDto;
import com.example.banknew.dtos.UserDto;
import com.example.banknew.entities.AccountEntity;
import com.example.banknew.service.AccountService;
import com.example.banknew.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/noauth/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Создать запись о новом пользователе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь добавлен",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "400", description = "Неверный запрос",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь не добавлен",
                    content = @Content)})
    @PostMapping("/")
    public void add(@RequestBody UserDto userDto) {
        userService.createUser(userDto);
    }


//    @Operation(summary = "Создать запись о новом пользователе - клиенте")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Пользователь добавлен",
//                    content = {@Content(mediaType = "application/json",
//                            schema = @Schema(implementation = CreateClientUserDto.class))}),
//            @ApiResponse(responseCode = "400", description = "Неверный запрос",
//                    content = @Content),
//            @ApiResponse(responseCode = "404", description = "Пользователь не добавлен",
//                    content = @Content)})
//    @PostMapping("/client/")
//    public void add(@RequestBody CreateClientUserDto createClientUserDto) {
//        userService.createClientUser(createClientUserDto);
//    }
}
