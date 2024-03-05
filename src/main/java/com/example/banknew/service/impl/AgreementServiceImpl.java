package com.example.banknew.service.impl;

import com.example.banknew.dtos.*;
import com.example.banknew.entities.*;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.exception.ValidationException;
import com.example.banknew.mappers.*;
import com.example.banknew.repository.*;
import com.example.banknew.service.AccountService;
import com.example.banknew.service.AgreementService;
import com.example.banknew.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static com.example.banknew.enums.Status.ACTIVE;
import static com.example.banknew.enums.Status.INACTIVE;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgreementServiceImpl implements AgreementService {
    private final AgreementRepository agreementRepository;
    private final AgreementMapper agreementMapper;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ManagerRepository managerRepository;
    private final ManagerMapper managerMapper;
    private final AccountService accountService;
    private final ScheduleService scheduleService;


    @Override
    public List<AgreementDto> getAll() {
        return agreementRepository.findAll().stream()
                .map(agreementMapper::toDto)
                .toList();
    }

    @Override
    public AgreementDto findById(Long id) {
        Optional<AgreementEntity> optAgreementEntity = agreementRepository.findById(id);
        if (optAgreementEntity.isEmpty()) {
            log.info("Agreement id ={} is not found", id);
            throw new NotFoundException("Agreement not found");
        }
        return agreementMapper.toDto(optAgreementEntity.get());
    }

    @Override
    public List<AgreementDto> findByClientId(Long id) {
        List<AgreementEntity> agreementEntities = agreementRepository.findByClientId(id);
        if (agreementEntities.isEmpty()) {
            log.info("There is no Agreement for client id ={}", id);
            throw new NotFoundException("Agreement for client id doesn't exist");
        }
        return agreementEntities.stream()
                .map(agreementMapper::toDto)
                .toList();
    }

    @Override
    public List<AgreementDto> findAllActive() {
        List<AgreementEntity> agreementEntities = agreementRepository.findByStatus(ACTIVE.ordinal());
        if (agreementEntities.isEmpty()) {
            log.info("There are no active Agreements");
            throw new NotFoundException("There are no active Agreements");
        }
        return agreementEntities.stream()
                .map(agreementMapper::toDto)
                .toList();
    }

    @Override
    public AgreementDto findByAccountId(Long id) {
        Optional<AgreementEntity> optAgreementEntity = agreementRepository.findByAccountId(id);
        if (optAgreementEntity.isEmpty()) {
            log.info("There is no Agreement with account id ={}", id);
            throw new NotFoundException("Agreement for this account id doesn't exist");
        }
        return agreementMapper.toDto(optAgreementEntity.get());
    }

    @Override
    public List<AgreementDto> findByProductId(Long id) {
        List<AgreementEntity> agreementEntities = agreementRepository.findByProductId(id);
        if (agreementEntities.isEmpty()) {
            log.info("There is no Agreement for product id ={}", id);
            throw new NotFoundException("Agreement with this product id doesn't exist");
        }
        return agreementEntities.stream()
                .map(agreementMapper::toDto)
                .toList();
    }

    @Override
    public List<AgreementDto> findByManagerId(Long id) {
        List<AgreementEntity> agreementEntities = agreementRepository.findByManagerId(id);
        if (agreementEntities.isEmpty()) {
            throw new NotFoundException("There are no Agreements with this manager id");
        }
        return agreementEntities.stream()
                .map(agreementMapper::toDto)
                .toList();
    }

    @Transactional
    public CreateAgreementResponse createAgreement(CreateAgreementRequest createAgreementRequest) {

        Optional<ClientEntity> optClientEntity = clientRepository.findById(createAgreementRequest.getClientId());
        Optional<ProductEntity> optProductEntity = productRepository.findById(createAgreementRequest.getProductId());
        Optional<ManagerEntity> optManagerEntity = managerRepository.findById(createAgreementRequest.getManagerId());
        // validateOptional(Optional<ClientEntity>)
        if (optClientEntity.isEmpty() || optProductEntity.isEmpty() || optManagerEntity.isEmpty()) {
            throw new NotFoundException("Some value is empty");
        }

        ProductEntity productEntity = optProductEntity.get();

        //validation of sum and duration
        if ((createAgreementRequest.getSum().compareTo(productEntity.getLimitMax()) > 0)
                || (createAgreementRequest.getSum().compareTo(productEntity.getLimitMin()) < 0)) {
            throw new ValidationException("Sum of agreement is less or greater than than limits of the product");
        }
        if (createAgreementRequest.getDuration() != productEntity.getDepositPeriod()) {
            throw new ValidationException("Duration of agreement is less or greater than it is in duration setups of the product");
        }

        AgreementEntity agreementEntity = new AgreementEntity();
        ClientEntity clientEntity = optClientEntity.get();
        agreementEntity.setClient(clientEntity);
        agreementEntity.setProduct(productEntity);
        ManagerEntity managerEntity = optManagerEntity.get();
        agreementEntity.setManager(managerEntity);

        AccountEntity accountEntity = accountService.createAccount();
        agreementEntity.setAccount(accountEntity);

        agreementEntity.setInterestRate(productEntity.getInterestRate());
        agreementEntity.setSum(createAgreementRequest.getSum());
        agreementEntity.setStatus(ACTIVE);

        LocalDate maturityDate = LocalDate.now().plusMonths(createAgreementRequest.getDuration());

        Instant instantMaturityDate = maturityDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        agreementEntity.setMaturityDate(instantMaturityDate);
        AgreementEntity savedAgreementEntity = agreementRepository.saveAndFlush(agreementEntity);

        CreateAgreementResponse createAgreementResponse = new CreateAgreementResponse();
        createAgreementResponse.setAgreementDto(agreementMapper.toDto(savedAgreementEntity));
        createAgreementResponse.setClientDto(clientMapper.toDto(clientEntity));
        createAgreementResponse.setAccountDto(accountMapper.toDto(accountEntity));
        createAgreementResponse.setProductDto(productMapper.toDto(productEntity));
        createAgreementResponse.setManagerDto(managerMapper.toDto(managerEntity));

        log.info("Agreement with ID " + savedAgreementEntity.getId() + " is created");

        scheduleService.createPaymentSchedule(accountEntity);

        return createAgreementResponse;
    }


    @Override
    public AgreementDto updateAgreement(Long id, AgreementDto agreementDto) {
        Optional<AgreementEntity> optAgreementEntity = agreementRepository.findById(id);
        if (optAgreementEntity.isEmpty()) {
            throw new NotFoundException("Agreement cannot be updated, " + id + " is not found");
        }
        AgreementEntity agreementEntity = optAgreementEntity.get();
        agreementMapper.updateEntity(agreementEntity, agreementDto);
        agreementRepository.save(agreementEntity);
        log.info("Agreement with ID {} is updated ", id);
        return agreementMapper.toDto(agreementEntity);
    }

    @Transactional
    @Override
    public void deleteAgreement(Long id) {
        Optional<AgreementEntity> optAgreementEntity = agreementRepository.findById(id);
        if (optAgreementEntity.isEmpty()) {
            throw new NotFoundException("Agreement cannot be deleted, " + id + " is not found");
        }
        AgreementEntity agreementEntity = optAgreementEntity.get();
        agreementEntity.setStatus(INACTIVE);
        agreementRepository.save(agreementEntity);
        accountService.deleteAccount(agreementEntity.getAccount().getId());
        log.info("Status of agreement id = {} is changed to inactive or 0 ", id);
    }
}
