package com.example.banknew.mappers;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.ClientDto;
import com.example.banknew.dtos.ManagerDto;
import com.example.banknew.entities.AccountEntity;
import com.example.banknew.entities.ClientEntity;
import com.example.banknew.entities.ManagerEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    // @Mapping(source = "firstName", target = "n")
    AccountDto toDto(AccountEntity entity);
    AccountEntity toEntity(AccountDto accountDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target ="id", ignore = true)
    @Mapping(target ="balance", ignore = true)
    void updateEntity(@MappingTarget AccountEntity accountEntity, AccountDto accountDto);
}
