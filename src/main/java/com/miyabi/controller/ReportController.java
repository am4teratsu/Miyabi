package com.miyabi.controller;

import com.miyabi.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST encargado de la generación y descarga de reportes y comprobantes.
 * Se encarga de recibir la petición del cliente y devolver el archivo PDF (la boleta) 
 * generado por JasperReports.
 */
@RestController // Indica que es una API REST. Las respuestas no son vistas HTML, sino datos puros (o archivos binarios en este caso).
@RequestMapping("/api/receipts") // Ruta base para los comprobantes de pago.
public class ReportController {

    // Dependencia del servicio que contiene toda la lógica pesada de JasperReports y consultas SQL.
    private final ReportService reportService;

    /**
     * Constructor para la Inyección de Dependencias.
     * (Es una mejor práctica usar el constructor en lugar de la anotación @Autowired
     * porque facilita las pruebas unitarias y asegura que la dependencia no sea nula).
     */
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Endpoint GET: /api/receipts/generate/{id}
     * Descarga la boleta de pago de una reserva específica en formato PDF.
     * * @param id El ID de la reserva, extraído de la URL.
     * @return Un ResponseEntity que contiene los bytes del PDF y las cabeceras HTTP configuradas para forzar la descarga.
     */
    @GetMapping("/generate/{id}")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Integer id) {
        try {
            // 1. Llama al método del ReportService que compila el .jrxml y lo llena con los datos de SQL Server.
            // Devuelve el PDF "crudo" en forma de un arreglo de bytes.
            byte[] pdfBytes = reportService.generateReceiptPDF(id);

            // 2. Configuración de las cabeceras HTTP (Headers)
            HttpHeaders headers = new HttpHeaders();
            
            // Le indica al navegador del usuario que el contenido que está recibiendo es un archivo PDF.
            headers.setContentType(MediaType.APPLICATION_PDF);
            
            // Le indica al navegador que debe descargar el archivo (attachment) en lugar de intentar abrirlo en una pestaña,
            // y le asigna un nombre por defecto al archivo descargado.
            headers.setContentDispositionFormData("attachment", "Receipt_Reservation_" + id + ".pdf");

            // 3. Retorna la respuesta HTTP 200 (OK) con las cabeceras configuradas y el PDF en el cuerpo (body).
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
                    
        } catch (Exception e) {
            // Si JasperReports falla o la consulta SQL da error, se imprime en la consola del servidor
            // y se le devuelve al cliente un error HTTP 500 (Internal Server Error).
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}