package org.devsu.dto.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BankStatementsReportDTO {

    private List<BankStatementItemReportDTO> bankStatements;

}
