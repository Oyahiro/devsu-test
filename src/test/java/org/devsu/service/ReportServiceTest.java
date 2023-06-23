package org.devsu.service;

import org.devsu.common.Constants;
import org.devsu.common.Exceptions;
import org.devsu.dto.PrimaryUser;
import org.devsu.dto.reports.BankStatementsReportDTO;
import org.devsu.entity.Account;
import org.devsu.entity.Client;
import org.devsu.entity.Movement;
import org.devsu.entity.Person;
import org.devsu.enums.MovementType;
import org.devsu.enums.Role;
import org.devsu.repository.AccountRepository;
import org.devsu.repository.ClientRepository;
import org.devsu.repository.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private MovementRepository movementRepository;

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
    public void testGetBankStatement() {
        setCurrentUserInContext();

        UUID clientId = UUID.randomUUID();
        Client client = new Client();
        client.setId(clientId);
        Account account = new Account();
        account.setClient(client);
        Movement movement = new Movement();
        movement.setAccount(account);
        movement.setMovementType(MovementType.DEPOSIT);
        List<Account> accounts = List.of(account);
        List<Movement> movements = List.of(movement);
        Date startDate = new Date();
        Date endDate = new Date();

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(accountRepository.findAllByClient(client)).thenReturn(accounts);
        when(movementRepository.findByAccountAndDateBetweenOrderByDateAsc(any(Account.class), any(), any())).thenReturn(movements);

        BankStatementsReportDTO result = reportService.getBankStatement(clientId, startDate, endDate);

        assertEquals(1, result.getBankStatements().size());
        verify(clientRepository, times(1)).findById(clientId);
        verify(accountRepository, times(1)).findAllByClient(client);
        verify(movementRepository, times(1)).findByAccountAndDateBetweenOrderByDateAsc(any(Account.class), any(), any());
    }

    @Test
    public void testFindClientByIdThrowsExceptionWhenNotFound() {
        UUID clientId = UUID.randomUUID();

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(Exceptions.RecordNotFoundException.class, () -> reportService.getBankStatement(clientId, new Date(), new Date()));
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
