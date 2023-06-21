package org.devsu.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.devsu.enums.AccountType;
import org.devsu.enums.MovementType;
import org.devsu.enums.Status;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;

@Data
@Entity
@Table(name="movement")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Movement extends AbstractEntity {

    @Temporal(TemporalType.DATE)
    @Column(name = "date")
    private Date date;

    @Enumerated(EnumType.STRING)
    @Column(name="movement_type", length = 15)
    private MovementType movementType;

    @Min(0)
    @Column(name = "value")
    private double value;

    @Min(0)
    @Column(name = "balance")
    private double balance;

}
