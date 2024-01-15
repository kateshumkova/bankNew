package com.example.banknew.service;

import com.example.banknew.dtos.CreateAgreementRequest;
import com.example.banknew.dtos.AgreementDto;
import com.example.banknew.dtos.CreateAgreementResponse;
import com.example.banknew.entities.*;

import java.util.List;

public interface AgreementService {
    List<AgreementDto> getAll();
    AgreementDto findById(Long id);
    List<AgreementDto> findByClientId(Long id);

   // static List<AgreementDto> findAllActive() {
   //     return null;
  //  }

    List<AgreementDto> findAllActive();

    AgreementDto findByAccountId(Long id);

    List<AgreementDto> findByProductId(Long id);
    List<AgreementDto> findByManagerId(Long id);
    CreateAgreementResponse createAgreement(CreateAgreementRequest createAgreementRequest);
    AgreementDto updateAgreement(Long id, AgreementDto agreementDto);
    void deleteAgreement(Long id);


}
