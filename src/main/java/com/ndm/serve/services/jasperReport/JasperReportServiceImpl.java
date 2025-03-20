package com.ndm.serve.services.jasperReport;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class JasperReportServiceImpl implements JasperReportService {

    @Override
    public byte[] generateEmployeeReport(String reportName, Map<String, Object> parameters, List<?> dataSource) throws JRException, IOException {
        // Tải file .jasper từ classpath (resources/reports/)
        InputStream reportStream = new ClassPathResource("reports/" + reportName + ".jasper").getInputStream();
        JRBeanCollectionDataSource jrDataSource = new JRBeanCollectionDataSource(dataSource);

        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, jrDataSource);

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    @Override
    public byte[] generateProjectReport(String reportName, List<Map<String, Object>> parametersList) throws JRException, IOException {
        // Tải file .jasper từ classpath
        InputStream reportStream = new ClassPathResource("reports/" + reportName + ".jasper").getInputStream();

        // Danh sách chứa các JasperPrint để merge nhiều báo cáo vào một file PDF
        List<JasperPrint> jasperPrints = new ArrayList<>();

        // Duyệt qua danh sách parameters cho từng project
        for (Map<String, Object> parameters : parametersList) {
            // Lấy dữ liệu nguồn cho bảng nhân viên
            JRBeanCollectionDataSource tableDataSource = (JRBeanCollectionDataSource) parameters.get("TABLE_DATA_SOURCE");

            // Điền dữ liệu vào báo cáo
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, tableDataSource);
            jasperPrints.add(jasperPrint);
        }

        // Xuất PDF chứa tất cả các project
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrints));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();

        return outputStream.toByteArray();
    }
}
