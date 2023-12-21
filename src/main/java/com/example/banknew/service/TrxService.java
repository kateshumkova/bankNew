package com.example.banknew.service;
import com.example.banknew.dtos.ClientDto;
import com.example.banknew.dtos.TrxDto;
import com.example.banknew.entities.ClientEntity;
import com.example.banknew.entities.TrxEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface TrxService {
    List<TrxDto> getAll();
    TrxDto getById(Long id);
    List<TrxDto> findByAccountId(Long id, Authentication authentication );
    List<TrxDto> findByStatus(int status);
    TrxDto createTrx(TrxDto trxDto);
    TrxEntity updateTrx(Long id, TrxDto trxDto);
    void deleteTrx(Long id);
}
