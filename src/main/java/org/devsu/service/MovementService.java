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
import org.devsu.service.interfaces.IMovementService;
import org.devsu.utils.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.devsu.common.Constants.DAILY_WITHDRAWAL_LIMIT;

@Service
public class MovementService implements IMovementService {

    private final Logger LOG = LoggerFactory.getLogger(MovementService.class);

    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;

    public MovementService(AccountRepository accountRepository, MovementRepository movementRepository) {
        this.accountRepository = accountRepository;
        this.movementRepository = movementRepository;
    }

    @Override
    public MovementResponseDTO read(UUID movementId) {
        Optional<Movement> optionalMovement = movementRepository.findById(movementId);
        if (optionalMovement.isEmpty()) {
            LOG.error(String.format("The movement with id %s does not exist", movementId));
            throw new Exceptions.RecordNotFoundException("Record not found");
        }

        return new MovementResponseDTO(optionalMovement.get());
    }

    @Transactional
    @Override
    public MovementResponseDTO create(CreateMovementRequestDTO movement) throws Exception {
        Account account = getAccountByAccountNumber(movement.getAccountNumber());
        double currentBalance = getCurrentBalance(account);
        double movementValue = NumberUtils.roundToTwoDecimals(movement.getValue());
        double newBalance = calculateAndValidateNewBalance(account, currentBalance, movementValue, movement.getMovementType());

        if (newBalance < 0) {
            LOG.error(String.format("A withdrawal of .2%f to account %s has been attempted", movementValue, account.getAccountNumber()));
            throw new Exception("Balance not available");
        }

        Movement movementToSave = new Movement();
        movementToSave.setDate(LocalDateTime.now());
        movementToSave.setMovementType(movement.getMovementType());
        movementToSave.setValue(movementValue);
        movementToSave.setBalance(NumberUtils.roundToTwoDecimals(newBalance));
        movementToSave.setAccount(account);
        movementToSave.setUpdated(false);

        movementToSave = movementRepository.save(movementToSave);

        return new MovementResponseDTO(movementToSave);
    }

    @Transactional
    @Override
    public MovementResponseDTO update(UpdateMovementRequestDTO movement) {
        List<Movement> movementsToUpdate = new ArrayList<>();

        Movement movementToUpdate = movementRepository.findById(movement.getMovementId())
                .orElseThrow(() -> {
                    LOG.error(String.format("The movement with id %s does not exist", movement.getMovementId()));
                    return new Exceptions.RecordNotFoundException("Record not found");
                });

        Account account = movementToUpdate.getAccount();


        double balanceBeforeThisMovement = getCurrentBalanceBeforeMovement(movementToUpdate);

        movementToUpdate.setMovementType(movement.getMovementType());
        movementToUpdate.setValue(movement.getValue());
        movementToUpdate.setBalance(calculateNewBalance(balanceBeforeThisMovement, movementToUpdate.getValue(), movementToUpdate.getMovementType()));
        movementToUpdate.setUpdated(true);

        movementsToUpdate.add(movementToUpdate);

        balanceBeforeThisMovement = movementToUpdate.getBalance();

        List<Movement> subsequentMovements = movementRepository.
                findAllByAccountAndDateAfterOrderByDateAsc(account, movementToUpdate.getDate());

        for (Movement m : subsequentMovements) {
            balanceBeforeThisMovement = calculateNewBalance(balanceBeforeThisMovement, m.getValue(), m.getMovementType());
            m.setBalance(balanceBeforeThisMovement);
            movementsToUpdate.add(m);

            if (balanceBeforeThisMovement < 0) {
                LOG.error(String.format("The balance becomes negative when the movement with id %s is eliminated", movement.getMovementId()));
                throw new Exceptions.BalanceCalculationException("It is not possible to update the balance sheets");
            }
        }

        movementRepository.saveAll(movementsToUpdate);

        return new MovementResponseDTO(movementToUpdate);
    }

    @Transactional
    @Override
    public void delete(UUID movementId) throws Exception {
        List<Movement> movementsToUpdate = new ArrayList<>();

        Movement movementToDelete = movementRepository.findById(movementId)
                .orElseThrow(() -> {
                    LOG.error(String.format("The movement with id %s does not exist", movementId));
                    return new Exceptions.RecordNotFoundException("Record not found");
                });

        Account account = movementToDelete.getAccount();

        double balanceBeforeThisMovement = getCurrentBalanceBeforeMovement(movementToDelete);

        List<Movement> subsequentMovements = movementRepository.
                findAllByAccountAndDateAfterOrderByDateAsc(account, movementToDelete.getDate());

        for (Movement m : subsequentMovements) {
            balanceBeforeThisMovement = calculateNewBalance(balanceBeforeThisMovement, m.getValue(), m.getMovementType());
            m.setBalance(balanceBeforeThisMovement);
            movementsToUpdate.add(m);

            if (balanceBeforeThisMovement < 0) {
                LOG.error(String.format("The balance becomes negative when the movement with id %s is eliminated", movementId));
                throw new Exceptions.BalanceCalculationException("It is not possible to update the balance sheets");
            }
        }

        movementRepository.delete(movementToDelete);
        movementRepository.saveAll(movementsToUpdate);
    }

    private Account getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    LOG.error(String.format("The account with account number %s not exist", accountNumber));
                    return new Exceptions.AccountNotFoundException("Account not found");
                });
    }

    private double getCurrentBalance(Account account) {
        return movementRepository.findTopByAccountAccountNumberOrderByDateDesc(account.getAccountNumber())
                .map(Movement::getBalance)
                .orElse(account.getInitialBalance());
    }

    private double calculateAndValidateNewBalance(Account account, double currentBalance, double movementValue, MovementType movementType) {
        if (movementType == MovementType.WITHDRAWAL) validateWithdrawalLimit(account, movementValue);
        return calculateBalance(currentBalance, movementValue, movementType);
    }

    private double calculateNewBalance(double currentBalance, double movementValue, MovementType movementType) {
        return calculateBalance(currentBalance, movementValue, movementType);
    }

    private double calculateBalance(double currentBalance, double movementValue, MovementType movementType) {
        switch (movementType) {
            case WITHDRAWAL:
                return currentBalance - movementValue;
            case DEPOSIT:
                return currentBalance + movementValue;
            default:
                throw new IllegalArgumentException("Unsupported movement type: " + movementType);
        }
    }

    private void validateWithdrawalLimit(Account account, double withdrawalAmount) {
        Double todayTotal = movementRepository
                .findTotalValueByMovementTypeAndAccountNumberForToday(account.getAccountNumber(), MovementType.WITHDRAWAL);
        if (Objects.nonNull(todayTotal) && todayTotal + withdrawalAmount > DAILY_WITHDRAWAL_LIMIT) {
            LOG.error(String.format("Attempted over-withdrawal on account number %s", account.getAccountNumber()));
            throw new Exceptions.ExceedsDailyWithdrawalLimitException("Daily quota exceeded");
        }
    }

    private double getCurrentBalanceBeforeMovement(Movement movement) {
        return movementRepository.findTopByAccountAndDateBefore(movement.getAccount(), movement.getDate())
                .map(Movement::getBalance)
                .orElse(movement.getAccount().getInitialBalance());
    }
}
