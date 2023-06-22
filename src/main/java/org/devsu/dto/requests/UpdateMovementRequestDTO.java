package org.devsu.dto.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.devsu.enums.MovementType;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UpdateMovementRequestDTO {

    @NotNull(message = "Value is required")
    private UUID movementId;

    @NotNull(message = "Value is required")
    private MovementType movementType;

    @DecimalMin(value = "0.01", message = "The minimum value is 1 cent.")
    private double value;

}
