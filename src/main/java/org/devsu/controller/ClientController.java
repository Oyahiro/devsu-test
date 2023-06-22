package org.devsu.controller;

import org.devsu.dto.ErrorResponseDTO;
import org.devsu.dto.requests.CreateClientRequestDTO;
import org.devsu.dto.requests.UpdateClientRequestDTO;
import org.devsu.dto.responses.ClientResponseDTO;
import org.devsu.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.devsu.common.Constants.URI_CLIENTS;

@RestController
@RequestMapping(value = {URI_CLIENTS})
public class ClientController {

    private final Logger LOG = LoggerFactory.getLogger(ClientController.class);

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("")
    public ResponseEntity<?> read(@RequestParam UUID clientId) {
        try {
            ClientResponseDTO clientResponse = clientService.read(clientId);
            return new ResponseEntity<>(clientResponse, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());

            ErrorResponseDTO errorResponse = new ErrorResponseDTO();
            errorResponse.setMessage(ex.getMessage());
            errorResponse.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("")
    public ResponseEntity<?> create(@Valid @RequestBody CreateClientRequestDTO client) {
        try {
            ClientResponseDTO clientResponse = clientService.create(client);
            return new ResponseEntity<>(clientResponse, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());

            ErrorResponseDTO errorResponse = new ErrorResponseDTO();
            errorResponse.setMessage(ex.getMessage());
            errorResponse.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("")
    public ResponseEntity<?> update(@Valid @RequestBody UpdateClientRequestDTO client) {
        try {
            ClientResponseDTO clientResponse = clientService.update(client);
            return new ResponseEntity<>(clientResponse, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());

            ErrorResponseDTO errorResponse = new ErrorResponseDTO();
            errorResponse.setMessage(ex.getMessage());
            errorResponse.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("")
    public ResponseEntity<?> delete(@RequestParam UUID clientId) {
        try {
            clientService.delete(clientId);
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
