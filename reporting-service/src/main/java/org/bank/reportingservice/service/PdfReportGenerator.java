package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
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
public class PdfReportGenerator implements ReportFileGenerator {

    private static final String CONTENT_TYPE = "application/pdf";
    private static final float MARGIN = 45;
    private static final float LINE_HEIGHT = 16;

    private final TradeRecordRepository tradeRecordRepository;

    @Override
    public GeneratedReport generate(ReportEntity report) {
        if (report.getReportType() != ReportType.TRADE_HISTORY) {
            throw new ReportProcessingException(
                    "PDF generator currently supports TRADE_HISTORY only"
            );
        }

        List<TradeRecordEntity> trades =
                tradeRecordRepository
                        .findByUserIdAndExecutedAtGreaterThanEqualAndExecutedAtLessThanOrderByExecutedAtAsc(
                                report.getUserId(),
                                report.getPeriodStart(),
                                report.getPeriodEnd()
                        );

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            PDType1Font regularFont =
                    new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font boldFont =
                    new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            PDPage page = newPage(document);
            float y = page.getMediaBox().getHeight() - MARGIN;

            y = writeLine(
                    document,
                    page,
                    "TRADE HISTORY REPORT",
                    MARGIN,
                    y,
                    boldFont,
                    16
            );

            y -= LINE_HEIGHT;

            y = writeLine(
                    document,
                    page,
                    "Report ID: " + report.getId(),
                    MARGIN,
                    y,
                    regularFont,
                    9
            );

            y = writeLine(
                    document,
                    page,
                    "User: " + report.getUserId(),
                    MARGIN,
                    y,
                    regularFont,
                    9
            );

            y = writeLine(
                    document,
                    page,
                    "Period: " + report.getPeriodStart()
                            + " - " + report.getPeriodEnd(),
                    MARGIN,
                    y,
                    regularFont,
                    9
            );

            y -= LINE_HEIGHT;

            y = writeLine(
                    document,
                    page,
                    "Trade ID | Symbol | Quantity | Executed Price | Executed At",
                    MARGIN,
                    y,
                    boldFont,
                    8
            );

            y -= 4;

            for (TradeRecordEntity trade : trades) {
                if (y < MARGIN + LINE_HEIGHT) {
                    page = newPage(document);
                    y = page.getMediaBox().getHeight() - MARGIN;
                }

                String row = trade.getTradeId()
                        + " | " + trade.getSymbol()
                        + " | " + trade.getQuantity().toPlainString()
                        + " | " + trade.getExecutedPrice().toPlainString()
                        + " | " + trade.getExecutedAt();

                y = writeLine(
                        document,
                        page,
                        row,
                        MARGIN,
                        y,
                        regularFont,
                        7
                );
            }

            document.save(output);

            byte[] bytes = output.toByteArray();

            return new GeneratedReport(
                    new ByteArrayInputStream(bytes),
                    bytes.length,
                    CONTENT_TYPE,
                    "pdf"
            );
        } catch (IOException exception) {
            throw new ReportProcessingException(
                    "Unable to generate PDF report",
                    exception
            );
        }
    }

    private PDPage newPage(PDDocument document) {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        return page;
    }

    private float writeLine(
            PDDocument document,
            PDPage page,
            String value,
            float x,
            float y,
            PDType1Font font,
            float fontSize
    ) throws IOException {
        String safeValue = toPdfSafeText(value);

        try (PDPageContentStream stream =
                     new PDPageContentStream(
                             document,
                             page,
                             PDPageContentStream.AppendMode.APPEND,
                             true,
                             true
                     )) {
            stream.beginText();
            stream.setFont(font, fontSize);
            stream.newLineAtOffset(x, y);
            stream.showText(safeValue);
            stream.endText();
        }

        return y - LINE_HEIGHT;
    }

    private String toPdfSafeText(String value) {
        if (value == null) {
            return "";
        }

        String normalized = value
                .replace('\r', ' ')
                .replace('\n', ' ');

        StringBuilder result = new StringBuilder();

        for (char character : normalized.toCharArray()) {
            if (character >= 32 && character <= 126) {
                result.append(character);
            } else {
                result.append('?');
            }
        }

        return result.toString();
    }
}