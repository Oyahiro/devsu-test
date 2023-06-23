package org.devsu.controller;

import org.devsu.config.jwt.JwtProvider;
import org.devsu.dto.requests.LoginUserRequestDTO;
import org.devsu.dto.responses.LoginUserResponseDTO;
import org.devsu.entity.Client;
import org.devsu.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.devsu.common.Constants.URI_AUTH;

@RestController
@RequestMapping(value = {URI_AUTH})
@CrossOrigin
public class AuthController {

    private final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final ClientService clientService;

    public AuthController(AuthenticationManager authenticationManager, JwtProvider jwtProvider, ClientService clientService) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.clientService = clientService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponseDTO> login(@Valid @RequestBody LoginUserRequestDTO loginUserDTO, BindingResult bindingResult) throws Exception {
        if(bindingResult.hasErrors()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUserDTO.getUsername(), loginUserDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Client client = clientService.getByIdentificationNumber(userDetails.getUsername())
                .orElseThrow(() -> new Exception("User not found"));
        LOG.info(String.format("********** User %s logged **********", client.getUsername()));
        LoginUserResponseDTO jwtDTO = new LoginUserResponseDTO(jwt, userDetails.getUsername(), client.getPerson().getIdentificationNumber(), userDetails.getAuthorities());
        return new ResponseEntity<>(jwtDTO, HttpStatus.OK);
    }

}
