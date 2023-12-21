package com.example.banknew.mappers;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.ProductDto;
import com.example.banknew.entities.AccountEntity;
import com.example.banknew.entities.ProductEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    // @Mapping(source = "firstName", target = "n")
    ProductDto toDto(ProductEntity entity);
    ProductEntity toEntity(ProductDto productDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target ="id", ignore = true)
    @Mapping(target ="name", ignore = true)
    @Mapping(target ="currencyCode", ignore = true)
    @Mapping(target ="interestRate", ignore = true)
    @Mapping(target ="limitMin", ignore = true)
    @Mapping(target ="limitMax", ignore = true)
    void updateEntity(@MappingTarget ProductEntity productEntity, ProductDto productDto);
}
