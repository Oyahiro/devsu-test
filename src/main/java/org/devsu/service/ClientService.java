package org.devsu.service;

import org.devsu.dto.requests.CreateClientRequestDTO;
import org.devsu.dto.requests.UpdateClientRequestDTO;
import org.devsu.dto.responses.ClientResponseDTO;
import org.devsu.entity.Client;
import org.devsu.entity.Person;
import org.devsu.enums.Status;
import org.devsu.repository.ClientRepository;
import org.devsu.repository.PersonRepository;
import org.devsu.service.interfaces.IClientService;
import org.devsu.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClientService implements IClientService {

    private final Logger LOG = LoggerFactory.getLogger(ClientService.class);

    private final PersonRepository personRepository;
    private final ClientRepository clientRepository;

    public ClientService(PersonRepository personRepository, ClientRepository clientRepository) {
        this.personRepository = personRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientResponseDTO read(UUID clientId) throws Exception {
        Optional<Client> optionalClient = clientRepository.findById(clientId);
        if (optionalClient.isEmpty()) {
            LOG.error(String.format("The client with id %s does not exist", clientId));
            throw new Exception("Record not found");
        }

        return new ClientResponseDTO(optionalClient.get());
    }

    @Override
    public ClientResponseDTO create(CreateClientRequestDTO client) throws Exception {
        Assert.isTrue(ValidationUtils.validateDocument(client.getIdentificationNumber()), "Identification number is not valid");
        Optional<Person> optionalPerson = personRepository.findByIdentificationNumber(client.getIdentificationNumber());
        if (optionalPerson.isPresent()) {
            LOG.error("There is already a client registered with this identification number");
            throw new Exception("There is already a client registered with this identification number");
        }

        Person personToSave = new Person();
        personToSave.setName(client.getName());
        personToSave.setGender(client.getGender());
        personToSave.setAge(client.getAge());
        personToSave.setIdentificationNumber(client.getIdentificationNumber());
        personToSave.setAddress(client.getAddress());
        personToSave.setPhoneNumber(client.getPhoneNumber());

        personToSave = personRepository.saveAndFlush(personToSave);

        Client clientToSave = new Client();
        clientToSave.setPassword(client.getIdentificationNumber());
        clientToSave.setStatus(Status.ACTIVE);
        clientToSave.setPerson(personToSave);

        clientToSave = clientRepository.save(clientToSave);

        return new ClientResponseDTO(clientToSave);
    }

    @Override
    public ClientResponseDTO update(UpdateClientRequestDTO client) throws Exception {
        Assert.isTrue(ValidationUtils.validateDocument(client.getIdentificationNumber()), "Identification number is not valid");

        Optional<Client> optionalClient = clientRepository.findById(client.getClientId());
        if (optionalClient.isEmpty()) {
            LOG.error(String.format("The client with id %s does not exist", client.getClientId()));
            throw new Exception("The record to be modified was not found");
        }

        if (Objects.isNull(optionalClient.get().getPerson())) {
            LOG.error(String.format("There is no person associated with the clientId %s", optionalClient.get().getId()));
            throw new Exception("The record to be modified was not found");
        }

        Client clientToSave = optionalClient.get();
        Person personToSave = clientToSave.getPerson();

        personToSave.setName(client.getName());
        personToSave.setGender(client.getGender());
        personToSave.setAge(client.getAge());
        personToSave.setIdentificationNumber(client.getIdentificationNumber());
        personToSave.setAddress(client.getAddress());
        personToSave.setPhoneNumber(client.getPhoneNumber());

        clientToSave.setPassword(client.getPassword());
        clientToSave.setStatus(client.getStatus());
        clientToSave.setPerson(personToSave);

        clientToSave = clientRepository.save(clientToSave);

        return new ClientResponseDTO(clientToSave);
    }

    @Override
    public void delete(UUID clientId) throws Exception {
        Optional<Client> optionalClient = clientRepository.findById(clientId);
        if (optionalClient.isEmpty()) {
            LOG.error(String.format("The client with id %s does not exist", clientId));
            throw new Exception("The record to be modified was not found");
        }

        clientRepository.deleteById(clientId);
    }
}