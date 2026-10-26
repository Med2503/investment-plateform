package org.bank.reportingservice.service;

import org.bank.reportingservice.entity.ReportEntity;

public interface ReportFileGenerator {

    GeneratedReport generate(ReportEntity report);
}
