package com.example.banknew.dtos;

import com.example.banknew.enums.Status;
import com.example.banknew.enums.TrxType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
@Getter
@Setter
public class TrxDto {
    @Schema(description = "id счета", example = "12")
    private Long accountId;
    @Schema(description = "Статус трансакции Active - или inactive - удаленная", example = "ACTIVE")
    @Enumerated(EnumType.STRING)
    private TrxType trxType;
    @Schema(description = "Сумма трансакции", example = "3600")
    private BigDecimal amount;
    @Schema(description = "Описание трансакции", example = "пополнение счета по договору 4848")
    private String description;
}
