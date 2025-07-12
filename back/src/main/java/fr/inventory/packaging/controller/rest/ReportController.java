package fr.inventory.packaging.controller.rest;

import fr.inventory.packaging.exceptions.entity.report.UnableToCreateReport;
import fr.inventory.packaging.service.core.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/rest/report")
@CrossOrigin("${front.url}")
public class ReportController {

    /// //////////////
    /// Attributs ///
    /// //////////////

    private ReportService reportService;

    /// //////////////
    /// Endpoints ///
    /// //////////////

    @GetMapping("/generate/{idInventory}")
    public ResponseEntity<?> createReport(@PathVariable Long idInventory) {
        try {
            byte[] csvBytes = reportService.createMonthlyReport(idInventory);
            ByteArrayResource resource = new ByteArrayResource(csvBytes);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"inventaire.xlsx\"")
                    .contentType(MediaType.parseMediaType("text/xlsx"))
                    .contentLength(csvBytes.length)
                    .body(resource);
        } catch (UnableToCreateReport e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        }
    }

    @Autowired
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }
}
