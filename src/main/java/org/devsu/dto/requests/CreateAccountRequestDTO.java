package org.devsu.dto.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.devsu.enums.AccountType;
import org.devsu.enums.Gender;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CreateAccountRequestDTO {

    @NotEmpty(message = "Cannot be empty")
    @Size(max = 10, message = "The account number cannot be longer than 10 characters.")
    @Pattern(regexp="^[0-9]{1,10}$", message="This field only accepts numeric values")
    private String accountNumber;

    @NotNull(message = "Value is required")
    private AccountType accountType;

    @Min(value = 0, message = "The balance cannot be negative.")
    private double initialBalance;

    @NotNull(message = "Value is required")
    private UUID clientId;

}
