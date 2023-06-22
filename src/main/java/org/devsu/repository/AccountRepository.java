package org.devsu.repository;

import org.devsu.entity.Account;

import java.util.Optional;

public interface AccountRepository extends AbstractEntityRepository<Account> {

    Optional<Account> findByAccountNumber(String accountNumber);

}
