package org.devsu.controller;

import org.devsu.dto.ErrorResponseDTO;
import org.devsu.dto.requests.CreateMovementRequestDTO;
import org.devsu.dto.requests.UpdateMovementRequestDTO;
import org.devsu.dto.responses.MovementResponseDTO;
import org.devsu.service.MovementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.devsu.common.Constants.URI_MOVEMENTS;

@RestController
@RequestMapping(value = {URI_MOVEMENTS})
public class MovementController {

    private final Logger LOG = LoggerFactory.getLogger(MovementController.class);

    private final MovementService movementService;

    @Autowired
    public MovementController(MovementService movementService) {
        this.movementService = movementService;
    }

    @GetMapping("")
    public ResponseEntity<?> read(@RequestParam UUID movementId) {
        try {
            MovementResponseDTO movementResponse = movementService.read(movementId);
            return new ResponseEntity<>(movementResponse, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());

            ErrorResponseDTO errorResponse = new ErrorResponseDTO();
            errorResponse.setMessage(ex.getMessage());
            errorResponse.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("")
    public ResponseEntity<?> create(@Valid @RequestBody CreateMovementRequestDTO movement) {
        try {
            MovementResponseDTO movementResponse = movementService.create(movement);
            return new ResponseEntity<>(movementResponse, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());

            ErrorResponseDTO errorResponse = new ErrorResponseDTO();
            errorResponse.setMessage(ex.getMessage());
            errorResponse.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("")
    public ResponseEntity<?> update(@Valid @RequestBody UpdateMovementRequestDTO movement) {
        try {
            MovementResponseDTO movementResponse = movementService.update(movement);
            return new ResponseEntity<>(movementResponse, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());

            ErrorResponseDTO errorResponse = new ErrorResponseDTO();
            errorResponse.setMessage(ex.getMessage());
            errorResponse.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("")
    public ResponseEntity<?> delete(@RequestParam UUID movementId) {
        try {
            movementService.delete(movementId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());

            ErrorResponseDTO errorResponse = new ErrorResponseDTO();
            errorResponse.setMessage(ex.getMessage());
            errorResponse.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
