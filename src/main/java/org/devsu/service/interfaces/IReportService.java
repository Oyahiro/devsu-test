package org.devsu.service.interfaces;

import org.devsu.dto.reports.BankStatementsReportDTO;

import java.util.Date;
import java.util.UUID;

public interface IReportService {

    BankStatementsReportDTO getBankStatement(UUID clientId, Date startDate, Date endDate) throws Exception;

}
