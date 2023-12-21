package com.example.banknew.service;

import com.example.banknew.dtos.ClientDto;
import com.example.banknew.dtos.ProductDto;
import com.example.banknew.entities.ClientEntity;
import com.example.banknew.entities.ProductEntity;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<ProductDto> getAll();
    ProductDto getById(Long id);
    List<ProductDto> findByName(String name);
    ProductDto createProduct(ProductDto productDto);
    ProductEntity updateProduct(Long id, ProductDto productDto);
    void deleteProduct(Long id);
    boolean validateOptProduct(Optional<ProductEntity> optProductEntity);

}
