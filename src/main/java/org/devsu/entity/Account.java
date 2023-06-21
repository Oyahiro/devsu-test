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
@Table(name="account", uniqueConstraints = {
        @UniqueConstraint(name = "UK_ACCOUNT_NUMBER", columnNames = {"account_number"})
})
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Account extends AbstractEntity {

    @NotEmpty(message = "Cannot be empty")
    @Size(max = 10, message = "The account number cannot be longer than 10 characters.")
    @Pattern(regexp="^[0-9]{0,10}$", message="This field only accepts numeric values")
    @Column(name="account_number", length = 10)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name="account_type", length = 10)
    private AccountType accountType;

    @Min(value = 0, message = "The balance cannot be negative.")
    @Column(name = "initial_balance")
    private double initialBalance;

    @Enumerated(EnumType.STRING)
    @Column(name="status", length = 10)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

}
