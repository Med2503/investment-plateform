package org.bank.reportingservice.service;

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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CsvReportGenerator implements ReportFileGenerator {

    private static final String CONTENT_TYPE = "text/csv; charset=UTF-8";

    private final TradeRecordRepository tradeRecordRepository;

    @Override
    public GeneratedReport generate(ReportEntity report) {
        if (report.getReportType() != ReportType.TRADE_HISTORY) {
            throw new ReportProcessingException(
                    "CSV generator currently supports TRADE_HISTORY only"
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

            try (Writer writer = new OutputStreamWriter(
                    output,
                    StandardCharsets.UTF_8
            )) {
                writer.write(
                        "tradeId,accountId,symbol,quantity,price,tradeType,executedPrice,executedAt"
                );
                writer.write("\r\n");

                for (TradeRecordEntity trade : trades) {
                    writer.write(csv(trade.getTradeId().toString()));
                    writer.write(",");
                    writer.write(csv(trade.getAccountId().toString()));
                    writer.write(",");
                    writer.write(csv(trade.getSymbol()));
                    writer.write(",");
                    writer.write(csv(trade.getQuantity().toPlainString()));
                    writer.write(",");
                    writer.write(csv(trade.getPrice().toPlainString()));
                    writer.write(",");
                    writer.write(csv(trade.getTradeType().name()));
                    writer.write(",");
                    writer.write(csv(trade.getExecutedPrice().toPlainString()));
                    writer.write(",");
                    writer.write(csv(trade.getExecutedAt().toString()));
                    writer.write("\r\n");
                }
            }

            byte[] bytes = output.toByteArray();

            return new GeneratedReport(
                    new ByteArrayInputStream(bytes),
                    bytes.length,
                    CONTENT_TYPE,
                    "csv"
            );
        } catch (IOException exception) {
            throw new ReportProcessingException(
                    "Unable to generate CSV report",
                    exception
            );
        }
    }

    private String csv(String value) {
        String safeValue = value == null ? "" : value;

        if (!safeValue.isEmpty()
                && "=+-@".indexOf(safeValue.charAt(0)) >= 0) {
            safeValue = "'" + safeValue;
        }

        return "\"" + safeValue.replace("\"", "\"\"") + "\"";
    }
}