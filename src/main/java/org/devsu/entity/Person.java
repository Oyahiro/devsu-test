package org.devsu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.devsu.enums.Gender;


import javax.persistence.*;
import javax.validation.constraints.*;

@Data
@Entity
@Table(name="person", uniqueConstraints = {
        @UniqueConstraint(name = "UK_PERSON_IDENTIFICATION_NUMBER", columnNames = {"identification_number"})
})
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Person extends AbstractEntity {

    @NotEmpty(message = "Cannot be empty")
    @Pattern(regexp="^[A-Za-z_ ]*$", message="This field only accepts letters")
    @Column(name="name", length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name="gender", length = 20)
    private Gender gender;

    @Min(value = 0, message = "The value cannot be less than 0")
    @Max(value = 150, message = "The value cannot be greater than 0")
    @Column(name = "age")
    private int age;

    @NotEmpty(message = "Cannot be empty")
    @Size(min = 10, max = 10, message = "This field can only have 10 characters")
    @Pattern(regexp="^[0-9]{10}$", message="This field only accepts numeric values")
    @Column(name="identification_number", length = 10)
    private String identificationNumber;

    @NotEmpty(message = "Cannot be empty")
    @Pattern(regexp="^[A-Za-z_ ]*$", message="This field only accepts letters")
    @Column(name = "address", length = 255)
    private String address;

    @Size(min = 10, max = 10, message = "This field can only have 10 characters")
    @Pattern(regexp="^[0-9]{10}$", message="This field only accepts numeric values")
    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

}
