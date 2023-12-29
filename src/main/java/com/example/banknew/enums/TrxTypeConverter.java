//package com.example.banknew.enums;
//
//
//import jakarta.persistence.AttributeConverter;
//
//import static com.example.banknew.enums.TrxType.CREDIT;
//import static com.example.banknew.enums.TrxType.DEBIT;
//
//public class TrxTypeConverter implements AttributeConverter<TrxType, Integer> {
//    @Override
//    public Integer convertToDatabaseColumn(TrxType trxType) {
//        return switch (trxType) {
//            case DEBIT -> 1;
//            case CREDIT -> 2;
//            default -> throw new IllegalStateException("Unexpected value: " + trxType);
//        };
//    }
//
//    @Override
//    public TrxType convertToEntityAttribute(Integer typeCode) {
//        return switch (typeCode) {
//            case 1 -> TrxType.DEBIT;
//            case 2 -> CREDIT;
//            default -> throw new IllegalStateException("Unexpected value: " + typeCode);
//        };
//    }
//}