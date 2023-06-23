package org.devsu.service;

import org.devsu.common.Constants;
import org.devsu.dto.PrimaryUser;
import org.devsu.dto.requests.CreateAccountRequestDTO;
import org.devsu.dto.requests.UpdateAccountRequestDTO;
import org.devsu.entity.Account;
import org.devsu.entity.Client;
import org.devsu.entity.Person;
import org.devsu.enums.AccountType;
import org.devsu.enums.Role;
import org.devsu.enums.Status;
import org.devsu.repository.AccountRepository;
import org.devsu.repository.ClientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    private static final String NON_EXISTING_ACCOUNT_MESSAGE = "Record not found";
    private static final String ACCOUNT_ALREADY_EXISTS_MESSAGE = "An account with account number %s already exists";

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    public void init() {

    }

    @Test
    public void testCreate() throws Exception {
        CreateAccountRequestDTO request = createRequest();
        Client client = createClient(request.getClientId());
        Account account = createAccount(request, client);

        when(clientRepository.findById(request.getClientId())).thenReturn(Optional.of(client));
        when(accountRepository.findByAccountNumber(request.getAccountNumber())).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        assertEquals(request.getAccountNumber(), accountService.create(request).getAccountNumber());
    }

    @Test
    public void testRead() throws Exception {
        setCurrentUserInContext();

        String accountId = "12345";
        Account account = new Account();
        account.setAccountNumber(accountId);

        when(accountRepository.findByAccountNumber(accountId)).thenReturn(Optional.of(account));

        assertEquals(accountId, accountService.read(accountId).getAccountNumber());
    }

    @Test
    public void testUpdate() throws Exception {
        String accountNumber = "12345";
        UpdateAccountRequestDTO request = new UpdateAccountRequestDTO();
        request.setAccountNumber(accountNumber);
        request.setAccountType(AccountType.CHECKING);
        request.setStatus(Status.ACTIVE);
        Account account = new Account();
        account.setAccountNumber(accountNumber);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        assertEquals(accountNumber, accountService.update(request).getAccountNumber());
    }

    @Test
    public void testDelete_ExistingAccount() throws Exception {
        String accountNumber = "existingAccount";
        Account accountToDelete = new Account();
        accountToDelete.setAccountNumber(accountNumber);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(accountToDelete));
        accountService.delete(accountNumber);

        verify(accountRepository, times(1)).delete(accountToDelete);
    }

    @Test
    public void testCreate_AccountAlreadyExists() throws Exception {
        CreateAccountRequestDTO request = createRequest();
        Client client = createClient(request.getClientId());
        Account existingAccount = createAccount(request, client);

        when(clientRepository.findById(request.getClientId())).thenReturn(Optional.of(client));
        when(accountRepository.findByAccountNumber(request.getAccountNumber())).thenReturn(Optional.of(existingAccount));

        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            accountService.create(request);
        });

        assertEquals(String.format(ACCOUNT_ALREADY_EXISTS_MESSAGE, request.getAccountNumber()), exception.getMessage());
    }

    @Test
    public void testRead_NonExistingAccount() {
        String accountId = "nonExistingId";

        when(accountRepository.findByAccountNumber(accountId)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            accountService.read(accountId);
        });

        assertEquals(NON_EXISTING_ACCOUNT_MESSAGE, exception.getMessage());
    }

    @Test
    public void testDelete_NonExistingAccount() {
        String accountNumber = "nonExistingAccount";

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            accountService.delete(accountNumber);
        });

        assertEquals(NON_EXISTING_ACCOUNT_MESSAGE, exception.getMessage());
    }

    private CreateAccountRequestDTO createRequest() {
        CreateAccountRequestDTO request = new CreateAccountRequestDTO();
        request.setClientId(UUID.randomUUID());
        request.setAccountNumber("12345");
        request.setAccountType(AccountType.SAVINGS);
        request.setInitialBalance(1000.0);
        return request;
    }

    private Client createClient(UUID clientId) {
        Client client = new Client();
        client.setId(clientId);
        return client;
    }

    private Account createAccount(CreateAccountRequestDTO request, Client client) {
        Account account = new Account();
        account.setClient(client);
        account.setAccountNumber(request.getAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setInitialBalance(request.getInitialBalance());
        return account;
    }

    private void setCurrentUserInContext() {
        Person person = new Person();
        person.setIdentificationNumber("0941106445");

        Client client = new Client();
        client.setPerson(person);
        client.setRoles(new HashSet<>(Collections.singletonList(Role.ROLE_ADMIN.toString())));

        Map<String, Object> sessionMap = new HashMap<>();
        sessionMap.put(Constants.CURRENT_USER, client);

        PrimaryUser primaryUser = PrimaryUser.build(client, sessionMap);

        when(authentication.getPrincipal()).thenReturn(primaryUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }
}
