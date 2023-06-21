package org.devsu.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.devsu.enums.AccountType;
import org.devsu.enums.Status;

import javax.persistence.*;
import javax.validation.constraints.*;

@Data
@Entity
@Table(name="account")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Account extends AbstractEntity {

    @NotEmpty
    @Size(min = 10, max = 10)
    @Pattern(regexp="^[0-9]{10}$", message="This field only accepts numeric values")
    @Column(name="account_number", length = 10)
    private String accountNumber;


    @Enumerated(EnumType.STRING)
    @Column(name="account_type", length = 10)
    private AccountType accountType;

    @Min(0)
    @Column(name = "initial_balance")
    private double initialBalance;

    @Enumerated(EnumType.STRING)
    @Column(name="status", length = 10)
    private Status status;

}
