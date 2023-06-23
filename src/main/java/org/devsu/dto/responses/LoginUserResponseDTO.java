package org.devsu.dto.responses;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
public class LoginUserResponseDTO {

    private String token;
    private String bearer = "Bearer";
    private String username;
    private String identificationNumber;
    private Collection<? extends GrantedAuthority> authorities;

    public LoginUserResponseDTO(String token, String username, String identificationNumber, Collection<? extends GrantedAuthority> authorities) {
        this.token = token;
        this.username = username;
        this.identificationNumber = identificationNumber;
        this.authorities = authorities;
    }

}
