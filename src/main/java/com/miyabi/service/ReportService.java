package com.miyabi.service;

import com.miyabi.dto.ReceiptDetailDTO;
import com.miyabi.models.Reservation;
import com.miyabi.repository.ConsumptionRepository;
import com.miyabi.repository.ReservationRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio de Generación de Reportes.
 * Utiliza la librería JasperReports para transformar los datos de la base de datos
 * en un documento PDF legal (Boleta de Venta) para el huésped.
 */
@Service
public class ReportService {

    private final ReservationRepository reservationRepository;
    private final ConsumptionRepository consumptionRepository;

    // Inyección de dependencias de los repositorios necesarios para recolectar toda la información.
    public ReportService(ReservationRepository reservationRepository, ConsumptionRepository consumptionRepository) {
        this.reservationRepository = reservationRepository;
        this.consumptionRepository = consumptionRepository;
    }

    /**
     * Motor de generación de Boleta en PDF.
     * @param reservationId ID de la reserva a facturar.
     * @return Arreglo de bytes (byte[]) que representa el archivo PDF generado.
     */
    public byte[] generateReceiptPDF(Integer reservationId) throws Exception {
        
        // 1. OBTENCIÓN DE DATOS: Usamos el JOIN FETCH del repositorio para traer todo en una sola consulta.
        Reservation reservation = reservationRepository.getReceiptMainData(reservationId);
        
        if (reservation == null) {
            throw new RuntimeException("Reservation not found with ID: " + reservationId);
        }

        // Obtener los detalles de consumos extras mediante la consulta nativa SQL.
        List<Object[]> consumptions = consumptionRepository.getReceiptDetails(reservationId);

        // 2. PREPARACIÓN DE CABECERA: Formateamos nombres y fechas.
        String customerName = reservation.getGuest().getNames() + " " + reservation.getGuest().getSurnames();
        String issueDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); 
        String totalPay = reservation.getTotalPay() != null ? reservation.getTotalPay().toString() : "0.00";

        // 3. CONSTRUCCIÓN DEL DETALLE (DataSource):
        // Usamos una lista de DTOs para que JasperReports pueda leer los datos fácilmente.
        List<ReceiptDetailDTO> details = new ArrayList<>();
        
        // Agregamos la primera fila: El costo de la estadía (noches de habitación).
        String roomName = reservation.getRoom().getRoomType().getNameType();
        details.add(new ReceiptDetailDTO(
            String.valueOf(reservation.getNumberNights()),
            "Estadía: " + roomName,                       
            reservation.getPricePerNight().toString(),     
            reservation.getRoomSubtotal().toString()       
        ));

        // Agregamos el resto de filas: Consumos de servicios extras.
        for (Object[] row : consumptions) {
            details.add(new ReceiptDetailDTO(
                row[0].toString(), // Cantidad
                row[1].toString(), // Nombre del servicio
                row[2].toString(), // Precio Unitario
                row[3].toString()  // Subtotal
            ));
        }

        // 4. CONFIGURACIÓN DE JASPERREPORTS:
        // Parámetros globales para el diseño (Logo, cliente, totales).
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nombreCliente", customerName);
        parameters.put("fechaEmision", issueDate);
        parameters.put("totalPagar", totalPay);

        // Cargar recursos gráficos (Logo) y el diseño del reporte (.jrxml).
        InputStream logoStream = getClass().getResourceAsStream("/logo.png");
        if (logoStream != null) {
            parameters.put("logoEmpresa", logoStream);
        }

        InputStream reportStream = getClass().getResourceAsStream("/boleta_hotel.jrxml");
        if (reportStream == null) {
            throw new RuntimeException("¡ERROR! No se encontró el diseño en src/main/resources/boleta_hotel.jrxml");
        }

        // 5. COMPILACIÓN Y LLENADO: Transformamos el diseño y los datos en un documento.
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(details);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // 6. EXPORTACIÓN: Convertimos el resultado a formato PDF.
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}