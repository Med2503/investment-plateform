package org.bank.reportingservice.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.bank.reportingservice.entity.ReportEntity;
import org.bank.reportingservice.entity.TradeRecordEntity;
import org.bank.reportingservice.exception.ReportProcessingException;
import org.bank.reportingservice.model.ReportType;
import org.bank.reportingservice.repository.TradeRecordRepository;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JsonReportGenerator implements ReportFileGenerator {

    private static final String CONTENT_TYPE = "application/json";

    private final TradeRecordRepository tradeRecordRepository;
    private final ObjectMapper objectMapper;

    @Override
    public GeneratedReport generate(ReportEntity report) {
        if (report.getReportType() != ReportType.TRADE_HISTORY) {
            throw new ReportProcessingException(
                    "JSON generator currently supports TRADE_HISTORY only"
            );
        }

        List<TradeRecordEntity> trades =
                tradeRecordRepository
                        .findByUserIdAndExecutedAtGreaterThanEqualAndExecutedAtLessThanOrderByExecutedAtAsc(
                                report.getUserId(),
                                report.getPeriodStart(),
                                report.getPeriodEnd()
                        );

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            try (JsonGenerator generator =
                         objectMapper.getFactory().createGenerator(output)) {

                generator.writeStartObject();

                generator.writeStringField(
                        "reportId",
                        report.getId().toString()
                );
                generator.writeStringField(
                        "userId",
                        report.getUserId()
                );
                generator.writeStringField(
                        "reportType",
                        report.getReportType().name()
                );
                generator.writeStringField(
                        "periodStart",
                        report.getPeriodStart().toString()
                );
                generator.writeStringField(
                        "periodEnd",
                        report.getPeriodEnd().toString()
                );

                generator.writeArrayFieldStart("trades");

                for (TradeRecordEntity trade : trades) {
                    generator.writeStartObject();

                    generator.writeStringField(
                            "tradeId",
                            trade.getTradeId().toString()
                    );
                    generator.writeStringField(
                            "accountId",
                            trade.getAccountId().toString()
                    );
                    generator.writeStringField(
                            "symbol",
                            trade.getSymbol()
                    );
                    generator.writeNumberField(
                            "quantity",
                            trade.getQuantity()
                    );
                    generator.writeNumberField(
                            "price",
                            trade.getPrice()
                    );
                    generator.writeStringField(
                            "tradeType",
                            trade.getTradeType().name()
                    );
                    generator.writeNumberField(
                            "executedPrice",
                            trade.getExecutedPrice()
                    );
                    generator.writeStringField(
                            "executedAt",
                            trade.getExecutedAt().toString()
                    );

                    generator.writeEndObject();
                }

                generator.writeEndArray();
                generator.writeEndObject();
            }

            byte[] bytes = output.toByteArray();

            return new GeneratedReport(
                    new ByteArrayInputStream(bytes),
                    bytes.length,
                    CONTENT_TYPE,
                    "json"
            );
        } catch (IOException exception) {
            throw new ReportProcessingException(
                    "Unable to generate JSON report",
                    exception
            );
        }
    }
}