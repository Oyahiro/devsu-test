package org.devsu.dto.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.devsu.entity.Movement;
import org.devsu.utils.NumberUtils;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class MovementDetailReportDTO {

    private LocalDateTime date;
    private double initialBalance;
    private double movementValue;
    private double balance;

    public MovementDetailReportDTO(Movement movement) {
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
    }

}
