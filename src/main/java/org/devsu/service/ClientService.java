package org.devsu.service;

import org.devsu.common.Exceptions;
import org.devsu.dto.requests.CreateClientRequestDTO;
import org.devsu.dto.requests.UpdateClientRequestDTO;
import org.devsu.dto.responses.ClientResponseDTO;
import org.devsu.entity.Client;
import org.devsu.entity.Person;
import org.devsu.enums.Role;
import org.devsu.enums.Status;
import org.devsu.repository.ClientRepository;
import org.devsu.repository.PersonRepository;
import org.devsu.service.interfaces.IClientService;
import org.devsu.utils.SessionUtils;
import org.devsu.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

@Service
public class ClientService implements IClientService {

    private final Logger LOG = LoggerFactory.getLogger(ClientService.class);

    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;
    private final ClientRepository clientRepository;

    public ClientService(PasswordEncoder passwordEncoder, PersonRepository personRepository, ClientRepository clientRepository) {
        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientResponseDTO read(UUID clientId) {
        Client client = findClientById(clientId);
        SessionUtils.verifyPermissions(client);

        return new ClientResponseDTO(client);
    }

    @Transactional
    @Override
    public ClientResponseDTO create(CreateClientRequestDTO client) throws Exception {
        Assert.isTrue(ValidationUtils.validateDocument(client.getIdentificationNumber()), "Identification number is not valid");
        validateNonExistentPerson(client.getIdentificationNumber());

        Person savedPerson = buildAndSavePerson(client);
        Client savedClient = buildAndSaveClient(savedPerson, client.getIdentificationNumber());

        return new ClientResponseDTO(savedClient);
    }

    @Transactional
    @Override
    public ClientResponseDTO update(UpdateClientRequestDTO client) throws Exception {
        Assert.isTrue(ValidationUtils.validateDocument(client.getIdentificationNumber()), "Identification number is not valid");
        Client clientToUpdate = findClientById(client.getClientId());
        SessionUtils.verifyPermissions(clientToUpdate);

        updateClientAndPersonObjects(clientToUpdate, client);

        Client updatedClient = clientRepository.save(clientToUpdate);
        return new ClientResponseDTO(updatedClient);
    }

    @Transactional
    @Override
    public void delete(UUID clientId) throws Exception {
        Client client = findClientById(clientId);
        clientRepository.delete(client);
    }

    @Override
    public Optional<Client> getByIdentificationNumber(String identificationNumber) {
        Assert.notNull(identificationNumber, "Identification number can't be null");
        return clientRepository.findByPersonIdentificationNumber(identificationNumber);
    }

    @Override
    public boolean existsByIdentificationNumber(String identificationNumber) {
        Assert.notNull(identificationNumber, "Identification number can't be null");
        return clientRepository.existsByPersonIdentificationNumber(identificationNumber);
    }

    private Client findClientById(UUID clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> {
                    LOG.error(String.format("The client with id %s not exist", clientId));
                    return new Exceptions.RecordNotFoundException("Record not found");
                });
    }

    private void validateNonExistentPerson(String identificationNumber) throws Exception {
        Optional<Person> optionalPerson = personRepository.findByIdentificationNumber(identificationNumber);
        if (optionalPerson.isPresent()) {
            LOG.error(String.format("There is already a client registered with identification number %s", identificationNumber));
            throw new Exception("There is already a client registered with this identification number");
        }
    }

    private Person buildAndSavePerson(CreateClientRequestDTO clientRequest) {
        Person person = new Person();
        person.setName(clientRequest.getName());
        person.setGender(clientRequest.getGender());
        person.setAge(clientRequest.getAge());
        person.setIdentificationNumber(clientRequest.getIdentificationNumber());
        person.setAddress(clientRequest.getAddress());
        person.setPhoneNumber(clientRequest.getPhoneNumber());

        return personRepository.saveAndFlush(person);
    }

    private Client buildAndSaveClient(Person person, String password) {
        Client client = new Client();
        client.setPassword(passwordEncoder.encode(password));
        client.setStatus(Status.ACTIVE);
        client.setPerson(person);
        client.setRoles(new HashSet<>(Collections.singletonList(Role.ROLE_CLIENT.toString())));

        return clientRepository.save(client);
    }

    private void updateClientAndPersonObjects(Client clientToUpdate, UpdateClientRequestDTO clientRequest) throws Exception {
        Person person = clientToUpdate.getPerson();
        if (Objects.isNull(person)) {
            LOG.error(String.format("There is no person associated with the clientId %s", clientToUpdate.getId()));
            throw new Exceptions.RecordNotFoundException("The record to be modified was not found");
        }
        updatePersonDetails(person, clientRequest);
        updateClientDetails(clientToUpdate, clientRequest);
    }

    private void updatePersonDetails(Person personToUpdate, UpdateClientRequestDTO clientRequest) {
        personToUpdate.setName(clientRequest.getName());
        personToUpdate.setGender(clientRequest.getGender());
        personToUpdate.setAge(clientRequest.getAge());
        personToUpdate.setIdentificationNumber(clientRequest.getIdentificationNumber());
        personToUpdate.setAddress(clientRequest.getAddress());
        personToUpdate.setPhoneNumber(clientRequest.getPhoneNumber());
    }

    private void updateClientDetails(Client clientToUpdate, UpdateClientRequestDTO clientRequest) {
        clientToUpdate.setPassword(passwordEncoder.encode(clientRequest.getPassword()));
        clientToUpdate.setStatus(clientRequest.getStatus());
    }
}