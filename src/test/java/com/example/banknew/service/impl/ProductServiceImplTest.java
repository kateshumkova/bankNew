package com.example.banknew.service.impl;

import com.example.banknew.dtos.ProductDto;
import com.example.banknew.dtos.TrxDto;
import com.example.banknew.entities.ProductEntity;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.exception.ValidationException;
import com.example.banknew.mappers.ProductMapper;
import com.example.banknew.repository.ManagerRepository;
import com.example.banknew.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

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

    @Test
    void testValidateOptProduct_shouldNotFoundException_ifEmptyProduct() {
        ValidationException exception = assertThrows(ValidationException.class, () -> productService.getById(1L));
        assertEquals("No such product", exception.getMessage());
    }

    @Test
    void testGetAll_shouldReturnListProductsDto() {
        //заглушки
        // AccountEntity accountEntity = new AccountEntity();
        // when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntity));

        //вызов метода
        List<ProductDto> actual = productService.getAll();

        //проверка результата
//        verify(accountMapper,atLeast(1)).toDto(any());
        verify(productRepository, atLeast(1)).findAll();
        assertNotNull(actual);
    }

    @Test
    void testGetById_shouldNotFoundException_ifEmptyProduct() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.getById(1L));
        assertEquals("Product 1 is not found", exception.getMessage());
    }

    @Test
    void testFindByName_shouldNotFoundException_ifEmptyProduct() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.findByName("Вклад"));
        assertEquals("Product with name Вклад is not found", exception.getMessage());
    }

//    @Test
//    void testCreateProduct_shouldNotFoundException_ifEmptyProduct() {
//        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.createProduct(new DtoProduct());
//        assertEquals("", exception.getMessage());
//    }

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
        verify(productMapper,atLeast(1)).updateEntity(any(), any());
        verify(productRepository).save(any());
       // assertNotNull(actual);
    }

    @Test
    void testDeleteProduct_shouldNotFoundException_ifEmptyProduct() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.deleteProduct(1L));
        assertEquals("Product 1 is not found", exception.getMessage());
    }
}