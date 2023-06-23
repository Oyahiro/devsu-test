package org.devsu.repository;

import org.devsu.entity.Client;

import java.util.Optional;

public interface ClientRepository extends AbstractEntityRepository<Client> {

    Optional<Client> findByPersonIdentificationNumber(String identificationNumber);

    boolean existsByPersonIdentificationNumber(String identificationNumber);

}
