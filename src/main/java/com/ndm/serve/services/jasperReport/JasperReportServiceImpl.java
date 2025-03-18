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
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
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
}
