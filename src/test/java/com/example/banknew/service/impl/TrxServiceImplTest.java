package com.example.banknew.service.impl;

import com.example.banknew.dtos.TrxDto;
import com.example.banknew.entities.*;
import com.example.banknew.exception.AccessDeniedException;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.exception.ValidationException;
import com.example.banknew.mappers.AccountMapper;
import com.example.banknew.mappers.TrxMapper;
import com.example.banknew.repository.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
class TrxServiceImplTest {
    @InjectMocks
    private TrxServiceImpl trxService;

    @Mock
    private TrxRepository trxRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AgreementRepository agreementRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TrxMapper trxMapper;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private AuthServiceImpl authService;

    private List<TrxDto> returnListOfTrxForAccount(Long accountId) {
        List<TrxEntity> trxEntities = trxRepository.findByAccountId(accountId);
        if (trxEntities.isEmpty()) {
            throw new NotFoundException("There is no trx for this account " + accountId);
        }
        return trxEntities.stream()
                .map(trxMapper::toDto)
                .toList();
    }

    @Test
    void testReturnListOfTrxForAccount_shouldNotFoundException_ifEmptyTrxEntities() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> returnListOfTrxForAccount(1L));
        assertEquals("There is no trx for this account 1", exception.getMessage());
    }

    @Test
    void testReturnListOfTrxForAccount_shouldReturnListTrxDto_ifPresentTrxEntities() {
        TrxEntity trxEntity = new TrxEntity();
        when(trxRepository.findByAccountId(any())).thenReturn(List.of(trxEntity));

        List<TrxDto> actual = returnListOfTrxForAccount(1L);

        //проверка результата
        verify(trxMapper, atLeast(1)).toDto(any());
        assertNotNull(actual);
    }

    @Test
    void testGetAll_shouldReturnListTrxDto() {
        //заглушки
        // AccountEntity accountEntity = new AccountEntity();
        // when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntity));

        //вызов метода
        List<TrxDto> actual = trxService.getAll();

        //проверка результата
//        verify(accountMapper,atLeast(1)).toDto(any());
        verify(trxRepository, atLeast(1)).findAll();
        assertNotNull(actual);
    }

    @Test
    void testGetById_shouldNotFoundException_ifRoleUser_ifEmptyTrx() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("ROLE_USER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));
      //  when(authService.checkRole(authentication,"ROLE_USER")).thenReturn(true);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> trxService.getById(1L, authentication));
        assertEquals("Trx 1 is not found", exception.getMessage());
    }

    @Test
    void testGetById_shouldNotFoundException_ifRoleAdmin_ifEmptyTrx() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("ROLE_ADMIN");
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> trxService.getById(1L, authentication));
        assertEquals("Trx 1 is not found", exception.getMessage());
    }

    @Test
    void testGetById_shouldNotFoundException_ifRoleManager_ifEmptyTrx() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("ROLE_MANAGER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> trxService.getById(1L, authentication));
        assertEquals("Trx 1 is not found", exception.getMessage());
    }

    @Test
    void testGetById_shouldNotFoundException_ifRoleAdmin_ifPresentTrx() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("ROLE_ADMIN");
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));

        TrxEntity trxEntity = new TrxEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);

        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));
        when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.of(userEntity));

        TrxDto actual = trxService.getById(1L, authentication);

        //проверка результата
        verify(trxMapper, atLeast(1)).toDto(any());
        //  assertNotNull(actual);
    }

    @Test
    void testGetById_shouldNotFoundException_ifUnknownRole_ifEmptyTrx() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("ROLE_VISITOR");
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> trxService.getById(1L, authentication));
        assertEquals("Access with such role is impossible", exception.getMessage());
    }

    @Test
    void testGetById_shouldReturnTrxDto_ifRoleUser_ifPresentTrx() {

        TrxEntity trxEntity = new TrxEntity();
        trxEntity.setId(1L);
        AccountEntity accountEntity = createAccountEntity(1L);
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setId(1L);
        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setId(1L);
        agreementEntity.setAccount(accountEntity);
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(clientEntity));
        when(agreementRepository.findByClientId(any())).thenReturn(List.of(agreementEntity));
        when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntity));
        when(trxMapper.toDto(any())).thenReturn(new TrxDto());
        when(authService.checkRole(any(),any())).thenReturn(true);
        TrxDto actual = trxService.getById(1L, createAuthentication("ROLE_USER"));

        //проверка результата
        verify(trxMapper, atLeast(1)).toDto(any());
        assertNotNull(actual);
    }

    private Authentication createAuthentication(String role) {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(role);
        return new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));
    }

    private AccountEntity createAccountEntity(Long id) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(id);
        return accountEntity;
    }

    @Test
    void testGetById_shouldReturnTrxDto_ifRoleUser_ifCheckUserFalse() {
        RoleEntity roleEntity = new RoleEntity();
        TrxEntity trxEntity = new TrxEntity();
        trxEntity.setId(1L);
        AccountEntity accountEntity = createAccountEntity(1L);
        AccountEntity accountEntity2 = createAccountEntity(2L);
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
        roleEntity.setName("ROLE_USER");
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setId(1L);
        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setId(8L);
        agreementEntity.setAccount(accountEntity2);
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(clientEntity));
        when(agreementRepository.findByClientId(any())).thenReturn(List.of(agreementEntity));
        when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntity));
        when(trxMapper.toDto(any())).thenReturn(new TrxDto());
        TrxDto actual = trxService.getById(1L, authentication);

        //проверка результата
        verify(trxMapper, atLeast(1)).toDto(any());
        assertNotNull(actual);
    }

    @Test
    void testGetById_shouldReturnTrxDto_ifRoleUser_ifPresentTrx_ifEmptyUserEntity() {
        RoleEntity roleEntity = new RoleEntity();
        TrxEntity trxEntity = new TrxEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
        roleEntity.setName("ROLE_USER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));
        when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.ofNullable(null));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.getById(1L, authentication));
        assertEquals("There is no user with such username-email", exception.getMessage());

    }

    @Test
    void testGetById_shouldReturnTrxDto_ifPresentTrx_ifRoleUser_ifEmptyClientEntity() {
        RoleEntity roleEntity = new RoleEntity();
        TrxEntity trxEntity = new TrxEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
        roleEntity.setName("ROLE_USER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));
        when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.of(userEntity));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.getById(1L, authentication));
        assertEquals("There is no client with such username-email", exception.getMessage());
    }

    @Test
    void testGetById_shouldReturnTrxDto_ifRoleUser_ifPresentTrx_ifPresentClientEntity_ifCheckOwnerIsPassed() {
        RoleEntity roleEntity = new RoleEntity();
        TrxEntity trxEntity = new TrxEntity();
        ClientEntity clientEntity = new ClientEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setAccount(accountEntity);
        trxEntity.setAccount(accountEntity);
        roleEntity.setName("ROLE_USER");
        List<Long> actual = Arrays.asList(1L, 2L, 3L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));
        when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(userEntity.getUsername())).thenReturn(Optional.of(clientEntity));

        when(agreementRepository.findByClientId(clientEntity.getId())).thenReturn(List.of(agreementEntity));

        TrxDto actual1 = trxService.getById(1L, authentication);

        //проверка результата
        verify(trxMapper, atLeast(1)).toDto(any());
        assertThat(actual, hasItems(Long.valueOf(1L)));
        //  assertNotNull(actual);
    }

    @Test
    void testGetById_shouldNotFoundException_ifRoleUser_ifPresentTrx_ifPresentClientEntity_ifCheckOwnerIsPassed() {
        RoleEntity roleEntity = new RoleEntity();
        TrxEntity trxEntity = new TrxEntity();
        ClientEntity clientEntity = new ClientEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setAccount(accountEntity);
        trxEntity.setAccount(accountEntity);
        roleEntity.setName("ROLE_USER");
        List<Long> actual = Arrays.asList(1L, 2L, 3L);
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));
        when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(userEntity.getUsername())).thenReturn(Optional.of(clientEntity));

        when(agreementRepository.findByClientId(clientEntity.getId())).thenReturn(List.of(agreementEntity));
        // when(idsOfAgreementEntities.contains(1L)).thenReturn(true);

        TrxDto actual1 = trxService.getById(1L, authentication);

        //проверка результата
        verify(trxMapper, atLeast(1)).toDto(any());
        assertThat(actual, hasItems(Long.valueOf(1L)));
        //  assertNotNull(actual);
    }

    @Test
    void testFindByAccountId_shouldNotFoundException_ifRoleUser_ifEmptyClientInCheckOwner() {
        RoleEntity roleEntity = new RoleEntity();
        TrxEntity trxEntity = new TrxEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
        roleEntity.setName("ROLE_USER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));
        when(trxRepository.findByAccountId(any())).thenReturn(List.of(trxEntity));
        when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(userEntity.getUsername())).thenReturn(Optional.ofNullable(null));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.findByAccountId(1L, authentication));
        assertEquals("There is no client with such username-email", exception.getMessage());
    }

    @Test
    void testFindByAccountId_shouldNotFoundException_ifRoleUser_ifEmptyUserInCheckOwner() {
        RoleEntity roleEntity = new RoleEntity();
        TrxEntity trxEntity = new TrxEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
        roleEntity.setName("ROLE_USER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));
        when(trxRepository.findByAccountId(any())).thenReturn(List.of(trxEntity));
        when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.ofNullable(null));

        //  assertNotNull(actual);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.findByAccountId(1L, authentication));
        assertEquals("There is no user with such username-email", exception.getMessage());
    }

    @Test
    void testGetById_shouldReturnTrxDto_ifRoleManager_ifPresentTrx() {
        RoleEntity roleEntity = new RoleEntity();
        TrxEntity trxEntity = new TrxEntity();
        trxEntity.setId(1L);
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
        roleEntity.setName("ROLE_MANAGER");
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setId(1L);
        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setId(1L);
        agreementEntity.setAccount(accountEntity);
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(clientEntity));
        when(agreementRepository.findByClientId(any())).thenReturn(List.of(agreementEntity));
        when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntity));
        when(trxMapper.toDto(any())).thenReturn(new TrxDto());
        TrxDto actual = trxService.getById(1L, authentication);

        //проверка результата
        verify(trxMapper, atLeast(1)).toDto(any());
        assertNotNull(actual);
    }

    @Test
    void findByStatus() {
    }

    @Test
    void testCreateTrx_shouldValidationException_ifRoleIsNotUser() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("ROLE_MANAGER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));
        // when(trxRepository.findByAccountId(any())).thenReturn(List.of(trxEntity));
        // when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.ofNullable(null));

        //  assertNotNull(actual);
        ValidationException exception = assertThrows(ValidationException.class, () -> trxService.createTrx(new TrxDto(), authentication));
        assertEquals("Trx can be created only by clients", exception.getMessage());
    }

    @Test
    void testCreateTrx_shouldNotFoundException_ifRoleUser_ifCheckOwnerIsFalse() {
        RoleEntity roleEntity = new RoleEntity();
        // TrxEntity trxEntity = new TrxEntity();
        ClientEntity clientEntity = new ClientEntity();
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        UserEntity userEntity = new UserEntity();
        // trxEntity.setAccount(accountEntity);
        TrxDto trxDto = new TrxDto();
        trxDto.setAccountId(1L);
        roleEntity.setName("ROLE_USER");
        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setAccount(accountEntity);
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(clientEntity));
        when(agreementRepository.findByClientId(any())).thenReturn(List.of(agreementEntity));
//описать поведение агримент репозитория
        //  assertNotNull(actual);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.createTrx(trxDto, authentication));
        assertEquals("This account belongs to other user", exception.getMessage());
    }

    @Test
    void testCreateTrx_shouldNotFoundException_ifRoleUser_ifPresentClientEntity_inCheckOwner() {
        RoleEntity roleEntity = new RoleEntity();
        TrxEntity trxEntity = new TrxEntity();
        ClientEntity clientEntity = new ClientEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
        roleEntity.setName("ROLE_USER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));
        // when(trxRepository.findByAccountId(any())).thenReturn(List.of(trxEntity));
        when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(userEntity.getUsername())).thenReturn(Optional.of(clientEntity));

        //  assertNotNull(actual);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.createTrx(new TrxDto(), authentication));
        assertEquals("This account belongs to other user", exception.getMessage());
    }
//    @Transactional
//    @Override
//    public TrxDto createTrx(TrxDto trxDto, Authentication authentication) {
//        if (!authentication.getAuthorities().stream()
//                .anyMatch(r -> r.getAuthority().equalsIgnoreCase("ROLE_USER"))) {
//            throw new ValidationException("Trx can be created only by clients");
//        }
//        if (!checkOwner(trxDto.getAccountId(), authentication)) {
//            throw new NotFoundException("This account belongs to other user");
//        }
//        TrxEntity trxEntity = trxMapper.toEntity(trxDto);
//        Optional<AccountEntity> optAccountEntity = accountRepository.findById(trxDto.getAccountId());
//        if (optAccountEntity.isEmpty()) {
//            throw new NotFoundException("There is no such account" + trxDto.getAccountId());
//        }
//        AccountEntity accountEntity = optAccountEntity.get();
//        BigDecimal balanceBeforeTrx = accountEntity.getBalance();
//
//        //если операция дебитовая  тип 1
//        if (trxEntity.getTrxType() == TrxType.DEBIT) {
//            accountEntity.setBalance(balanceBeforeTrx.add(trxDto.getAmount()));
//        } else {
//            //если операция кредитовая  тип 2
//            //проверка достаточен ли баланс для проведения операции списания
//
//            BigDecimal amountTrx = trxDto.getAmount();
//            if (amountTrx.compareTo(balanceBeforeTrx) <= 0) {
//                accountEntity.setBalance(balanceBeforeTrx.subtract(trxDto.getAmount()));
//            } else log.info("Balance is not enough to proceed the operation");
//            throw new ValidationException("Balance is not enough to proceed the operation");
//        }
//        trxEntity.setAccount(accountEntity);
//        trxEntity.setStatus(Status.ACTIVE);
//        trxEntity.setTrxType(trxDto.getTrxType());
//        AccountEntity savedAccountEntity = accountRepository.saveAndFlush(accountEntity);
//        TrxEntity savedTrx = trxRepository.save(trxEntity);
//        log.info("Created and saved Trx with ID= {}", savedTrx.getId());
//        return trxMapper.toDto(savedTrx);
//    }

}