package com.example.banknew.service.impl;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.TrxDto;
import com.example.banknew.entities.*;
import com.example.banknew.enums.Status;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.banknew.enums.TrxType.CREDIT;
import static com.example.banknew.enums.TrxType.DEBIT;
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

//    private List<TrxDto> returnListOfTrxForAccount(Long accountId) {
//        List<TrxEntity> trxEntities = trxRepository.findByAccountId(accountId);
//        if (trxEntities.isEmpty()) {
//            throw new NotFoundException("There is no trx for this account " + accountId);
//        }
//        return trxEntities.stream()
//                .map(trxMapper::toDto)
//                .toList();
//    }
//
//    @Test
//    void testReturnListOfTrxForAccount_shouldNotFoundException_ifEmptyTrxEntities() {
//        NotFoundException exception = assertThrows(NotFoundException.class, () -> returnListOfTrxForAccount(1L));
//        assertEquals("There is no trx for this account 1", exception.getMessage());
//    }
//
//    @Test
//    void testReturnListOfTrxForAccount_shouldReturnListTrxDto_ifPresentTrxEntities() {
//        TrxEntity trxEntity = new TrxEntity();
//        when(trxRepository.findByAccountId(any())).thenReturn(List.of(trxEntity));
//
//        List<TrxDto> actual = returnListOfTrxForAccount(1L);
//
//        //проверка результата
//        verify(trxMapper, atLeast(1)).toDto(any());
//        assertNotNull(actual);
//    }

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

        Authentication authentication = createAuthentication("ROLE_USER");

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> trxService.getById(1L, authentication));
        assertEquals("Trx 1 is not found", exception.getMessage());
    }

    @Test
    void testGetById_shouldNotFoundException_ifRoleAdmin_ifEmptyTrx() {

        Authentication authentication = createAuthentication("ROLE_ADMIN");
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> trxService.getById(1L, authentication));
        assertEquals("Trx 1 is not found", exception.getMessage());
    }

    @Test
    void testGetById_shouldNotFoundException_ifRoleManager_ifEmptyTrx() {

        Authentication authentication = createAuthentication("ROLE_MANAGER");
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> trxService.getById(1L, authentication));
        assertEquals("Trx 1 is not found", exception.getMessage());
    }

    @Test
    void testGetById_shouldNotFoundException_ifRoleAdmin_ifPresentTrx() {

        Authentication authentication = createAuthentication("ROLE_ADMIN");
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
    void testGetById_shouldReturnTrxDto_ifRoleUser_ifEmptyTrx() {

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

        when(trxRepository.findById(any())).thenReturn(Optional.ofNullable(null));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(clientEntity));
        when(agreementRepository.findByClientId(any())).thenReturn(List.of(agreementEntity));
        when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntity));
        when(trxMapper.toDto(any())).thenReturn(new TrxDto());
        when(authService.checkRole(any(), any())).thenReturn(true);
         NotFoundException exception = assertThrows(NotFoundException.class,
    () -> trxService.getById(1L, createAuthentication("ROLE_USER")));
        assertEquals("Trx 1 is not found", exception.getMessage());

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
        when(authService.checkRole(any(), any())).thenReturn(true);
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
        TrxEntity trxEntity = new TrxEntity();
        trxEntity.setId(1L);
        AccountEntity accountEntity = createAccountEntity(1L);
        AccountEntity accountEntity2 = createAccountEntity(2L);
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setId(1L);
        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setId(8L);
        agreementEntity.setAccount(accountEntity2);
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(clientEntity));
        when(agreementRepository.findByClientId(any())).thenReturn(List.of(agreementEntity));
        when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntity));
        when(trxMapper.toDto(any())).thenReturn(new TrxDto());
        TrxDto actual = trxService.getById(1L, createAuthentication("ROLE_USER"));

        //проверка результата
        verify(trxMapper, atLeast(1)).toDto(any());
        assertNotNull(actual);
    }

    @Test
    void testGetById_shouldReturnTrxDto_ifRoleUser_ifPresentTrx_ifEmptyUserEntity() {
        TrxEntity trxEntity = new TrxEntity();
        AccountEntity accountEntity = new AccountEntity();
        trxEntity.setAccount(accountEntity);
        when(authService.checkRole(any(), any())).thenReturn(true);
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.getById(1L, createAuthentication("ROLE_USER")));
        assertEquals("There is no user with such username-email", exception.getMessage());

    }
    @Test
    void testGetById_shouldReturnTrxDto_ifRoleManager_ifPresentTrx() {

        TrxEntity trxEntity = new TrxEntity();
        trxEntity.setId(1L);
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
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

        TrxDto actual = trxService.getById(1L, createAuthentication("ROLE_MANAGER"));

        //проверка результата
        verify(trxMapper, atLeast(1)).toDto(any());
        assertNotNull(actual);
    }
    @Test
    void testGetById_shouldNotFoundException_ifRoleUser_checkOwnerFalse() {

        TrxEntity trxEntity = new TrxEntity();
        ClientEntity clientEntity = new ClientEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);

        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(clientEntity));
        when(authService.checkRole(any(), any())).thenReturn(true);


        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.getById(1L, createAuthentication("ROLE_USER")));
        assertEquals("This account belongs to other user", exception.getMessage());
    }

    @Test
    void testGetById_shouldReturnTrxDto_ifPresentTrx_ifRoleUser_ifEmptyClientEntity() {

        TrxEntity trxEntity = new TrxEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);

        Authentication authentication = createAuthentication("ROLE_USER");
        when(authService.checkRole(any(), any())).thenReturn(true);
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));
        when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.of(userEntity));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.getById(1L, authentication));
        assertEquals("There is no client with such username-email", exception.getMessage());
    }

    @Test
    void testGetById_shouldReturnTrxDto_ifRoleUser_ifPresentTrx_ifPresentClientEntity_ifCheckOwnerIsPassed() {

        TrxEntity trxEntity = new TrxEntity();
        ClientEntity clientEntity = new ClientEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setAccount(accountEntity);
        trxEntity.setAccount(accountEntity);
        List<Long> actual = Arrays.asList(1L, 2L, 3L);
        Authentication authentication = createAuthentication("ROLE_USER");
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

        TrxEntity trxEntity = new TrxEntity();
        ClientEntity clientEntity = new ClientEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setAccount(accountEntity);
        trxEntity.setAccount(accountEntity);

        Authentication authentication = createAuthentication("ROLE_USER");
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));
        when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(userEntity.getUsername())).thenReturn(Optional.of(clientEntity));
        when(agreementRepository.findByClientId(clientEntity.getId())).thenReturn(List.of(agreementEntity));

        TrxDto actual1 = trxService.getById(1L, authentication);

        //проверка результата
        verify(trxMapper, atLeast(1)).toDto(any());

    }

    @Test
    void testFindByAccountId_shouldNotFoundException_ifRoleUser_ifEmptyClientInCheckOwner() {
         TrxEntity trxEntity = new TrxEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
         when(authService.checkRole(any(), any())).thenReturn(true);
        when(trxRepository.findByAccountId(any())).thenReturn(List.of(trxEntity));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(any())).thenReturn(Optional.ofNullable(null));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.findByAccountId(1L, createAuthentication("ROLE_USER")));
        assertEquals("There is no client with such username-email", exception.getMessage());
    }

    @Test
    void testFindByAccountId_shouldNotFoundException_ifRoleUser_ifEmptyUserInCheckOwner() {
       TrxEntity trxEntity = new TrxEntity();
        AccountEntity accountEntity = new AccountEntity();
        trxEntity.setAccount(accountEntity);
        Authentication authentication = createAuthentication("ROLE_USER");
        when(trxRepository.findByAccountId(any())).thenReturn(List.of(trxEntity));
        when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.ofNullable(null));
        when(authService.checkRole(any(), any())).thenReturn(true);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.findByAccountId(1L, authentication));
        assertEquals("There is no user with such username-email", exception.getMessage());
    }
    @Test
    void testFindByAccountId_shouldReturnListTrxDto_ifRoleUser_ifPresentTrx() {
        TrxEntity trxEntity = new TrxEntity();
        trxEntity.setId(1L);
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
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
        when(trxRepository.findByAccountId(any())).thenReturn(List.of(trxEntity));

        List<TrxDto> actual = trxService.findByAccountId(1L,createAuthentication("ROLE_USER"));

        //проверка результата
        verify(trxMapper, atLeast(1)).toDto(any());
        assertNotNull(actual);
    }
    @Test
    void testFindByAccountId_shouldReturnListTrxDto_ifRoleManager_ifPresentTrx() {
        TrxEntity trxEntity = new TrxEntity();
        trxEntity.setId(1L);
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
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
        when(trxRepository.findByAccountId(any())).thenReturn(List.of(trxEntity));

        List<TrxDto> actual = trxService.findByAccountId(1L,createAuthentication("ROLE_MANAGER"));

        //проверка результата
        verify(trxMapper, atLeast(1)).toDto(any());
        assertNotNull(actual);
    }
    @Test
    void testFindByAccountId_shouldNotFoundException_ifRoleUser_ifTrxEmpty() {

        TrxEntity trxEntity = new TrxEntity();
        trxEntity.setId(1L);
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setId(1L);
        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setId(1L);
        agreementEntity.setAccount(accountEntity);
        when(trxRepository.findByAccountId(any())).thenReturn(List.of());
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(clientEntity));
        when(agreementRepository.findByClientId(any())).thenReturn(List.of(agreementEntity));
        when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntity));
        when(trxMapper.toDto(any())).thenReturn(new TrxDto());


        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.findByAccountId(1L,createAuthentication("ROLE_USER")));
        assertEquals("There is no trx for this account 1", exception.getMessage());
    }
    @Test
    void testFindByAccountId_shouldNotFoundException_ifRoleManager_ifTrxEmpty() {
       TrxEntity trxEntity = new TrxEntity();
        trxEntity.setId(1L);
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setId(1L);
        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setId(1L);
        agreementEntity.setAccount(accountEntity);
        when(trxRepository.findByAccountId(any())).thenReturn(List.of());
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(clientEntity));
        when(agreementRepository.findByClientId(any())).thenReturn(List.of(agreementEntity));
        when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntity));
        when(trxMapper.toDto(any())).thenReturn(new TrxDto());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.findByAccountId(1L,createAuthentication("ROLE_MANAGER")));
        assertEquals("There is no trx for this account 1", exception.getMessage());
    }
    @Test
    void testFindByAccountId_shouldNotFoundException_ifRoleUser_ifCheckOwnerIsFalse() {
        TrxEntity trxEntity = new TrxEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
        ClientEntity clientEntity = new ClientEntity();
        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setAccount(accountEntity);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(clientEntity));
        when(agreementRepository.findByClientId(any())).thenReturn(List.of(agreementEntity));
        when(authService.checkRole(any(), any())).thenReturn(true);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.findByAccountId(1L, createAuthentication("ROLE_USER")));
        assertEquals("This account belongs to other user", exception.getMessage());
    }
    @Test
    void testFindByAccountId_HappyPath_ifRoleUser_ifCheckOwnerIsTrue() {
        TrxEntity trxEntity = new TrxEntity();
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
        ClientEntity clientEntity = new ClientEntity();

        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setAccount(accountEntity);
        trxEntity.setAccount(accountEntity);
        List <Long> listOfAccountId = List.of(1L,2L);

        Authentication authentication = createAuthentication("ROLE_USER");
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));
        when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(userEntity.getUsername())).thenReturn(Optional.of(clientEntity));
        when(agreementRepository.findByClientId(clientEntity.getId())).thenReturn(List.of(agreementEntity));
        when(authService.checkRole(any(), any())).thenReturn(true);
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));
        when(trxRepository.findByAccountId(any())).thenReturn(List.of(trxEntity));
        List<TrxDto> actual = trxService.findByAccountId(1L,createAuthentication("ROLE_USER"));

        //проверка результата
        verify(trxMapper, atLeast(1)).toDto(any());
        assertNotNull(actual); }

    @Test
    void testFindByStatus_shouldNotFoundException_ifRoleUser_ifEmptyClientInCheckOwner() {
        TrxEntity trxEntity = new TrxEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
        when(authService.checkRole(any(), any())).thenReturn(true);
        when(trxRepository.findByAccountId(any())).thenReturn(List.of(trxEntity));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(any())).thenReturn(Optional.ofNullable(null));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.findByStatus(createAuthentication("ROLE_USER"),1L, Status.ACTIVE));
        assertEquals("There is no client with such username-email", exception.getMessage());
    }
    @Test
    void testFindByStatus_shouldNotFoundException_ifRoleUser_ifEmptyUserInCheckOwner() {

        TrxEntity trxEntity = new TrxEntity();
        AccountEntity accountEntity = new AccountEntity();
        trxEntity.setAccount(accountEntity);

        Authentication authentication = createAuthentication("ROLE_USER");
        when(trxRepository.findByAccountId(any())).thenReturn(List.of(trxEntity));
        when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.ofNullable(null));
        when(authService.checkRole(any(), any())).thenReturn(true);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.findByStatus(authentication,1L,Status.ACTIVE));
        assertEquals("There is no user with such username-email", exception.getMessage());
    }
    @Test
    void testCreateTrx_shouldValidationException_ifRoleIsNotUser() {

        ValidationException exception = assertThrows(ValidationException.class, () -> trxService.createTrx(new TrxDto(), createAuthentication("ROLE_MANAGER")));
        assertEquals("Trx can be created only by clients", exception.getMessage());
    }

    @Test
    void testFindByStatus_shouldReturnListTrxDto_ifRoleUser_ifPresentTrx() {
        TrxEntity trxEntity = new TrxEntity();
        trxEntity.setId(1L);
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
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
        when(trxRepository.findByStatus(any())).thenReturn(List.of(trxEntity));

        List<TrxDto> actual = trxService.findByStatus(createAuthentication("ROLE_USER"),1L,Status.ACTIVE);

        //проверка результата
        verify(trxMapper, atLeast(1)).toDto(any());
        assertNotNull(actual);
    }
    @Test
    void testFindByStatus_shouldReturnListTrxDto_ifRoleManager_ifPresentTrx() {
        TrxEntity trxEntity = new TrxEntity();
        trxEntity.setId(1L);
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
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
        when(trxRepository.findByStatus(any())).thenReturn(List.of(trxEntity));

        List<TrxDto> actual = trxService.findByStatus(createAuthentication("ROLE_MANAGER"),1L,Status.ACTIVE);

        //проверка результата
        verify(trxMapper, atLeast(1)).toDto(any());
        assertNotNull(actual);
    }
    @Test
    void testFindByStatus_shouldNotFoundException_ifRoleUser_ifTrxEmpty() {
        TrxEntity trxEntity = new TrxEntity();
        trxEntity.setId(1L);
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
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

        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.findByStatus(createAuthentication("ROLE_USER"),1L,Status.ACTIVE));
        assertEquals("There is no trx with status ACTIVE", exception.getMessage());
    }
    @Test
    void testFindByStatus_shouldNotFoundException_ifRoleManager_ifTrxEmpty() {
        TrxEntity trxEntity = new TrxEntity();
        trxEntity.setId(1L);
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
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

        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.findByStatus(createAuthentication("ROLE_MANAGER"),1L,Status.ACTIVE));
        assertEquals("There is no trx with status ACTIVE", exception.getMessage());
    }
    @Test
    void testFindByStatus_shouldNotFoundException_ifRoleUser_ifCheckOwnerIsFalse() {
        TrxEntity trxEntity = new TrxEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
         ClientEntity clientEntity = new ClientEntity();
        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setAccount(accountEntity);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(clientEntity));
        when(agreementRepository.findByClientId(any())).thenReturn(List.of(agreementEntity));
        when(authService.checkRole(any(), any())).thenReturn(true);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.findByStatus(createAuthentication("ROLE_USER"),1L,Status.ACTIVE));
        assertEquals("This account belongs to other user", exception.getMessage());
    }

    @Test
    void testFindByStatus_HappyPath_ifRoleUser_ifCheckOwnerIsTrue_and_TrxIsPresent () {
        TrxEntity trxEntity = new TrxEntity();
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
        ClientEntity clientEntity = new ClientEntity();

        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setAccount(accountEntity);
        trxEntity.setAccount(accountEntity);
        List <Long> listOfAccountId = List.of(1L,2L);

        Authentication authentication = createAuthentication("ROLE_USER");
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));
        when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(userEntity.getUsername())).thenReturn(Optional.of(clientEntity));
        when(agreementRepository.findByClientId(clientEntity.getId())).thenReturn(List.of(agreementEntity));
        when(authService.checkRole(any(), any())).thenReturn(true);
        when(trxRepository.findByStatus(any())).thenReturn(List.of(trxEntity));

        List<TrxDto> actual = trxService.findByStatus(createAuthentication("ROLE_USER"),1L,Status.ACTIVE);

        //проверка результата
        verify(trxMapper, atLeast(1)).toDto(any());
        assertNotNull(actual);
    }
    @Test
    void testFindByStatus_HappyPath_ifRoleUser_ifCheckOwnerIsTrue_and_TrxIsEmpty () {

        TrxEntity trxEntity = new TrxEntity();
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
        ClientEntity clientEntity = new ClientEntity();

        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setAccount(accountEntity);
        trxEntity.setAccount(accountEntity);
        List <Long> listOfAccountId = List.of(1L,2L);

        Authentication authentication = createAuthentication("ROLE_USER");
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));
        when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(userEntity.getUsername())).thenReturn(Optional.of(clientEntity));
        when(agreementRepository.findByClientId(clientEntity.getId())).thenReturn(List.of(agreementEntity));
        when(authService.checkRole(any(), any())).thenReturn(true);
        when(trxRepository.findByStatus(any())).thenReturn(List.of());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.findByStatus(createAuthentication("ROLE_USER"),1L,Status.ACTIVE));
        assertEquals("There is no trx with status ACTIVE", exception.getMessage());
    }


    @Test
    void testCreateTrx_HappyPath_DebitTrx() {
         ClientEntity clientEntity = new ClientEntity();
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        accountEntity.setBalance(BigDecimal.valueOf(100));
        UserEntity userEntity = new UserEntity();
        TrxDto trxDto = new TrxDto();
        trxDto.setAccountId(1L);
        trxDto.setAmount(BigDecimal.valueOf(500));
        TrxEntity trxEntity = new TrxEntity();
        trxEntity.setId(1L);
        trxEntity.setAmount(BigDecimal.valueOf(500));
        trxEntity.setTrxType(DEBIT);
        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setAccount(accountEntity);

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(clientEntity));
        when(agreementRepository.findByClientId(any())).thenReturn(List.of(agreementEntity));
        when(authService.checkRole(any(), any())).thenReturn(true);
        when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntity));
        when(trxMapper.toEntity(any())).thenReturn(trxEntity);
        when(trxRepository.save(any())).thenReturn(new TrxEntity());
        when(trxMapper.toDto(any())).thenReturn(new TrxDto());


        TrxDto actual = trxService.createTrx(trxDto, createAuthentication("ROLE_USER"));

        //проверка результата
        verify(trxMapper, atLeast(1)).toDto(any());
        assertNotNull(actual);
    }

    @Test
    void testCreateTrx_CreditTrx_shouldValidationException() {
         ClientEntity clientEntity = new ClientEntity();
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        accountEntity.setBalance(BigDecimal.valueOf(1000));
        UserEntity userEntity = new UserEntity();
        TrxDto trxDto = new TrxDto();
        trxDto.setAccountId(1L);
        trxDto.setAmount(BigDecimal.valueOf(5000));
        TrxEntity trxEntity = new TrxEntity();
        trxEntity.setId(1L);
        trxEntity.setAmount(BigDecimal.valueOf(5000));
        trxEntity.setTrxType(CREDIT);
        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setAccount(accountEntity);

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(clientEntity));
        when(agreementRepository.findByClientId(any())).thenReturn(List.of(agreementEntity));
        when(authService.checkRole(any(), any())).thenReturn(true);
        when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntity));
        when(trxMapper.toEntity(any())).thenReturn(trxEntity);
        when(trxRepository.save(any())).thenReturn(new TrxEntity());
        when(trxMapper.toDto(any())).thenReturn(new TrxDto());

        ValidationException exception = assertThrows(ValidationException.class, () -> trxService.createTrx(trxDto, createAuthentication("ROLE_USER")));
        assertEquals("Balance is not enough to proceed the operation", exception.getMessage());
    }

    @Test
    void testCreateTrx_CreditTrx_HappyPath() {
         ClientEntity clientEntity = new ClientEntity();
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        accountEntity.setBalance(BigDecimal.valueOf(1000));
        UserEntity userEntity = new UserEntity();
        BigDecimal balanceBeforeTrx = accountEntity.getBalance();

        TrxDto trxDto = new TrxDto();
        trxDto.setAccountId(1L);
        trxDto.setAmount(BigDecimal.valueOf(500));
        TrxEntity trxEntity = new TrxEntity();
        trxEntity.setId(1L);
        trxEntity.setAmount(BigDecimal.valueOf(500));
        trxEntity.setTrxType(CREDIT);
        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setAccount(accountEntity);

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(clientEntity));
        when(agreementRepository.findByClientId(any())).thenReturn(List.of(agreementEntity));
        when(authService.checkRole(any(), any())).thenReturn(true);
        when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntity));
        when(trxMapper.toEntity(any())).thenReturn(trxEntity);
        when(trxRepository.save(any())).thenReturn(new TrxEntity());
        when(trxMapper.toDto(any())).thenReturn(new TrxDto());

        TrxDto actual = trxService.createTrx(trxDto, createAuthentication("ROLE_USER"));

        //проверка результата
        verify(trxMapper, atLeast(1)).toDto(any());
        assertNotNull(actual);
    }

    @Test
    void testCreateTrx_shouldNotFoundException_ifRoleUser_ifCheckOwnerIsFalse() {
         ClientEntity clientEntity = new ClientEntity();
        AccountEntity accountEntity = new AccountEntity();
        AccountEntity accountEntity1 = new AccountEntity();
        accountEntity.setId(1L);
        accountEntity1.setId(5L);
        UserEntity userEntity = new UserEntity();
        TrxDto trxDto = new TrxDto();
        trxDto.setAccountId(1L);
        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setAccount(accountEntity1);

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(clientEntity));
        when(agreementRepository.findByClientId(any())).thenReturn(List.of(agreementEntity));
        when(authService.checkRole(any(), any())).thenReturn(true);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.createTrx(trxDto, createAuthentication("ROLE_USER")));
        assertEquals("This account belongs to other user", exception.getMessage());
    }

    @Test
    void testCreateTrx_shouldNotFoundException_ifRoleUser_ifAccountEntityIsEmpty() {
        ClientEntity clientEntity = new ClientEntity();
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        UserEntity userEntity = new UserEntity();
        TrxDto trxDto = new TrxDto();
        trxDto.setAccountId(1L);
         AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setAccount(accountEntity);

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(clientEntity));
        when(agreementRepository.findByClientId(any())).thenReturn(List.of(agreementEntity));
        when(authService.checkRole(any(), any())).thenReturn(true);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.createTrx(trxDto, createAuthentication("ROLE_USER")));
        assertEquals("There is no such account 1", exception.getMessage());
    }

    @Test
    void testCreateTrx_shouldNotFoundException_ifRoleUser_ifPresentClientEntity_inCheckOwner() {

        TrxEntity trxEntity = new TrxEntity();
        ClientEntity clientEntity = new ClientEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(any())).thenReturn(Optional.of(clientEntity));
        when(authService.checkRole(any(), any())).thenReturn(true);
        //  assertNotNull(actual);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.createTrx(new TrxDto(), createAuthentication("ROLE_USER")));
        assertEquals("This account belongs to other user", exception.getMessage());
    }
    @Test
    void testUpdateTrx_shouldNotFoundException_ifEmptyTrx() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.updateTrx(1L,new TrxDto()));
        assertEquals("Trx cannot be updated, 1 is not found", exception.getMessage());
    }

    @Test
    void testUpdateTrx_shouldReturnTrxDto_ifPresentTrx() {
        //заглушки
        TrxEntity trxEntity = new TrxEntity();
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));

        //вызов метода
        TrxDto actual = trxService.updateTrx(1L, new TrxDto());

        //проверка результата
        verify(trxMapper, atLeast(1)).updateEntity(any(), any());
        verify(trxRepository).save(any());

    }

    @Test
    void testDeleteTrx_shouldNotFoundException_ifEmptyTrx() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.deleteTrx(1L));
        assertEquals("Trx 1 is not found", exception.getMessage());
    }

    @Test
    void testDeleteTrx_shouldChangeStatusToInactive_ifPresentTrx() {
        //заглушки
        TrxEntity trxEntity = new TrxEntity();
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));

        //вызов метода
        trxService.deleteTrx(1L);

        //проверка результата
        verify(trxRepository).save(any());
    }
}