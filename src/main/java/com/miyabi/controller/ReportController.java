package com.miyabi.controller;

import com.miyabi.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/boleta-test")
    public ResponseEntity<byte[]> getBoletaTest() {
        // Se le pasara datos "quemados" (hardcodeados) para probar que el diseño funcione
        byte[] pdfBytes = reportService.generateBoleta("Carlos Mendoza", "2026-02-25", "350.00");

        HttpHeaders headers = new HttpHeaders();
        // Le decimos al navegador que esto es un archivo PDF
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Descomentar la siguiente línea si se descargara automáticamente en lugar de abrirse en una pestaña nueva
        // headers.setContentDispositionFormData("attachment", "Boleta_Miyabi.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}