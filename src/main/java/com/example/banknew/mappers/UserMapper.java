package com.example.banknew.mappers;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.UserDto;
import com.example.banknew.entities.AccountEntity;
import com.example.banknew.entities.UserEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // @Mapping(source = "firstName", target = "n")
    UserDto toDto(UserEntity entity);
    UserEntity toEntity(UserDto userDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target ="id", ignore = true)
    @Mapping(target ="username", ignore = true)
    void updateEntity(@MappingTarget UserEntity userEntity, UserDto userDto);
}
