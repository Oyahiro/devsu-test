package org.devsu.repository;

import org.devsu.entity.Account;
import org.devsu.entity.Client;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends AbstractEntityRepository<Account> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findAllByClient(Client client);

}
