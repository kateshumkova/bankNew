package com.example.banknew.mappers;

import com.example.banknew.dtos.ClientDto;
import com.example.banknew.dtos.CreateClientRequest;
import com.example.banknew.entities.ClientEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    // @Mapping(source = "firstName", target = "n")
    ClientDto toDto(ClientEntity entity);

    ClientEntity toEntity(ClientDto clientDto);
    ClientEntity toEntity(CreateClientRequest creationRequestClientDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target ="id", ignore = true)
    void updateEntity(@MappingTarget ClientEntity clientEntity, ClientDto clientDto);

}
