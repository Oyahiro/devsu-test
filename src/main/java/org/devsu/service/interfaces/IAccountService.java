package org.devsu.service.interfaces;

import org.devsu.dto.requests.CreateAccountRequestDTO;
import org.devsu.dto.requests.UpdateAccountRequestDTO;
import org.devsu.dto.responses.AccountResponseDTO;

public interface IAccountService {

    AccountResponseDTO read(String accountNumber) throws Exception;

    AccountResponseDTO create(CreateAccountRequestDTO account) throws Exception;

    AccountResponseDTO update(UpdateAccountRequestDTO client) throws Exception;

    void delete(String accountNumber) throws Exception;

}
