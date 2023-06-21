package org.devsu.controller;

import org.devsu.dto.ErrorResponseDTO;
import org.devsu.dto.requests.CreateAccountRequestDTO;
import org.devsu.dto.requests.UpdateAccountRequestDTO;
import org.devsu.dto.responses.AccountResponseDTO;
import org.devsu.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

import static org.devsu.common.Constants.URI_ACCOUNTS;

@RestController
@RequestMapping(value = {URI_ACCOUNTS})
public class AccountController {

    private final Logger LOG = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("")
    public ResponseEntity<?> read(@RequestParam String accountNumber) {
        try {
            AccountResponseDTO accountResponse = accountService.read(accountNumber);
            return new ResponseEntity<>(accountResponse, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());

            ErrorResponseDTO errorResponse = new ErrorResponseDTO();
            errorResponse.setMessage(ex.getMessage());
            errorResponse.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("")
    public ResponseEntity<?> create(@Valid @RequestBody CreateAccountRequestDTO account) {
        try {
            AccountResponseDTO accountResponse = accountService.create(account);
            return new ResponseEntity<>(accountResponse, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());

            ErrorResponseDTO errorResponse = new ErrorResponseDTO();
            errorResponse.setMessage(ex.getMessage());
            errorResponse.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("")
    public ResponseEntity<?> update(@Valid @RequestBody UpdateAccountRequestDTO account) {
        try {
            AccountResponseDTO accountResponse = accountService.update(account);
            return new ResponseEntity<>(accountResponse, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());

            ErrorResponseDTO errorResponse = new ErrorResponseDTO();
            errorResponse.setMessage(ex.getMessage());
            errorResponse.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("")
    public ResponseEntity<?> delete(@RequestParam String accountNumber) {
        try {
            accountService.delete(accountNumber);
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
