package org.devsu.dto.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.devsu.entity.Account;
import org.devsu.enums.AccountType;
import org.devsu.enums.Status;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class AccountResponseDTO {

    private String accountNumber;
    private AccountType accountType;
    private double initialBalance;
    private Status status;
    private String client;

    public AccountResponseDTO(Account account) {
        this.accountNumber = account.getAccountNumber();
        this.accountType = account.getAccountType();
        this.initialBalance = account.getInitialBalance();
        this.status = account.getStatus();

        if (Objects.nonNull(account.getClient())) {
            this.client = account.getClient().getName();
        }
    }

}
