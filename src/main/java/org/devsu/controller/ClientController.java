package org.devsu.controller;

import org.devsu.dto.requests.SaveClientRequestDTO;
import org.devsu.dto.requests.UpdateClientRequestDTO;
import org.devsu.dto.responses.ClientResponseDTO;
import org.devsu.dto.ErrorResponseDTO;
import org.devsu.entity.Person;
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
    public ResponseEntity<?> get(@RequestParam UUID clientId) {
        try {
            ClientResponseDTO clientResponse = clientService.get(clientId);
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
    public ResponseEntity<?> save(@Valid @RequestBody SaveClientRequestDTO client) {
        try {
            ClientResponseDTO clientResponse = clientService.save(client);
            return new ResponseEntity<>(clientResponse, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());

            ErrorResponseDTO errorResponse = new ErrorResponseDTO();
            errorResponse.setMessage(ex.getMessage());
            errorResponse.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("")
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
