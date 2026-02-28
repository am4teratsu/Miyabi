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

@Service
public class ReportService {

    private final ReservationRepository reservationRepository;
    private final ConsumptionRepository consumptionRepository;

    public ReportService(ReservationRepository reservationRepository, ConsumptionRepository consumptionRepository) {
        this.reservationRepository = reservationRepository;
        this.consumptionRepository = consumptionRepository;
    }

    public byte[] generateReceiptPDF(Integer reservationId) throws Exception {
        
        Reservation reservation = reservationRepository.getReceiptMainData(reservationId);
        
        if (reservation == null) {
            throw new RuntimeException("Reservation not found with ID: " + reservationId);
        }

        List<Object[]> consumptions = consumptionRepository.getReceiptDetails(reservationId);

        String customerName = reservation.getGuest().getNames() + " " + reservation.getGuest().getSurnames();
        String issueDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); 
        String totalPay = reservation.getTotalPay() != null ? reservation.getTotalPay().toString() : "0.00";

        List<ReceiptDetailDTO> details = new ArrayList<>();
        
        String roomName = reservation.getRoom().getRoomType().getNameType();
        
        details.add(new ReceiptDetailDTO(
            String.valueOf(reservation.getNumberNights()),
            "Room stay: " + roomName,                      
            reservation.getPricePerNight().toString(),     
            reservation.getRoomSubtotal().toString()       
        ));

        for (Object[] row : consumptions) {
            details.add(new ReceiptDetailDTO(
                row[0].toString(),
                row[1].toString(),
                row[2].toString(),
                row[3].toString()
            ));
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nombreCliente", customerName);
        parameters.put("fechaEmision", issueDate);
        parameters.put("totalPagar", totalPay);

        InputStream logoStream = getClass().getResourceAsStream("/logo.png");
        if (logoStream != null) {
            parameters.put("logoEmpresa", logoStream);
        }

        InputStream reportStream = getClass().getResourceAsStream("/boleta_hotel.jrxml");

        if (reportStream == null) {
            throw new RuntimeException("¡ERROR! No se encontró el diseño en src/main/resources/boleta_hotel.jrxml");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
        
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(details);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}