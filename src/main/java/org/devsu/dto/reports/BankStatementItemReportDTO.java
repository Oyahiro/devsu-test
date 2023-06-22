package org.devsu.dto.reports;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.devsu.dto.responses.AccountResponseDTO;
import org.devsu.entity.Account;
import org.devsu.entity.Movement;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BankStatementItemReportDTO {

    private AccountResponseDTO account;
    private List<MovementDetailReportDTO> movements;

    public BankStatementItemReportDTO(Account account, List<Movement> movements) {
        this.account = new AccountResponseDTO(account);

        this.movements = new ArrayList<>();
        for (Movement movement : movements) {
            this.movements.add(new MovementDetailReportDTO(movement));
        }
    }

}
