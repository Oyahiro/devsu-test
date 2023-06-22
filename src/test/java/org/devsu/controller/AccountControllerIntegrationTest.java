package org.devsu.controller;

import org.devsu.dto.responses.AccountResponseDTO;
import org.devsu.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
public class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    public void whenGetAccount_thenReturnAccount() throws Exception {
        String accountNumber = "123456789";
        AccountResponseDTO accountResponse = new AccountResponseDTO();
        accountResponse.setAccountNumber(accountNumber);
        accountResponse.setInitialBalance(1000.00);

        given(accountService.read(accountNumber)).willReturn(accountResponse);

        mockMvc.perform(get("/api/accounts")
                        .param("accountNumber", accountNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(accountNumber))
                .andExpect(jsonPath("$.initialBalance").value(1000.00));
    }

}
