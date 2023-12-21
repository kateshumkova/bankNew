package com.example.banknew.mappers;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.ManagerDto;
import com.example.banknew.entities.AccountEntity;
import com.example.banknew.entities.ManagerEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ManagerMapper {
   // @Mapping(source = "firstName", target = "n")
    ManagerDto toDto(ManagerEntity entity);
    ManagerEntity toEntity(ManagerDto managerDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target ="id", ignore = true)
    void updateEntity(@MappingTarget ManagerEntity managerEntity, ManagerDto managerDto);
}
