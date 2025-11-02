package be.kdg.sa.deliveryservice.application;

import be.kdg.sa.deliveryservice.domain.courier.Courier;
import be.kdg.sa.deliveryservice.domain.courier.CourierRepository;
import be.kdg.sa.deliveryservice.domain.delivery.Delivery;
import be.kdg.sa.deliveryservice.domain.delivery.DeliveryRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class PdfSummaryService {

    // Repositories
    private final DeliveryRepository deliveryRepository;
    private final CourierRepository courierRepository;

    private Document document;

    public PdfSummaryService(DeliveryRepository deliveryRepository, CourierRepository courierRepository) {
        this.deliveryRepository = deliveryRepository;
        this.courierRepository = courierRepository;
    }

    // main method
    public byte[] generatePdfFromSummary(LocalDateTime start, LocalDateTime end) {
        log.info("Generating PDF from summary between {} and {}", start, end);
        final var couriersWithDeliveries = fetchCouriersWithDeliveries(start, end);
        final var outputStream = new ByteArrayOutputStream();

        try {
            this.document = new Document(PageSize.A4);
            PdfWriter.getInstance(this.document, outputStream);
            this.document.open();

            addPdfHeader(start, end, calculateTotalPayout(couriersWithDeliveries));
            addCourierSections(couriersWithDeliveries);

            this.document.close();
        } catch (DocumentException e) {
            log.error("Error generating PDF", e);
            throw new IllegalStateException("Error generating PDF", e);
        }

        return outputStream.toByteArray();
    }

    // data methods
    private Map<Courier, List<Delivery>> fetchCouriersWithDeliveries(LocalDateTime start, LocalDateTime end) {
        log.info("Finding all couriers with completed deliveries between {} and {}", start, end);

        final var completedDeliveries = deliveryRepository.findAllCompletedDeliveriesBetween(start, end);
        final var courierIds = completedDeliveries.stream()
                .map(Delivery::getCourierId)
                .collect(Collectors.toSet());
        final var couriers = courierRepository.findAllByIdIn(courierIds);

        return couriers.stream()
                .collect(Collectors.toMap(
                        courier -> courier,
                        courier -> completedDeliveries.stream()
                                .filter(delivery -> delivery.getCourierId().equals(courier.getId()))
                                .toList()
                ));
    }

    // PDF Document creation methods
    private void addPdfHeader(LocalDateTime start, LocalDateTime end, double totalPayout)
            throws DocumentException {
        log.info("Adding PDF header to summary");
        addDocumentTitle();
        addTimeRangeParagraph(start, end);
        addTotalPayoutParagraph(totalPayout);
        addSpacer();
    }

    private void addCourierSections(Map<Courier, List<Delivery>> couriersWithDeliveries)
            throws DocumentException {
        log.info("Adding courier sections to summary");
        for (var entry : couriersWithDeliveries.entrySet()) {
            addCourierSection(entry.getKey(), entry.getValue());
            addSectionSeparator();
        }
    }

    private void addDocumentTitle() throws DocumentException {
        log.info("Adding document title to summary");
        final var title = new Paragraph("Courier Delivery Summary", getTitleFont());
        title.setAlignment(Element.ALIGN_CENTER);
        this.document.add(title);
    }

    private void addTimeRangeParagraph(LocalDateTime start, LocalDateTime end)
            throws DocumentException {
        log.info("Adding time range paragraph to summary");
        final var dateRange = new Paragraph();
        dateRange.add(new Chunk("Period: ", getBoldFont()));
        dateRange.add(new Chunk(formatDateTime(start) + " - " + formatDateTime(end), getRegularFont()));
        dateRange.setAlignment(Element.ALIGN_CENTER);
        this.document.add(dateRange);
        addSpacer();
    }

    private void addTotalPayoutParagraph(double totalPayout) throws DocumentException {
        log.info("Adding total payout paragraph to summary");
        final var total = new Paragraph();
        total.add(new Chunk("Total Payout: ", getBoldFont()));
        total.add(new Chunk(formatCurrency(totalPayout), getRegularFont()));
        this.document.add(total);
    }

    private void addCourierSection(Courier courier, List<Delivery> deliveries)
            throws DocumentException {
        log.info("Adding courier section to summary for courier with id {}", courier.getId());
        final var courierHeader = new Paragraph(courier.getName(), getHeadingFont());
        this.document.add(courierHeader);

        addLabeledValue("ID", courier.getId().toString());
        addLabeledValue("Total Payout", formatCurrency(calculateCourierPayout(deliveries)));

        this.document.add(new Paragraph("Completed Deliveries:", getBoldFont()));
        addSpacer();
        addDeliveryTable(deliveries);
    }

    private void addDeliveryTable(List<Delivery> deliveries) throws DocumentException {
        log.info("Adding delivery table to summary for courier with id {}", deliveries.getFirst().getCourierId());
        final var table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 3f, 2f});

        addTableHeader(table, "Start Time", "End Time", "Payout");

        deliveries.forEach(delivery -> {
            table.addCell(createCell(formatDateTime(delivery.getStartTime()), getRegularFont()));
            table.addCell(createCell(formatDateTime(delivery.getEndTime()), getRegularFont()));
            table.addCell(createCell(formatCurrency(delivery.getPayout()), getRegularFont()));
        });

        this.document.add(table);
    }

    // Helper methods
    private void addLabeledValue(String label, String value) throws DocumentException {
        log.info("Adding labeled value to summary for label {} and value {}", label, value);
        final var paragraph = new Paragraph();
        paragraph.add(new Chunk(label + ": ", getBoldFont()));
        paragraph.add(new Chunk(value, getRegularFont()));
        this.document.add(paragraph);
    }

    private void addSpacer() throws DocumentException {
        this.document.add(new Paragraph(" "));
    }

    private void addSectionSeparator() throws DocumentException {
        log.info("Adding section separator to summary");
        addSpacer();
        document.add(new LineSeparator());
        addSpacer();
    }

    private void addTableHeader(PdfPTable table, String... headerTexts) {
        log.info("Adding table header to summary for header texts");
        for (var headerText : headerTexts) {
            table.addCell(createHeaderCell(headerText));
        }
    }

    private PdfPCell createHeaderCell(String text) {
        final var cell = createCell(text, getBoldFont());
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        return cell;
    }

    private PdfPCell createCell(String text, Font font) {
        final var cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        return cell;
    }

    // Font methods
    private Font getTitleFont() {
        return new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    }

    private Font getHeadingFont() {
        return new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    }

    private Font getBoldFont() {
        return new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    }

    private Font getRegularFont() {
        return new Font(Font.FontFamily.HELVETICA, 12);
    }

    // Calculation and formatting methods

    private double calculateTotalPayout(Map<Courier, List<Delivery>> couriersWithDeliveries) {
        log.info("Calculating total payout for couriers with deliveries {}", couriersWithDeliveries);
        return couriersWithDeliveries.values().stream()
                .flatMap(List::stream)
                .mapToDouble(Delivery::getPayout)
                .sum();
    }

    private double calculateCourierPayout(List<Delivery> deliveries) {
        log.info("Calculating payout for courier with deliveries {}", deliveries);
        return deliveries.stream()
                .mapToDouble(Delivery::getPayout)
                .sum();
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String formatCurrency(double amount) {
        return String.format("€%.2f", amount);
    }
}