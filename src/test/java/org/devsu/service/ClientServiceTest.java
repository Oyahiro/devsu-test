package org.devsu.service;

import org.devsu.common.Constants;
import org.devsu.common.Exceptions;
import org.devsu.dto.PrimaryUser;
import org.devsu.dto.requests.CreateClientRequestDTO;
import org.devsu.dto.requests.UpdateClientRequestDTO;
import org.devsu.entity.Client;
import org.devsu.entity.Person;
import org.devsu.enums.Gender;
import org.devsu.enums.Role;
import org.devsu.enums.Status;
import org.devsu.repository.ClientRepository;
import org.devsu.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    private static final String NON_EXISTING_CLIENT_MESSAGE = "Record not found";
    private static final String CLIENT_ALREADY_EXISTS_MESSAGE = "There is already a client registered with this identification number";

    @InjectMocks
    private ClientService clientService;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void init() {
    }

    @Test
    public void testCreate() throws Exception {
        CreateClientRequestDTO request = createRequest();
        Person person = createPerson(request);
        Client client = createClient(person, request.getIdentificationNumber());

        when(personRepository.findByIdentificationNumber(request.getIdentificationNumber())).thenReturn(Optional.empty());
        when(personRepository.saveAndFlush(any(Person.class))).thenReturn(person);
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        assertEquals(request.getName(), clientService.create(request).getName());
    }

    @Test
    public void testRead() throws Exception {
        setCurrentUserInContext();

        UUID clientId = UUID.randomUUID();
        Client client = new Client();
        client.setId(clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        assertEquals(clientId.toString(), clientService.read(clientId).getClientId());
    }

    @Test
    public void testUpdate() throws Exception {
        UpdateClientRequestDTO request = new UpdateClientRequestDTO();
        request.setClientId(UUID.randomUUID());
        request.setName("New Name");
        request.setIdentificationNumber("0941106445");

        Person person = new Person();
        person.setIdentificationNumber("0941106445");
        Client client = new Client();
        client.setId(request.getClientId());
        client.setPerson(person);

        when(clientRepository.findById(request.getClientId())).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        assertEquals(request.getName(), clientService.update(request).getName());
    }

    @Test
    public void testCreate_ClientAlreadyExists() throws Exception {
        CreateClientRequestDTO request = createRequest();
        Person existingPerson = createPerson(request);

        when(personRepository.findByIdentificationNumber(request.getIdentificationNumber())).thenReturn(Optional.of(existingPerson));

        Exception exception = assertThrows(Exception.class, () -> {
            clientService.create(request);
        });

        assertEquals(CLIENT_ALREADY_EXISTS_MESSAGE, exception.getMessage());
    }

    @Test
    public void testRead_NonExistingClient() {
        UUID clientId = UUID.randomUUID();

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            clientService.read(clientId);
        });

        assertEquals(NON_EXISTING_CLIENT_MESSAGE, exception.getMessage());
    }

    @Test
    public void testUpdate_NoPersonAssociatedWithClient() throws Exception {
        UpdateClientRequestDTO request = new UpdateClientRequestDTO();
        request.setClientId(UUID.randomUUID());
        request.setName("New Name");
        request.setIdentificationNumber("0941106445");

        Client client = new Client();
        client.setId(request.getClientId());

        when(clientRepository.findById(request.getClientId())).thenReturn(Optional.of(client));

        Exception exception = assertThrows(Exceptions.RecordNotFoundException.class, () -> {
            clientService.update(request);
        });

        assertEquals("The record to be modified was not found", exception.getMessage());
    }

    @Test
    public void testDelete_ExistingClient() throws Exception {
        UUID clientId = UUID.randomUUID();
        Client clientToDelete = new Client();
        clientToDelete.setId(clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(clientToDelete));
        clientService.delete(clientId);

        verify(clientRepository, times(1)).delete(clientToDelete);
    }

    @Test
    public void testDelete_NonExistingClient() {
        UUID clientId = UUID.randomUUID();

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            clientService.delete(clientId);
        });

        assertEquals(NON_EXISTING_CLIENT_MESSAGE, exception.getMessage());
    }

    private CreateClientRequestDTO createRequest() {
        CreateClientRequestDTO request = new CreateClientRequestDTO();
        request.setName("John Doe");
        request.setGender(Gender.MALE);
        request.setAge(24);
        request.setIdentificationNumber("0941106445");
        request.setAddress("P. Sherman Calle Wallaby 42 Sidney");
        request.setPhoneNumber("0963179218");
        return request;
    }

    private Person createPerson(CreateClientRequestDTO request) {
        Person person = new Person();
        person.setId(UUID.randomUUID());
        person.setName(request.getName());
        person.setGender(request.getGender());
        person.setAge(request.getAge());
        person.setIdentificationNumber(request.getIdentificationNumber());
        person.setAddress(request.getAddress());
        person.setPhoneNumber(request.getPhoneNumber());
        return person;
    }

    private Client createClient(Person person, String identificationNumber) {
        Client client = new Client();
        client.setId(UUID.randomUUID());
        client.setPerson(person);
        client.setPassword(identificationNumber);
        client.setStatus(Status.ACTIVE);
        return client;
    }

    private void setCurrentUserInContext() {
        Person person = new Person();
        person.setIdentificationNumber("0941106445");

        Client client = new Client();
        client.setPerson(person);
        client.setRoles(new HashSet<>(Collections.singletonList(Role.ROLE_ADMIN.toString())));

        Map<String, Object> sessionMap = new HashMap<>();
        sessionMap.put(Constants.CURRENT_USER, client);

        PrimaryUser primaryUser = PrimaryUser.build(client, sessionMap);

        when(authentication.getPrincipal()).thenReturn(primaryUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }
}
