package com.ndm.serve.controllers;

import com.ndm.serve.dtos.employee.EmployeeDTO;
import com.ndm.serve.dtos.employee.EmployeeReportDTO;
import com.ndm.serve.dtos.project.ProjectDTO;
import com.ndm.serve.dtos.project.ProjectReportDTO;
import com.ndm.serve.dtos.role.RoleDTO;
import com.ndm.serve.exceptions.ResourceNotFoundException;
import com.ndm.serve.services.employee.EmployeeService;
import com.ndm.serve.services.jasperReport.JasperReportService;
import com.ndm.serve.services.project.ProjectService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/report")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class JasperReportController {
    JasperReportService jasperReportService;
    EmployeeService employeeService;
    ProjectService projectService;

    @GetMapping("/employee")
    public ResponseEntity<byte[]> generateEmployeeReport() throws JRException, IOException {
        String reportName = "employee";
        // Create parameters (table data)
        List<EmployeeReportDTO> employeeReportDTOList = new ArrayList<>();
        for (EmployeeDTO employee : employeeService.getAll()) {
            Set<String> roleString = new HashSet<>();
            for (RoleDTO role : employee.getRoles()) {
                roleString.add(role.getName().name().toLowerCase());
            }
            employeeReportDTOList.add(EmployeeReportDTO.builder()
                    .firstName(employee.getFirstName())
                    .lastName(employee.getLastName())
                    .username(employee.getUsername())
                    .phone(employee.getPhone())
                    .dob(employee.getDob())
                    .email(employee.getEmail())
                    .roles(String.join(", ", roleString))
                    .build());
        }
        JRBeanCollectionDataSource tableDataSource = new JRBeanCollectionDataSource(employeeReportDTOList);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("TABLE_DATA_SOURCE", tableDataSource);

        // Create datasource (current user)
        EmployeeDTO currentEmployee = employeeService.getCurrentEmployee();

        Set<String> roleNames = new HashSet<>();
        for (RoleDTO roleDTO : currentEmployee.getRoles()) {
            roleNames.add(roleDTO.getName().name().toLowerCase());
        }

        List<EmployeeReportDTO> employees = new ArrayList<>(); // datasource
        employees.add(EmployeeReportDTO.builder()
                .firstName(currentEmployee.getFirstName())
                .lastName(currentEmployee.getLastName())
                .username(currentEmployee.getUsername())
                .email(currentEmployee.getEmail())
                .roles(String.join(", ", roleNames))
                .build());

        byte[] pdfBytes = jasperReportService.generateEmployeeReport(reportName, parameters, employees);

        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/project")
    public ResponseEntity<byte[]> generateProjectReport() throws JRException, IOException, ResourceNotFoundException {
        String reportName = "project";

        List<ProjectReportDTO> projectReportDTOS = new ArrayList<>();
        for (ProjectDTO projectDTO : projectService.getAll()) {
            String leaderName = "none";
            if (projectDTO.getLeader_id() != 0) {
                EmployeeDTO leader = employeeService.getById(projectDTO.getLeader_id());
                leaderName = leader.getFirstName() + " " + leader.getLastName();
            }

            projectReportDTOS.add(
                    ProjectReportDTO.builder()
                            .name(projectDTO.getName())
                            .startDate(projectDTO.getStartDate().toString())
                            .status(projectDTO.getStatus().name().toLowerCase())
                            .budget(projectDTO.getBudget().toString())
                            .leader(leaderName)
                            .size(String.valueOf(projectDTO.getMembers().size()))
                            .build()
            );
        }

        JRBeanCollectionDataSource tableDataSource = new JRBeanCollectionDataSource(projectReportDTOS);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("TABLE_DATA_SOURCE", tableDataSource);

        EmployeeDTO currentEmployee = employeeService.getCurrentEmployee();

        List<EmployeeReportDTO> employees = new ArrayList<>(); // datasource
        employees.add(EmployeeReportDTO.builder()
                .firstName(currentEmployee.getFirstName())
                .lastName(currentEmployee.getLastName())
                .username(currentEmployee.getUsername())
                .email(currentEmployee.getEmail())
                .phone(currentEmployee.getPhone())
                .build());

        byte[] pdfBytes = jasperReportService.generateEmployeeReport(reportName, parameters, employees);

        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
