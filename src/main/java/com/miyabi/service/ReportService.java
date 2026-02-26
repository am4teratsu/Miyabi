package com.miyabi.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    public byte[] generateBoleta(String nombreCliente, String fecha, String total) {
        try {
            // 1. Ubicar el diseño
            File file = ResourceUtils.getFile("classpath:boleta_hotel.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

            // 2. Cargar el Logo dinámicamente desde resources
            InputStream logoStream = getClass().getResourceAsStream("/logo.png");

            // 3. Llenar los parámetros
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("nombreCliente", nombreCliente);
            parameters.put("fechaEmision", fecha);
            parameters.put("totalPagar", total);
            parameters.put("logoEmpresa", logoStream); // Pasamos el logo aquí

            // 4. SIMULAR LA LISTA DE CONSUMOS DEL HOTEL
            List<Map<String, Object>> listaConsumos = new ArrayList<>();
            
            Map<String, Object> item1 = new HashMap<>();
            item1.put("cantidad", "1");
            item1.put("descripcion", "Habitación Matrimonial (2 Noches)");
            item1.put("precio", "150.00");
            item1.put("subtotal", "300.00");
            listaConsumos.add(item1);

            Map<String, Object> item2 = new HashMap<>();
            item2.put("cantidad", "2");
            item2.put("descripcion", "Pisco Sour (Room Service)");
            item2.put("precio", "25.00");
            item2.put("subtotal", "50.00");
            listaConsumos.add(item2);

            // 5. Generar PDF (Le pasamos la lista en vez del DataSource vacío)
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listaConsumos);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}