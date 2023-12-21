package com.example.banknew.mappers;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.AgreementDto;
import com.example.banknew.entities.AccountEntity;
import com.example.banknew.entities.AgreementEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AgreementMapper {
    // @Mapping(source = "firstName", target = "n")
    @Mapping(target = "clientId", source = "entity.client.id")
    @Mapping(target = "accountId", source = "entity.account.id")
    @Mapping(target = "productId", source = "entity.product.id")
    @Mapping(target = "managerId", source = "entity.manager.id")
    AgreementDto toDto(AgreementEntity entity);
    AgreementEntity toEntity(AgreementDto agreementDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target ="id", ignore = true)
    @Mapping(target ="client", ignore = true)
    @Mapping(target ="account", ignore = true)
    @Mapping(target ="product", ignore = true)
    @Mapping(target ="sum", ignore = true)
    void updateEntity(@MappingTarget AgreementEntity agreementEntity, AgreementDto agreementDto);
}
