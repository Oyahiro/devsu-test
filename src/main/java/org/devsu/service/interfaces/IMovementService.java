package org.devsu.service.interfaces;

import org.devsu.dto.requests.CreateMovementRequestDTO;
import org.devsu.dto.requests.UpdateMovementRequestDTO;
import org.devsu.dto.responses.MovementResponseDTO;

import java.util.UUID;

public interface IMovementService {

    MovementResponseDTO read(UUID movementId) throws Exception;

    MovementResponseDTO create(CreateMovementRequestDTO movement) throws Exception;

    MovementResponseDTO update(UpdateMovementRequestDTO movement) throws Exception;

    void delete(UUID movementId) throws Exception;

}
