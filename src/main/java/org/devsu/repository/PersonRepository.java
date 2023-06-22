package org.devsu.repository;

import org.devsu.entity.Person;

import java.util.Optional;

public interface PersonRepository extends AbstractEntityRepository<Person> {

    Optional<Person> findByIdentificationNumber(String identificationNumber);

}
