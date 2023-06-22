package org.devsu.dto.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.devsu.entity.Client;
import org.devsu.enums.Gender;
import org.devsu.enums.Status;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class ClientResponseDTO {

    private String clientId;
    private String name;
    private Gender gender;
    private int age;
    private String identificationNumber;
    private String address;
    private String phoneNumber;
    private String password;
    private Status status;

    public ClientResponseDTO(Client client) {
        this.clientId = client.getId().toString();
        this.password = client.getPassword();
        this.status = client.getStatus();

        if(Objects.nonNull(client.getPerson())) {
            this.name = client.getPerson().getName();
            this.gender = client.getPerson().getGender();
            this.age = client.getPerson().getAge();
            this.identificationNumber = client.getPerson().getIdentificationNumber();
            this.address = client.getPerson().getAddress();
            this.phoneNumber = client.getPerson().getPhoneNumber();
        }
    }

}
