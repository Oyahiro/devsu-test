package org.devsu.service;

import org.devsu.dto.requests.CreateAccountRequestDTO;
import org.devsu.dto.requests.UpdateAccountRequestDTO;
import org.devsu.dto.responses.AccountResponseDTO;
import org.devsu.entity.Account;
import org.devsu.entity.Client;
import org.devsu.enums.Status;
import org.devsu.repository.AccountRepository;
import org.devsu.repository.ClientRepository;
import org.devsu.service.interfaces.IAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService implements IAccountService {

    private final Logger LOG = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    public AccountService(AccountRepository accountRepository, ClientRepository clientRepository) {
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public AccountResponseDTO read(String accountId) throws Exception {
        Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountId);
        if (optionalAccount.isEmpty()) {
            LOG.error(String.format("The account with id %s does not exist", accountId));
            throw new Exception("Record not found");
        }

        return new AccountResponseDTO(optionalAccount.get());
    }

    @Override
    public AccountResponseDTO create(CreateAccountRequestDTO account) throws Exception {
        Optional<Client> optionalClient = clientRepository.findById(account.getClientId());
        if (optionalClient.isEmpty()) {
            LOG.error(String.format("The client with id %s not exist", account.getClientId()));
            throw new Exception("The client with the specified id does not exist");
        }

        Optional<Account> optionalAccount = accountRepository.findByAccountNumber(account.getAccountNumber());
        if (optionalAccount.isPresent()) {
            LOG.error(String.format("An account with account number %s already exists", account.getAccountNumber()));
            throw new Exception(String.format("An account with account number %s already exists", account.getAccountNumber()));
        }

        Account accountToSave = new Account();
        accountToSave.setAccountNumber(account.getAccountNumber());
        accountToSave.setAccountType(account.getAccountType());
        accountToSave.setInitialBalance(account.getInitialBalance());
        accountToSave.setStatus(Status.ACTIVE);
        accountToSave.setClient(optionalClient.get());

        accountToSave = accountRepository.save(accountToSave);

        return new AccountResponseDTO(accountToSave);
    }

    @Override
    public AccountResponseDTO update(UpdateAccountRequestDTO account) throws Exception {
        Optional<Account> optionalAccount = accountRepository.findByAccountNumber(account.getAccountNumber());
        if (optionalAccount.isEmpty()) {
            LOG.error(String.format("The account with account number %s does not exist", account.getAccountNumber()));
            throw new Exception("The record to be modified was not found");
        }

        Account accountToSave = optionalAccount.get();
        accountToSave.setAccountType(account.getAccountType());
        accountToSave.setStatus(account.getStatus());

        accountToSave = accountRepository.save(accountToSave);

        return new AccountResponseDTO(accountToSave);
    }

    @Override
    public void delete(String accountNumber) throws Exception {
        Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountNumber);
        if (optionalAccount.isEmpty()) {
            LOG.error(String.format("The account with account number %s does not exist", optionalAccount));
            throw new Exception("The record to be modified was not found");
        }

        accountRepository.delete(optionalAccount.get());
    }
}