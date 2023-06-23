package org.devsu.service.interfaces;

import org.devsu.dto.requests.CreateClientRequestDTO;
import org.devsu.dto.requests.UpdateClientRequestDTO;
import org.devsu.dto.responses.ClientResponseDTO;
import org.devsu.entity.Client;

import java.util.Optional;
import java.util.UUID;

public interface IClientService {

    ClientResponseDTO read(UUID clientId) throws Exception;

    ClientResponseDTO create(CreateClientRequestDTO client) throws Exception;

    ClientResponseDTO update(UpdateClientRequestDTO client) throws Exception;

    void delete(UUID clientId) throws Exception;

    Optional<Client> getByIdentificationNumber(String identificationNumber);

    boolean existsByIdentificationNumber(String identificationNumber);

}
