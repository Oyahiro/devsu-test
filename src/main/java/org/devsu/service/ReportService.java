package org.devsu.service;

import org.devsu.common.Exceptions;
import org.devsu.dto.reports.BankStatementItemReportDTO;
import org.devsu.dto.reports.BankStatementsReportDTO;
import org.devsu.entity.Account;
import org.devsu.entity.Client;
import org.devsu.entity.Movement;
import org.devsu.repository.AccountRepository;
import org.devsu.repository.ClientRepository;
import org.devsu.repository.MovementRepository;
import org.devsu.service.interfaces.IReportService;
import org.devsu.utils.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ReportService implements IReportService {

    private final Logger LOG = LoggerFactory.getLogger(ReportService.class);

    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final ClientRepository clientRepository;

    public ReportService(AccountRepository accountRepository, MovementRepository movementRepository, ClientRepository clientRepository) {
        this.accountRepository = accountRepository;
        this.movementRepository = movementRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public BankStatementsReportDTO getBankStatement(UUID clientId, Date startDate, Date endDate) {
        Client client = findClientById(clientId);
        SessionUtils.verifyPermissions(client);

        List<Account> accounts = accountRepository.findAllByClient(client);

        LocalDateTime startDateTime = toLocalDateTimeAtStartOfDay(startDate);
        LocalDateTime endDateTime = toLocalDateTimeAtEndOfDay(endDate);

        List<BankStatementItemReportDTO> bankStatements = new ArrayList<>();

        for (Account account : accounts) {
            List<Movement> movements = movementRepository.findByAccountAndDateBetweenOrderByDateAsc(account, startDateTime, endDateTime);
            BankStatementItemReportDTO bankStatement = new BankStatementItemReportDTO(account, movements);
            bankStatements.add(bankStatement);
        }

        return new BankStatementsReportDTO(bankStatements);
    }

    private Client findClientById(UUID clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> {
                    LOG.error(String.format("The client with id %s not exist", clientId));
                    return new Exceptions.RecordNotFoundException("Record not found");
                });
    }

    private LocalDateTime toLocalDateTimeAtStartOfDay(Date date) {
        Instant instant = date.toInstant();
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate().atStartOfDay();
    }

    private LocalDateTime toLocalDateTimeAtEndOfDay(Date date) {
        Instant instant = date.toInstant();
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate().atTime(23, 59, 59);
    }

}
