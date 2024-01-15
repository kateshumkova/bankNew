package com.example.banknew.service.impl;

import com.example.banknew.dtos.ClientDto;
import com.example.banknew.dtos.TrxDto;
import com.example.banknew.entities.*;
import com.example.banknew.exception.NotFoundException;
import com.example.banknew.exception.ValidationException;
import com.example.banknew.mappers.TrxMapper;
import com.example.banknew.repository.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
    private TrxMapper trxMapper;

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
//    @Test
//    void testCheckOwner_shouldNotFoundException_ifEmptyProduct() {
//       boolean result = assertThrows(NotFoundException.class, () -> checkOwner(1L, authentication));
//        assertEquals("There is no trx for this account 1", exception.getMessage());
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
    void testGetById_shouldNotFoundException_ifEmptyTrx() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("ROLE_USER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> trxService.getById(1L, authentication));
        assertEquals("Trx 1 is not found", exception.getMessage());
    }

    @Test
    void testGetById_shouldReturnTrxDto_ifPresentTrx() {
        RoleEntity roleEntity = new RoleEntity();
        TrxEntity trxEntity = new TrxEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
        roleEntity.setName("ROLE_USER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));
        when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.of(userEntity));

        TrxDto actual = trxService.getById(1L, authentication);

        //проверка результата
        verify(trxMapper, atLeast(1)).toDto(any());
        //  assertNotNull(actual);
    }

    @Test
    void testGetById_shouldReturnTrxDto_ifPresentTrx_ifEmptyUserEntity() {
        RoleEntity roleEntity = new RoleEntity();
        TrxEntity trxEntity = new TrxEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
        roleEntity.setName("ROLE_USER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));
        when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.ofNullable(null));

        //  assertNotNull(actual);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.getById(1L, authentication));
        assertEquals("There is no user with such username-email", exception.getMessage());

    }

    @Test
    void testGetById_shouldReturnTrxDto_ifPresentTrx_ifEmptyClientEntity() {
        RoleEntity roleEntity = new RoleEntity();
        TrxEntity trxEntity = new TrxEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        trxEntity.setAccount(accountEntity);
        roleEntity.setName("ROLE_USER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));
        when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.of(userEntity));

        //  assertNotNull(actual);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> trxService.getById(1L, authentication));
        assertEquals("There is no client with such username-email", exception.getMessage());
    }

    @Test
    void testGetById_shouldReturnTrxDto_ifPresentTrx_ifPresentClientEntity() {
        RoleEntity roleEntity = new RoleEntity();
        TrxEntity trxEntity = new TrxEntity();
        ClientEntity clientEntity = new ClientEntity();
        AccountEntity accountEntity = new AccountEntity();
        UserEntity userEntity = new UserEntity();
        AgreementEntity agreementEntity = new AgreementEntity();
        agreementEntity.setAccount(accountEntity);
        trxEntity.setAccount(accountEntity);
        roleEntity.setName("ROLE_USER");
        List<Long> idsOfAgreementEntities = Stream.of(agreementEntity).map(ae -> ae.getAccount().getId()).toList();
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, List.of(roleEntity));
        when(trxRepository.findById(any())).thenReturn(Optional.of(trxEntity));
        when(userRepository.findByUsername(authentication.getName())).thenReturn(Optional.of(userEntity));
        when(clientRepository.findByEmail(userEntity.getUsername())).thenReturn(Optional.of(clientEntity));

        when(agreementRepository.findByClientId(clientEntity.getId())).thenReturn(List.of(agreementEntity));
       // when(idsOfAgreementEntities.contains(1L)).thenReturn(true);

        TrxDto actual = trxService.getById(1L, authentication);

        //проверка результата
        verify(trxMapper, atLeast(1)).toDto(any());
        //  assertNotNull(actual);
    }

    @Test
    void findByAccountId() {
    }

    @Test
    void findByStatus() {
    }

    @Test
    void createTrx() {
    }


}