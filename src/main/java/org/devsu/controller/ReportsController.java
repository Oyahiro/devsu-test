package org.devsu.controller;

import org.devsu.dto.ErrorResponseDTO;
import org.devsu.dto.reports.BankStatementsReportDTO;
import org.devsu.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static org.devsu.common.Constants.URI_REPORTS;

@RestController
@RequestMapping(value = {URI_REPORTS})
public class ReportsController {

    private final Logger LOG = LoggerFactory.getLogger(ReportsController.class);

    private final ReportService reportService;

    @Autowired
    public ReportsController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("")
    public ResponseEntity<?> bankStatement(@RequestParam UUID clientId,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            BankStatementsReportDTO bankStatement = reportService.getBankStatement(clientId, startDate, endDate);
            return new ResponseEntity<>(bankStatement, HttpStatus.OK);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());

            ErrorResponseDTO errorResponse = new ErrorResponseDTO();
            errorResponse.setMessage(ex.getMessage());
            errorResponse.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

}
