package org.devsu.dto.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.devsu.enums.AccountType;
import org.devsu.enums.Status;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class UpdateAccountRequestDTO {

    @NotEmpty(message = "Cannot be empty")
    @Size(max = 10, message = "The account number cannot be longer than 10 characters.")
    @Pattern(regexp = "^[0-9]{0,10}$", message = "This field only accepts numeric values")
    private String accountNumber;

    @NotNull(message = "Value is required")
    private AccountType accountType;

    @NotNull(message = "Value is required")
    private Status status;

}
