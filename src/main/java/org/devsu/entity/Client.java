package org.devsu.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.devsu.enums.Status;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.UUID;

@Data
@Entity
@Table(name="client")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Client extends AbstractEntity {

    @NotEmpty
    @Column(name = "password", length = 50)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name="status", length = 10)
    private Status status;

    @OneToOne
    @JoinColumn(name="person_id", referencedColumnName="id")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    private Person person;

}