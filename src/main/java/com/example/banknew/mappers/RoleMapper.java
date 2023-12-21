package com.example.banknew.mappers;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.RoleDto;
import com.example.banknew.entities.AccountEntity;
import com.example.banknew.entities.RoleEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    // @Mapping(source = "firstName", target = "n")
    RoleDto toDto(RoleEntity entity);
    RoleEntity toEntity(RoleDto roleDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target ="id", ignore = true)
    @Mapping(target ="name", ignore = true)
    void updateEntity(@MappingTarget RoleEntity roleEntity, RoleDto roleDto);
}
