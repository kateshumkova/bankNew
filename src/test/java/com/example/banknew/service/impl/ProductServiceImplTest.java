package com.example.banknew.service.impl;

import com.example.banknew.dtos.ProductDto;
import com.example.banknew.dtos.TrxDto;
import com.example.banknew.entities.ProductEntity;
import com.example.banknew.enums.Status;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.exception.ValidationException;
import com.example.banknew.mappers.ProductMapper;
import com.example.banknew.repository.ManagerRepository;
import com.example.banknew.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductServiceImplTest {
    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;

//    @Test
//    void testValidateOptProduct_shouldNotFoundException_ifEmptyProduct() {
//        ValidationException exception = assertThrows(ValidationException.class, () -> productService.getById(1L));
//        assertEquals("No such product", exception.getMessage());
//    }

    @Test
    void testGetAll_shouldReturnListProductsDto() {
        //заглушки

        when(productRepository.findAll()).thenReturn(List.of(new ProductEntity(), new ProductEntity()));
        when(productMapper.toDto(any())).thenReturn(new ProductDto());

        //вызов метода
        List<ProductDto> actual = productService.getAll();

        //проверка результата
        verify(productMapper, atLeast(2)).toDto(any());
        verify(productRepository, atLeast(1)).findAll();
        assertEquals(2, actual.size());
    }

    @Test
    void testGetAll_shouldReturnEmptyList() {
        //заглушки

        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        //вызов метода
        List<ProductDto> actual = productService.getAll();

        //проверка результата
        verify(productMapper, never()).toDto(any());
        verify(productRepository, atLeast(1)).findAll();
        assertEquals(0, actual.size());
    }

    @Test
    void testGetById_shouldNotFoundException_ifEmptyProduct() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.getById(1L));
        assertEquals("Product 1 is not found", exception.getMessage());
    }

    @Test
    void testGetById_shouldReturnProductDto_ifProductEntityIsPresent() {
        //заглушки
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(1L);
        ProductDto productDto = new ProductDto();

        when(productRepository.findById(any())).thenReturn(Optional.of(productEntity));
        when(productMapper.toDto(productEntity)).thenReturn(productDto);
        //вызов метода
        ProductDto actual = productService.getById(1L);

        //проверка результата
        verify(productMapper, atLeast(1)).toDto(any());
        assertNotNull(actual);
    }

    @Test
    void testFindByName_shouldNotFoundException_ifEmptyProduct() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.findByName("Вклад"));
        assertEquals("Product with name Вклад is not found", exception.getMessage());
    }

    @Test
    void testFindByName_shouldReturnListProductDto_ifPresentProduct() {
        //заглушки
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(1L);
        ProductDto productDto = new ProductDto();

        when(productRepository.findByName(any())).thenReturn(List.of(productEntity));
        when(productMapper.toDto(productEntity)).thenReturn(productDto);
        //вызов метода
        List<ProductDto> actual = productService.findByName("Вклад");

        //проверка результата
        verify(productMapper, atLeast(1)).toDto(any());
        assertNotNull(actual);
    }

    @Test
    void testCreateProduct_shouldReturnProductDto() {
        //заглушки
        ProductDto productDto = new ProductDto();

        when(productMapper.toEntity(any())).thenReturn(new ProductEntity());
        when(productMapper.toDto(any())).thenReturn(productDto);
        when(productRepository.save(any())).thenReturn(new ProductEntity());

        //вызов метода
        ProductDto actual = productService.createProduct(productDto);

        //проверка результата
        verify(productRepository, atLeast(1)).save(any());
        verify(productMapper, atLeast(1)).toDto(any());
        verify(productMapper, atLeast(1)).toEntity(any());
        assertNotNull(actual);
    }

    @Test
    void testUpdateProduct_shouldNotFoundException_ifEmptyProduct() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.updateProduct(1L, new ProductDto()));
        assertEquals("Product cannot be updated, 1 is not found", exception.getMessage());
    }

    @Test
    void testUpdateProduct_shouldReturnProductDto_ifNotEmptyProduct() {
        //заглушки
        ProductEntity productEntity = new ProductEntity();
        when(productRepository.findById(any())).thenReturn(Optional.of(productEntity));

        //вызов метода
        ProductDto actual = productService.updateProduct(1L, new ProductDto());

        //проверка результата
        verify(productMapper, atLeast(1)).updateEntity(any(), any());
        verify(productRepository).save(any());
        // assertNotNull(actual);
    }

    @Test
    void testDeleteProduct_shouldNotFoundException_ifEmptyProduct() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.deleteProduct(1L));
        assertEquals("Product 1 is not found", exception.getMessage());
    }

    @Test
    void testDeleteProduct_shouldSucceed_ifPresentProduct() {
        ProductEntity productEntity = new ProductEntity();
        when(productRepository.findById(any())).thenReturn(Optional.of(productEntity));

        productService.deleteProduct(1L);

        verify(productRepository, atLeast(1)).save(any());
        assertEquals(productEntity.getStatus(), Status.INACTIVE);
    }
}