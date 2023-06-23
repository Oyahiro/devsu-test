package org.devsu.service;

import lombok.SneakyThrows;
import org.devsu.common.Constants;
import org.devsu.dto.PrimaryUser;
import org.devsu.entity.Client;
import org.devsu.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserDetailService implements UserDetailsService {

    private final Logger LOG = LoggerFactory.getLogger(UserDetailService.class);

    private final ClientRepository clientRepository;

    public UserDetailService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Assert.notNull(username, "Username can't be null");

        Map<String, Object> properties = new HashMap<>();
        Client client = clientRepository.findByPersonIdentificationNumber(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Identification number %s not found", username)));
        properties.put(Constants.CURRENT_USER, client);
        properties.put(Constants.CURRENT_USERNAME, username);
        properties.put(Constants.ROLES, client.getRoles());
        return PrimaryUser.build(client, properties);
    }

}
