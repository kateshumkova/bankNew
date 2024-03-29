package com.example.banknew.dtos;

import com.example.banknew.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
@Getter
@Setter
@Schema(description = "ДТО Продукта")
public class ProductDto {
    @Schema(description = "Это id продукта", example = "2")
    private Long id;
    @Schema(description = "Это название продукта", example = "Пенсионный вклад")
    private String name;

    @Schema(description = "Статус продукта Active - доступный к оформлению, Inactive - в архиве", example = "ACTIVE")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Schema(description = "Валюта продукта: RUB 1, EUR 10, USD 7", example = "1")
    private int currencyCode;

    @PositiveOrZero(message = "Interest rate must be greater than 0!")
    @Schema(description = "Годовой размер процентной ставки по продукту", example = "6,5")
    private double interestRate;
    @PositiveOrZero(message = "Limit min must be greater than 0!")
    @Schema(description = "Минимальный размер депозита", example = "50000")
    private BigDecimal limitMin;

    @PositiveOrZero(message = "Limit max must be greater than 0!")
    @Schema(description = "Минимальный размер депозита", example = "500000000")
    private BigDecimal limitMax;

    @PositiveOrZero(message = "Deposit period must be greater than 0!")
    @Schema(description = "Срок действия договора в месяцах", example = "12")
    private int depositPeriod;

    @PositiveOrZero(message = "Payment frequency must be greater than 0!")
    @Schema(description = "Периодичность выплаты процентов в месяцах", example = "12")
    private int paymentFrequency;

}

