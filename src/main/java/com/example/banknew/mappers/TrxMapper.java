package com.example.banknew.mappers;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.TrxDto;
import com.example.banknew.entities.AccountEntity;
import com.example.banknew.entities.TrxEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TrxMapper {
    @Mapping(target = "accountId", source = "entity.account.id")
    TrxDto toDto(TrxEntity entity);
    @Mapping(target ="id", ignore = true)
    TrxEntity toEntity(TrxDto trxDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target ="id", ignore = true)
    @Mapping(target ="account", ignore = true)
    @Mapping(target ="amount", ignore = true)
    void updateEntity(@MappingTarget TrxEntity trxEntity, TrxDto trxDto);
}
