package org.devsu.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.devsu.enums.MovementType;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "movement")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Movement extends AbstractEntity {

    @Column(name = "date")
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", length = 15)
    private MovementType movementType;

    @DecimalMin(value = "0.01", message = "The minimum value is 1 cent.")
    @Column(name = "value")
    private double value;

    @Min(value = 0, message = "The balance cannot be negative.")
    @Column(name = "balance")
    private double balance;

    @Column(name = "updated")
    private boolean updated = false;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

}
