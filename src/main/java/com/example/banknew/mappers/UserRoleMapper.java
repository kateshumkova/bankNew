package com.example.banknew.mappers;

import com.example.banknew.dtos.UserDto;
import com.example.banknew.dtos.UserRoleDto;
import com.example.banknew.entities.UserEntity;
import com.example.banknew.entities.UserRoleEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserRoleMapper {
    // @Mapping(source = "firstName", target = "n")
    UserRoleDto toDto(UserRoleEntity userRoleEntity);
    UserRoleEntity toEntity(UserRoleDto userRoleDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

   // @Mapping(target ="rolesId", ignore = true)
    //@Mapping(target ="users_id", ignore = true)
    void updateEntity(@MappingTarget UserRoleEntity userRoleEntity, UserRoleDto userRoleDto);
}
