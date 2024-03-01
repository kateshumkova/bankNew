package com.example.banknew.service.impl;

import com.example.banknew.aspect.UserAccess;
import com.example.banknew.dtos.TrxDto;
import com.example.banknew.entities.*;
import com.example.banknew.enums.Status;
import com.example.banknew.enums.TrxType;
import com.example.banknew.exception.AccessDeniedException;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.exception.ValidationException;
import com.example.banknew.mappers.AccountMapper;
import com.example.banknew.mappers.TrxMapper;
import com.example.banknew.repository.*;
import com.example.banknew.service.AuthService;
import com.example.banknew.service.TrxService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrxServiceImpl implements TrxService {
    private final TrxRepository trxRepository;
    private final TrxMapper trxMapper;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final AgreementRepository agreementRepository;
    private final AuthService authService;

    private List<TrxDto> returnListOfTrxForAccount(Long accountId) {
        List<TrxEntity> trxEntities = trxRepository.findByAccountId(accountId);
        if (trxEntities.isEmpty()) {
            throw new NotFoundException("There is no trx for this account " + accountId);
        }
        return trxEntities.stream()
                .map(trxMapper::toDto)
                .toList();
    }


    private boolean checkOwner(Long accountId, Authentication authentication) {
        Optional<UserEntity> optUserEntity = userRepository.findByUsername(authentication.getName());

        if (optUserEntity.isEmpty()) {
            throw new NotFoundException("There is no user with such username-email" + authentication.getName());
        }
        UserEntity userEntity = optUserEntity.get();
        ClientEntity clientEntity = clientRepository.findByEmail(userEntity.getUsername())
                .orElseThrow(() -> new NotFoundException("There is no client with such username-email" + authentication.getName()));
        List<AgreementEntity> agreementEntities = agreementRepository.findByClientId(clientEntity.getId());
        List<Long> listOfAccountId = agreementEntities.stream().map(ae -> ae.getAccount().getId()).toList();
        if (!listOfAccountId.contains(accountId)) {
            log.info("This account belongs to other user");
            return false;
        }
        return true;
    }

    @Override
    public List<TrxDto> getAll() {

        return trxRepository.findAll().stream()
                .map(trxMapper::toDto)
                .toList();
    }

    @Override
    public TrxDto getById(Long id, Authentication authentication) {
//        if (!authService.checkRole(authentication, "ROLE_USER")
//                || !authService.checkRole(authentication, "ROLE_MANAGER")
//                || !authService.checkRole(authentication, "ROLE_ADMIN")) {
//            throw new AccessDeniedException("Access with such role is impossible");
//        }
        if (authService.checkRole(authentication, "ROLE_USER")) {
            Optional<TrxEntity> optTrxEntity = trxRepository.findById(id);
            if (optTrxEntity.isEmpty()) {
                throw new NotFoundException("Trx " + id + " is not found");
            }
            TrxEntity trxEntity = optTrxEntity.get();
            Long accountId = trxEntity.getAccount().getId();
            if (!checkOwner(accountId, authentication)) {
                throw new NotFoundException("This account belongs to other user");
            }
            return trxMapper.toDto(optTrxEntity.get());

        } else {
            //access denied добавить в эксепшн если authentication.getAuthorities() is null
            Optional<TrxEntity> optTrxEntity = trxRepository.findById(id);
            if (optTrxEntity.isEmpty()) {
                throw new NotFoundException("Trx " + id + " is not found");
            }
            return trxMapper.toDto(optTrxEntity.get());
        }
    }
    @Override
    public List<TrxDto> findByAccountId(Long accountId, Authentication authentication) {
        if (authService.checkRole(authentication, "ROLE_USER")) {
            if (!checkOwner(accountId, authentication)) {
                throw new NotFoundException("This account belongs to other user");}
            return returnListOfTrxForAccount(accountId);
        } else {
            return returnListOfTrxForAccount(accountId);
        }
    }
    @Override
    public List<TrxDto> findByStatus(Authentication authentication, Long accountId, Status status) {

        if (authService.checkRole(authentication, "ROLE_USER")) {
            if (!checkOwner(accountId, authentication)) {
                throw new NotFoundException("This account belongs to other user");
            }
            List<TrxEntity> trxEntities = trxRepository.findByStatus(status);
            if (trxEntities.isEmpty()) {
                throw new NotFoundException("There is no trx with status " + status);
            }
            return trxEntities.stream()
                    .map(trxMapper::toDto)
                    .toList();
        } else {
            List<TrxEntity> trxEntities = trxRepository.findByStatus(status);
            if (trxEntities.isEmpty()) {
                throw new NotFoundException("There is no trx with status " + status);
            }
            return trxEntities.stream()
                    .map(trxMapper::toDto)
                    .toList();
        }
    }
    @Transactional
    @Override
    @UserAccess
    public TrxDto createTrx(TrxDto trxDto, Authentication authentication) {
        if (!authService.checkRole(authentication, "ROLE_USER")) {
            throw new ValidationException("Trx can be created only by clients");
        }
        if (!checkOwner(trxDto.getAccountId(), authentication)) {
            throw new NotFoundException("This account belongs to other user");
        }
        TrxEntity trxEntity = trxMapper.toEntity(trxDto);
        Optional<AccountEntity> optAccountEntity = accountRepository.findById(trxDto.getAccountId());
        if (optAccountEntity.isEmpty()) {
            throw new NotFoundException("There is no such account " + trxDto.getAccountId());
        }
        AccountEntity accountEntity = optAccountEntity.get();
        BigDecimal balanceBeforeTrx = accountEntity.getBalance();

        //если операция дебитовая  тип 1
        if (trxEntity.getTrxType() == TrxType.DEBIT) {
            accountEntity.setBalance(balanceBeforeTrx.add(trxDto.getAmount()));
        } else {
            //если операция кредитовая  тип 2
            //проверка достаточен ли баланс для проведения операции списания

            BigDecimal amountTrx = trxDto.getAmount();
            if (amountTrx.compareTo(balanceBeforeTrx) >= 0) {
                throw new ValidationException("Balance is not enough to proceed the operation");
            }
            accountEntity.setBalance(balanceBeforeTrx.subtract(trxDto.getAmount()));
        }
        trxEntity.setAccount(accountEntity);
        trxEntity.setStatus(Status.ACTIVE);
        trxEntity.setTrxType(trxDto.getTrxType());
        AccountEntity savedAccountEntity = accountRepository.saveAndFlush(accountEntity);
        TrxEntity savedTrx = trxRepository.save(trxEntity);
        log.info("Created and saved Trx with ID= {}", savedTrx.getId());
        return trxMapper.toDto(savedTrx);
    }

    //сомневаюсь, что эти операции нужны
    @Override
    public TrxDto updateTrx(Long id, TrxDto clientDto) {
        Optional<TrxEntity> optTrxEntity = trxRepository.findById(id);
        if (optTrxEntity.isEmpty()) {
            throw new NotFoundException("Trx cannot be updated," + id + "is not found");
        }
        TrxEntity trxEntity = optTrxEntity.get();
        trxMapper.updateEntity(trxEntity, clientDto);
        trxRepository.save(trxEntity);
        log.info("Trx with ID {} is updated", id);
        return trxMapper.toDto(trxEntity);
    }

    @Override
    public void deleteTrx(Long id) {
        Optional<TrxEntity> optTrxEntity = trxRepository.findById(id);
        if (optTrxEntity.isEmpty()) {
            throw new NotFoundException("Trx" + id + "is " +
                    "not found");
        }
        // trxRepository.deleteById(id);
        TrxEntity trxEntity = optTrxEntity.get();
        trxEntity.setStatus(Status.INACTIVE);
        trxRepository.save(trxEntity);
    }

}


