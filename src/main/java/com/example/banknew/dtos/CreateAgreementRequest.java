package com.example.banknew.dtos;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "ДТО необходимое для обновления обновления договора")


public class CreateAgreementRequest {
    // @Schema(description = "Это id договора", example = "2")
    // private AgreementDto agreement;
    //  @Schema(description = "Номер счета, открытый под договор", example = "2")
    //  private AccountDto account;
    @Schema(description = "Клиент,с которым заключен договор", example = "Майоров")
    private Long clientId;
    @Schema(description = "Менеджер, ведущий договор", example = "Петров")
    private Long managerId;
    @Schema(description = "Банковский продукт, на который заключается договор", example = "Пенсионный вклад")
    private Long productId;
    @PositiveOrZero(message = "Sum must be greater than 0!")
    @Schema(description = "Сумма договора", example = "10000000")
    private BigDecimal sum;
    @PositiveOrZero(message = "Duration must be greater than 0!")
    @Schema(description = "Срок договора в месяцах", example = "12")
    private int duration;

}


