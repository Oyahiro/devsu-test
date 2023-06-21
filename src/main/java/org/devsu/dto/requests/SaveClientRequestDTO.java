package org.devsu.dto.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.devsu.enums.Gender;
import org.devsu.enums.Status;

import javax.validation.constraints.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SaveClientRequestDTO {

    @NotEmpty
    @Pattern(regexp="^[A-Za-z_ ]*$", message="This field only accepts letters")
    private String name;

    @NotNull
    private Gender gender;

    @Min(value = 0, message = "The value cannot be less than 0")
    @Max(value = 150, message = "The value cannot be greater than 0")
    private int age;

    @NotEmpty
    @Size(min = 10, max = 10, message = "This field can only have 10 characters")
    @Pattern(regexp="^[0-9]{10}$", message="This field only accepts numeric values")
    private String identificationNumber;

    @NotEmpty
    @Pattern(regexp="^[A-Za-z_ ]*$", message="This field only accepts letters")
    private String address;

    @Size(min = 10, max = 10, message = "This field can only have 10 characters")
    @Pattern(regexp="^[0-9]{10}$", message="This field only accepts numeric values")
    private String phoneNumber;

}
