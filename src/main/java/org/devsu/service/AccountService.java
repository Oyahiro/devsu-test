package org.devsu.service;

import org.devsu.common.Exceptions;
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
import java.util.UUID;

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
        Account account = findAccount(accountId);
        return new AccountResponseDTO(account);
    }

    @Override
    public AccountResponseDTO create(CreateAccountRequestDTO account) throws Exception {
        Client client = findClient(account.getClientId());
        validateAccountNonExistence(account.getAccountNumber());

        Account newAccount = buildNewAccount(account, client);
        Account savedAccount = accountRepository.save(newAccount);

        return new AccountResponseDTO(savedAccount);
    }

    @Override
    public AccountResponseDTO update(UpdateAccountRequestDTO account) throws Exception {
        Account accountToUpdate = findAccount(account.getAccountNumber());
        updateAccountObject(accountToUpdate, account);

        Account updatedAccount = accountRepository.save(accountToUpdate);

        return new AccountResponseDTO(updatedAccount);
    }

    @Override
    public void delete(String accountNumber) throws Exception {
        Account account = findAccount(accountNumber);
        accountRepository.delete(account);
    }

    private Client findClient(UUID clientId) throws Exception {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> createRecordNotFoundException("client", clientId.toString()));
    }

    private Account findAccount(String accountNumber) throws Exception {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> createRecordNotFoundException("account", accountNumber));
    }

    private void validateAccountNonExistence(String accountNumber) throws Exception {
        Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountNumber);
        if (optionalAccount.isPresent()) {
            LOG.error(String.format("An account with account number %s already exists", accountNumber));
            throw new Exception(String.format("An account with account number %s already exists", accountNumber));
        }
    }

    private Account buildNewAccount(CreateAccountRequestDTO accountRequest, Client client) {
        Account newAccount = new Account();
        newAccount.setAccountNumber(accountRequest.getAccountNumber());
        newAccount.setAccountType(accountRequest.getAccountType());
        newAccount.setInitialBalance(accountRequest.getInitialBalance());
        newAccount.setStatus(Status.ACTIVE);
        newAccount.setClient(client);
        return newAccount;
    }

    private void updateAccountObject(Account accountToUpdate, UpdateAccountRequestDTO accountRequest) {
        accountToUpdate.setAccountType(accountRequest.getAccountType());
        accountToUpdate.setStatus(accountRequest.getStatus());
    }

    private Exception createRecordNotFoundException(String recordType, String id) {
        String errorMessage = String.format("The %s with id %s does not exist", recordType, id);
        LOG.error(errorMessage);
        return new Exceptions.RecordNotFoundException("Record not found");
    }
}