package be.kdg.sa.deliveryservice.api.admin;

import be.kdg.sa.deliveryservice.api.admin.dtos.TimeSpan;
import be.kdg.sa.deliveryservice.application.PdfSummaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('admin')")
public class AdminController {

    private final PdfSummaryService pdfSummaryService;

    public AdminController(PdfSummaryService pdfSummaryService) {
        this.pdfSummaryService = pdfSummaryService;
    }

    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getPdfSummary(@RequestBody TimeSpan timeSpan) {
        log.info("getPdfSummary");

        byte[] pdfBytes = pdfSummaryService.generatePdfFromSummary(
                timeSpan.start(),
                timeSpan.end()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("summary", "courier_summary.pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

    }
}
