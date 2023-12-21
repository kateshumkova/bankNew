package com.example.banknew.dtos;

import com.example.banknew.entities.RoleEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Schema(description = "ДТО пользователя")

public class UserDto {

    @Schema(description = "Имя пользователя", example = "maryPoppins")
    private String username;
    @Schema(description = "Пароль пользователя", example = "Qwerty123")
    private String password;
    @Schema(description = "Роль пользователя", example = "ROLE_USER")
    private String roleName;
}
