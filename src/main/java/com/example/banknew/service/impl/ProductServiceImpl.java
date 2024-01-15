package com.example.banknew.service.impl;

import com.example.banknew.dtos.ClientDto;
import com.example.banknew.dtos.ProductDto;
import com.example.banknew.entities.ClientEntity;
import com.example.banknew.entities.ProductEntity;
import com.example.banknew.entities.TrxEntity;
import com.example.banknew.enums.Status;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.exception.ValidationException;
import com.example.banknew.mappers.ClientMapper;
import com.example.banknew.mappers.ProductMapper;
import com.example.banknew.repository.ClientRepository;
import com.example.banknew.repository.ProductRepository;
import com.example.banknew.service.ClientService;
import com.example.banknew.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public boolean validateOptProduct(Optional<ProductEntity> optProductEntity) {

        if (optProductEntity.isEmpty()) {
            throw new ValidationException("No such product");
        }
        return true;
    }

    @Override
    public List<ProductDto> getAll() {

        return productRepository.findAll().stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    public ProductDto getById(Long id) {
        Optional<ProductEntity> optProductEntity = productRepository.findById(id);
        if (optProductEntity.isEmpty()) {
            throw new NotFoundException("Product " + id + " is not found");
        }
        return productMapper.toDto(optProductEntity.get());
    }

    @Override
    public List<ProductDto> findByName(String name) {
        List<ProductEntity> productEntities = productRepository.findByName(name);
        if (productEntities.isEmpty()) {
            throw new NotFoundException("Product with name " + name + " is not found");
        }
        return productEntities.stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    public ProductDto createProduct(ProductDto clientDto) {
        ProductEntity savedClient = productRepository.save(productMapper.toEntity(clientDto));
        log.info("Created and saved product with ID= {}", savedClient.getId());
        return productMapper.toDto(savedClient);
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Optional<ProductEntity> optProductEntity = productRepository.findById(id);
        if (optProductEntity.isEmpty()) {
            throw new NotFoundException("Product cannot be updated, " + id + " is not found");
        }
        ProductEntity productEntity = optProductEntity.get();
        productMapper.updateEntity(productEntity, productDto);
        productRepository.save(productEntity);
        log.info("Product with ID {} is updated", id);
        return productMapper.toDto(productEntity);
    }

    @Override
    public void deleteProduct(Long id) {
        Optional<ProductEntity> optProductEntity = productRepository.findById(id);
        if (optProductEntity.isEmpty()) {
            throw new NotFoundException("Product " + id + " is not found");
        }
        ProductEntity productEntity = optProductEntity.get();
        productEntity.setStatus(Status.INACTIVE);
        productRepository.save(productEntity);
        // productRepository.deleteById(id);
    }
}