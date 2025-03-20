package com.ndm.serve.services.jasperReport;

import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface JasperReportService {
    //void generateReport() throws IOException, JRException;

    byte[] generateEmployeeReport(String reportName, Map<String, Object> parameters, List<?> dataSource) throws JRException, IOException;

    byte[] generateProjectReport(String reportName, List<Map<String, Object>> parameters) throws JRException, IOException;
}
