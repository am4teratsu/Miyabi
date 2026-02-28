package com.miyabi.controller;

import com.miyabi.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/receipts")
public class ReportController {

    private final ReportService reportService;

    // Inyección de dependencias por constructor (mejor práctica que @Autowired)
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/generate/{id}")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Integer id) {
        try {
            // Llama al método que creamos en el ReportService
            byte[] pdfBytes = reportService.generateReceiptPDF(id);

            HttpHeaders headers = new HttpHeaders();
            // Esto le dice al navegador que es un PDF
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Fuerza la descarga con el nombre en inglés
            headers.setContentDispositionFormData("attachment", "Receipt_Reservation_" + id + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}