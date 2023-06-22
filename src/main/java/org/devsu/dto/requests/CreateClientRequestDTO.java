package org.devsu.dto.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.devsu.enums.Gender;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
public class CreateClientRequestDTO {

    @NotEmpty(message = "Cannot be empty")
    @Pattern(regexp="^[A-Za-z_ ]*$", message="This field only accepts letters")
    private String name;

    @NotNull(message = "Value is required")
    private Gender gender;

    @Min(value = 0, message = "The value cannot be less than 0")
    @Max(value = 150, message = "The value cannot be greater than 0")
    private int age;

    @NotEmpty(message = "Cannot be empty")
    @Size(min = 10, max = 10, message = "This field can only have 10 characters")
    @Pattern(regexp="^[0-9]{10}$", message="This field only accepts numeric values")
    private String identificationNumber;

    @NotEmpty(message = "Cannot be empty")
    @Pattern(regexp="^[A-Za-z_ ]*$", message="This field only accepts letters")
    private String address;

    @Size(min = 10, max = 10, message = "This field can only have 10 characters")
    @Pattern(regexp="^[0-9]{10}$", message="This field only accepts numeric values")
    private String phoneNumber;

}
