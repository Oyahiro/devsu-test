package org.devsu.dto.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginUserRequestDTO {

    @NotBlank private String username;
    @NotBlank private String password;

}
