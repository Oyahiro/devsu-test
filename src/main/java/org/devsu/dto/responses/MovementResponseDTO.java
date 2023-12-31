package org.devsu.dto.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.devsu.entity.Movement;
import org.devsu.enums.AccountType;
import org.devsu.enums.Status;
import org.devsu.utils.NumberUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class MovementResponseDTO {

    private LocalDateTime date;
    private String client;
    private String accountNumber;
    private AccountType accountType;
    private double initialBalance;
    private Status status;
    private double movementValue;
    private double balance;

    public MovementResponseDTO(Movement movement) {
        this.date = movement.getDate();
        this.balance = movement.getBalance();

        switch (movement.getMovementType()) {
            case WITHDRAWAL:
                this.movementValue = movement.getValue() * (-1);
                this.initialBalance = NumberUtils.roundToTwoDecimals(this.balance + movement.getValue());
                break;
            case DEPOSIT:
                this.movementValue = movement.getValue();
                this.initialBalance = NumberUtils.roundToTwoDecimals(this.balance - movement.getValue());
        }

        if (Objects.nonNull(movement.getAccount())) {
            this.accountNumber = movement.getAccount().getAccountNumber();
            this.accountType = movement.getAccount().getAccountType();
            this.status = movement.getAccount().getStatus();

            if (Objects.nonNull(movement.getAccount().getClient())) {
                this.client = movement.getAccount().getClient().getName();
            }
        }
    }

}
