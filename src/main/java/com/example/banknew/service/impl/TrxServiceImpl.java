package com.example.banknew.service.impl;

import com.example.banknew.dtos.TrxDto;
import com.example.banknew.entities.*;
import com.example.banknew.enums.Status;
import com.example.banknew.enums.TrxType;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.exception.ValidationException;
import com.example.banknew.mappers.AccountMapper;
import com.example.banknew.mappers.TrxMapper;
import com.example.banknew.repository.*;
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

    private List<TrxDto> returnListOfTrxForAccount(Long accountId) {
        List<TrxEntity> trxEntities = trxRepository.findByAccountId(accountId);
        if (trxEntities.isEmpty()) {
            throw new NotFoundException("There is no trx for this account " + accountId);
        } else {
            return trxEntities.stream()
                    .map(trxMapper::toDto)
                    .toList();
        }
    }


    public boolean checkOwner(Long accountId, Authentication authentication) {
        // if (authentication.getAuthorities().stream()
        //         .anyMatch(r -> r.getAuthority().equalsIgnoreCase("ROLE_USER"))) {
        Optional<UserEntity> optUserEntity = userRepository.findByUsername(authentication.getName());
        UserEntity userEntity = optUserEntity.get();
        ClientEntity clientEntity = clientRepository.findByEmail(userEntity.getUsername())
                .orElseThrow(() -> new NotFoundException("There is no client with such username-email" + authentication.getName()));
        List<AgreementEntity> agreementEntities = agreementRepository.findByClientId(clientEntity.getId());
        var listOfAccountId = agreementEntities.stream().map(ae -> ae.getAccount().getId()).toList();
        if (listOfAccountId.contains(accountId)) {
            //  if (agreementEntities != null && !agreementEntities.isEmpty()) {
            return true;
        } else {
            log.info("This account belongs to other user");
            return false;
        }
    }

    @Override
    public List<TrxDto> getAll() {

        return trxRepository.findAll().stream()
                .map(trxMapper::toDto)
                .toList();
    }
//правильно ли? ниже рабочие спагетти
//    @Override
//    public TrxDto getById(Long id, Authentication authentication) {
//        if (authentication.getAuthorities().stream()
//                .anyMatch(r -> r.getAuthority().equalsIgnoreCase("ROLE_USER"))) {
//            Optional<TrxEntity> optTrxEntity = Optional.ofNullable(trxRepository.findById(id).orElseThrow(() -> new NotFoundException("Trx " + id + "is not found")));
//            TrxEntity trxEntity = optTrxEntity.get();
//            Long accountId = trxEntity.getAccount().getId();
//            if (checkOwner(accountId, authentication)) {
//                return trxMapper.toDto(optTrxEntity.get());
//            } else {
//                throw new NotFoundException("This account belongs to other user");
//            }
//
//        } else {
//            Optional<TrxEntity> optTrxEntity = Optional.ofNullable(trxRepository.findById(id).orElseThrow(() -> new NotFoundException("Trx " + id + "is not found")));
//            return trxMapper.toDto(optTrxEntity.get());
//        }
//    }

    @Override
    public TrxDto getById(Long id, Authentication authentication) {
        if (authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equalsIgnoreCase("ROLE_USER"))) {
            Optional<TrxEntity> optTrxEntity = trxRepository.findById(id);
            if (optTrxEntity.isPresent()) {
                TrxEntity trxEntity = optTrxEntity.get();
                Long accountId = trxEntity.getAccount().getId();
                if (checkOwner(accountId, authentication)) {
                    return trxMapper.toDto(optTrxEntity.get());
                } else {
                    throw new NotFoundException("This account belongs to other user");
                }
            } else {
                throw new NotFoundException("Trx " + id + "is not found");
            }
        } else {
            Optional<TrxEntity> optTrxEntity = trxRepository.findById(id);
            if (optTrxEntity.isPresent()) {
                return trxMapper.toDto(optTrxEntity.get());
           } else {
                throw new NotFoundException("Trx " + id + "is not found");
            }
        }
    }


    // .orElseThrow(() -> new UsernameNotFoundException(ErrorsMessage.NOT_FOUND_USER_MESSAGE + " with name: " + username));
    @Override
    public List<TrxDto> findByAccountId(Long accountId, Authentication authentication) {
        if (authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equalsIgnoreCase("ROLE_USER"))) {
            if (checkOwner(accountId, authentication)) {
                return returnListOfTrxForAccount(accountId);
            } else {
                throw new NotFoundException("This account belongs to other user");
            }
        } else {
            return returnListOfTrxForAccount(accountId);
        }
    }
    // HttpServletRequest request) {
    //     if (request.isUserInRole("ROLE_ADMIN")) {
    // String name = authentication.getName(); //mary
    //  }
//        var author = authentication.getAuthorities();
//        var cred = authentication.getCredentials();
//        var details = authentication.getDetails();
//        var principal = authentication.getPrincipal();
//        var authClass = authentication.getClass();

//        if (authentication.getAuthorities().stream()
//                .anyMatch(r -> r.getAuthority().equalsIgnoreCase("ROLE_USER"))) {
//            Optional<UserEntity> optUserEntity = userRepository.findByUsername(authentication.getName());
//            UserEntity userEntity = optUserEntity.get();
//            ClientEntity clientEntity = clientRepository.findByEmail(userEntity.getUsername())
//                    .orElseThrow(() -> new NotFoundException("There is no client with such username-email" + authentication.getName()));
//
////проверить принадлежат ли клиенту аккаунт checkClient
//            List<TrxEntity> trxEntities = trxRepository.findByAccountId(accountId);
//            if (trxEntities.isEmpty()) {
//                throw new NotFoundException("There is no trx for this account" + accountId);
//            } else {
//                return trxEntities.stream()
//                        .map(trxMapper::toDto)
//                        .toList();
//            }
//        }
    //isPresent
    //var role = authentication.getAuthorities().stream()
//                .filter(r -> r.getAuthority().equals("ROLE_USER"))
//                .findFirst(); //возращает первый и оборачивает в опш, проверить на наличие
    // Optional<RoleEntity> role =  authentication.getAuthorities().stream()
    //         .anyMatch(r -> r.getAuthority().equals("ROLE_USER"));
    //equalsIgnoreCase
    //private boolean checkrole (String roleName, authorities) проверка
//            if (optUserEntity.isEmpty()) {
//                throw new NotFoundException("There is no such username" + authentication.getName());
//            } else {
//                if (//authentication.getAuthorities() == 3
//                        request.isUserInRole("ROLE_USER")) {
//                    UserEntity userEntity = optUserEntity.get();
//                    ClientEntity clientEntity = clientRepository.findByEmail(userEntity.getUsername())
//                            .orElseThrow(() -> new NotFoundException("There is no client with such username-email" + authentication.getName()));
//
////проверить принадлежат ли клиенту аккаунт checkClient
//                    List<TrxEntity> trxEntities = trxRepository.findByAccountId(accountId);
//                    if (trxEntities.isEmpty()) {
//                        throw new NotFoundException("There is no trx for this account" + accountId);
//                    } else {
//                        return trxEntities.stream()
//                                .map(trxMapper::toDto)
//                                .toList();
//                    }
//                }
//            }
//        else {
//
//            //  request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_MANAGER") // if (authentication.getAuthorities()==1 || authentication.getAuthorities()==2)
//            List<TrxEntity> trxEntities = trxRepository.findByAccountId(accountId);
//            if (trxEntities.isEmpty()) {
//                throw new NotFoundException("There is no trx for this account" + accountId);
//            } else {
//                return trxEntities.stream()
//                        .map(trxMapper::toDto)
//                        .toList();
//            }
//        }
//    }


    @Override
    public List<TrxDto> findByStatus(Status status) {
        List<TrxEntity> trxEntities = trxRepository.findByStatus(status);
        if (trxEntities.isEmpty()) {
            throw new NotFoundException("There is no trx with status" + status);
        } else {
            return trxEntities.stream()
                    .map(trxMapper::toDto)
                    .toList();
        }
    }

    @Transactional
    @Override
    public TrxDto createTrx(TrxDto trxDto) {
        // Optional<TrxEntity> optTrxEntity = trxRepository.getByEmail(trxDto.getEmail());
        //  if (optTrxEntity.isEmpty()) {
        TrxEntity trxEntity = trxMapper.toEntity(trxDto);
        Optional<AccountEntity> optAccountEntity = accountRepository.findById(trxDto.getAccountId());
        AccountEntity accountEntity = optAccountEntity.get();
        BigDecimal balanceBeforeTrx = accountEntity.getBalance();

        //если операция дебитовая  тип 1
        if (trxEntity.getTrxType() == TrxType.DEBIT) {
            accountEntity.setBalance(balanceBeforeTrx.add(trxDto.getAmount()));
        } else {
            //если операция кредитовая  тип 2
            //проверка достаточен ли баланс для проведения операции списания

            BigDecimal amountTrx = trxDto.getAmount();
            if (amountTrx.compareTo(balanceBeforeTrx) <= 0) {
                accountEntity.setBalance(balanceBeforeTrx.subtract(trxDto.getAmount()));
            } else log.info("Balance is not enough to proceed the operation");
            throw new ValidationException("Balance is not enough to proceed the operation");
        }
        trxEntity.setAccount(accountEntity);
        trxEntity.setStatus(Status.ACTIVE);
        trxEntity.setTrxType(trxDto.getTrxType());
        AccountEntity savedAccountEntity = accountRepository.saveAndFlush(accountEntity);
        TrxEntity savedTrx = trxRepository.save(trxEntity);
        log.info("Created and saved Trx with ID= {}", savedTrx.getId());
        return trxMapper.toDto(savedTrx);

    }

    @Override
    public TrxEntity updateTrx(Long id, TrxDto clientDto) {
        Optional<TrxEntity> optTrxEntity = trxRepository.findById(id);
        if (optTrxEntity.isPresent()) {
            TrxEntity trxEntity = optTrxEntity.get();
            trxMapper.updateEntity(trxEntity, clientDto);
            trxRepository.save(trxEntity);
            log.info("Trx with ID {} is updated", id);
            return trxEntity;
        }
        throw new NotFoundException("Trx cannot be updated," + id + "is not found");

    }

    @Override
    public void deleteTrx(Long id) {
        Optional<TrxEntity> optTrxEntity = trxRepository.findById(id);
        if (optTrxEntity.isPresent()) {
            // trxRepository.deleteById(id);
            TrxEntity trxEntity = optTrxEntity.get();
            trxEntity.setStatus(Status.INACTIVE);
            trxRepository.save(trxEntity);
            return;
        }
        throw new NotFoundException("Trx" + id + "is " +
                "not found");
    }

}
