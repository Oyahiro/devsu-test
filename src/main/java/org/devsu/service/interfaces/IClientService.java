package org.devsu.service.interfaces;

import org.devsu.dto.requests.SaveClientRequestDTO;
import org.devsu.dto.requests.UpdateClientRequestDTO;
import org.devsu.dto.responses.ClientResponseDTO;

import java.util.UUID;

public interface IClientService {

    ClientResponseDTO get(UUID clientId) throws Exception;
    ClientResponseDTO save(SaveClientRequestDTO client) throws Exception;
    ClientResponseDTO update(UpdateClientRequestDTO client) throws Exception;
    void delete(UUID clientId) throws Exception;

}
