package org.devsu.dto.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.devsu.enums.MovementType;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
public class CreateMovementRequestDTO {

    @NotEmpty(message = "Cannot be empty")
    @Size(max = 10, message = "The account number cannot be longer than 10 characters.")
    @Pattern(regexp = "^[0-9]{0,10}$", message = "This field only accepts numeric values")
    private String accountNumber;

    @NotNull(message = "Value is required")
    private MovementType movementType;

    @DecimalMin(value = "0.01", message = "The minimum value is 1 cent.")
    private double value;

}
