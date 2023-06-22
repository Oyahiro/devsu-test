package org.devsu.service;

import org.devsu.common.Exceptions;
import org.devsu.dto.requests.CreateMovementRequestDTO;
import org.devsu.dto.requests.UpdateMovementRequestDTO;
import org.devsu.dto.responses.MovementResponseDTO;
import org.devsu.entity.Account;
import org.devsu.entity.Movement;
import org.devsu.enums.MovementType;
import org.devsu.repository.AccountRepository;
import org.devsu.repository.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovementServiceTest {

    private static final String NON_EXISTING_MOVEMENT_MESSAGE = "Record not found";

    @InjectMocks
    private MovementService movementService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private MovementRepository movementRepository;

    private UUID id;
    private Movement movement;
    private Account account;
    private CreateMovementRequestDTO createMovementRequestDTO;
    private UpdateMovementRequestDTO updateMovementRequestDTO;

    @BeforeEach
    public void init() {
        id = UUID.randomUUID();
        account = new Account();
        account.setAccountNumber("12345");
        movement = new Movement();
        movement.setId(id);
        movement.setAccount(account);
        movement.setMovementType(MovementType.DEPOSIT);

        createMovementRequestDTO = new CreateMovementRequestDTO();
        createMovementRequestDTO.setAccountNumber("12345");
        createMovementRequestDTO.setValue(100.0);
        createMovementRequestDTO.setMovementType(MovementType.DEPOSIT);

        updateMovementRequestDTO = new UpdateMovementRequestDTO();
        updateMovementRequestDTO.setMovementId(id);
        updateMovementRequestDTO.setValue(200.0);
        updateMovementRequestDTO.setMovementType(MovementType.DEPOSIT);
    }

    @Test
    public void testRead() {
        when(movementRepository.findById(id)).thenReturn(Optional.of(movement));

        MovementResponseDTO result = movementService.read(id);
        assertEquals("12345", result.getAccountNumber());
    }

    @Test
    public void testCreate() throws Exception {
        when(accountRepository.findByAccountNumber(any())).thenReturn(Optional.of(account));
        when(movementRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        MovementResponseDTO result = movementService.create(createMovementRequestDTO);
        assertEquals(100.0, result.getMovementValue());
    }

    @Test
    public void testDelete() throws Exception {
        when(movementRepository.findById(id)).thenReturn(Optional.of(movement));
        movementService.delete(id);
        verify(movementRepository, times(1)).delete(movement);
    }

    @Test
    public void testRead_NonExistingMovement() {
        when(movementRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exceptions.RecordNotFoundException.class, () -> movementService.read(id));
        assertEquals(NON_EXISTING_MOVEMENT_MESSAGE, exception.getMessage());
    }

    @Test
    public void testDelete_NonExistingMovement() {
        when(movementRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exceptions.RecordNotFoundException.class, () -> movementService.delete(id));
        assertEquals(NON_EXISTING_MOVEMENT_MESSAGE, exception.getMessage());
    }

    @Test
    public void testCreate_InsufficientBalance() throws Exception {
        createMovementRequestDTO.setMovementType(MovementType.WITHDRAWAL);
        createMovementRequestDTO.setValue(5000.0);

        when(accountRepository.findByAccountNumber(any())).thenReturn(Optional.of(account));

        Exception exception = assertThrows(Exception.class, () -> movementService.create(createMovementRequestDTO));
        assertEquals("Balance not available", exception.getMessage());
    }

    @Test
    public void testDelete_WithSubsequentMovements() throws Exception {
        Movement subsequentMovement1 = new Movement();
        subsequentMovement1.setValue(200.0);
        subsequentMovement1.setMovementType(MovementType.DEPOSIT);
        Movement subsequentMovement2 = new Movement();
        subsequentMovement2.setValue(300.0);
        subsequentMovement2.setMovementType(MovementType.DEPOSIT);

        List<Movement> subsequentMovements = Arrays.asList(subsequentMovement1, subsequentMovement2);

        when(movementRepository.findById(id)).thenReturn(Optional.of(movement));
        when(movementRepository.findAllByAccountAndDateAfterOrderByDateAsc(any(), any())).thenReturn(subsequentMovements);

        movementService.delete(id);

        verify(movementRepository, times(1)).delete(movement);
        verify(movementRepository, times(1)).saveAll(subsequentMovements);
    }

    @Test
    public void testDelete_ThrowsBalanceCalculationException() {
        Movement subsequentMovement = new Movement();
        subsequentMovement.setValue(200.0);
        subsequentMovement.setMovementType(MovementType.WITHDRAWAL);
        subsequentMovement.setBalance(0.0);

        List<Movement> subsequentMovements = List.of(subsequentMovement);

        movement.setValue(300.0);
        movement.setBalance(100.0);

        when(movementRepository.findById(id)).thenReturn(Optional.of(movement));
        when(movementRepository.findAllByAccountAndDateAfterOrderByDateAsc(any(), any())).thenReturn(subsequentMovements);

        Exception exception = assertThrows(Exceptions.BalanceCalculationException.class, () -> movementService.delete(id));
        assertEquals("It is not possible to update the balance sheets", exception.getMessage());

        verify(movementRepository, times(0)).delete(any());
        verify(movementRepository, times(0)).saveAll(any());
    }

    @Test
    public void testUpdate() {
        when(movementRepository.findById(id)).thenReturn(Optional.of(movement));
        when(movementRepository.findAllByAccountAndDateAfterOrderByDateAsc(any(), any())).thenReturn(new ArrayList<>());
        when(movementRepository.saveAll(any())).thenAnswer(i -> i.getArguments()[0]);

        MovementResponseDTO result = movementService.update(updateMovementRequestDTO);
        assertEquals(200.0, result.getMovementValue());
    }

    @Test
    public void testUpdate_NonExistingMovement() {
        when(movementRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exceptions.RecordNotFoundException.class, () -> movementService.update(updateMovementRequestDTO));
        assertEquals(NON_EXISTING_MOVEMENT_MESSAGE, exception.getMessage());
    }

    @Test
    public void testUpdate_ThrowsBalanceCalculationException() {
        Movement subsequentMovement1 = new Movement();
        subsequentMovement1.setValue(100.0);
        subsequentMovement1.setMovementType(MovementType.WITHDRAWAL);
        Movement subsequentMovement2 = new Movement();
        subsequentMovement2.setValue(105.0);
        subsequentMovement2.setMovementType(MovementType.WITHDRAWAL);

        List<Movement> subsequentMovements = List.of(subsequentMovement1, subsequentMovement2);

        movement.setValue(300.0);
        movement.setBalance(100.0);

        when(movementRepository.findById(id)).thenReturn(Optional.of(movement));
        when(movementRepository.findAllByAccountAndDateAfterOrderByDateAsc(any(), any())).thenReturn(subsequentMovements);

        Exception exception = assertThrows(Exceptions.BalanceCalculationException.class, () -> movementService.update(updateMovementRequestDTO));
        assertEquals("It is not possible to update the balance sheets", exception.getMessage());

        verify(movementRepository, times(0)).saveAll(any());
    }

    @Test
    void createShouldThrowExceptionWhenAccountDoesNotExist() {
        CreateMovementRequestDTO request = new CreateMovementRequestDTO();
        request.setAccountNumber("nonExistingAccountNumber");

        when(accountRepository.findByAccountNumber(request.getAccountNumber())).thenReturn(Optional.empty());

        assertThrows(Exceptions.AccountNotFoundException.class, () ->
                movementService.create(request));
    }

    @Test
    public void testCreateMovementThrowsExceedsDailyWithdrawalLimitException() throws Exception {
        // arrange
        String accountNumber = "123456";
        double withdrawalAmount = 210.0;
        double todayTotal = 800.0;

        Account account = new Account();
        account.setAccountNumber(accountNumber);

        CreateMovementRequestDTO movementDTO = new CreateMovementRequestDTO();
        movementDTO.setAccountNumber(accountNumber);
        movementDTO.setMovementType(MovementType.WITHDRAWAL);
        movementDTO.setValue(withdrawalAmount);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
        when(movementRepository.findTotalValueByMovementTypeAndAccountNumberForToday(accountNumber, MovementType.WITHDRAWAL))
                .thenReturn(todayTotal);

        // assert
        assertThrows(Exceptions.ExceedsDailyWithdrawalLimitException.class, () -> {
            // act
            movementService.create(movementDTO);
        });
    }

}
