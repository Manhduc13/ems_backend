package com.ndm.serve.mappers;

import com.ndm.serve.dtos.employee.EmployeeCUDTO;
import com.ndm.serve.dtos.employee.EmployeeDTO;
import com.ndm.serve.models.Employee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface EmployeeMapper {

    EmployeeDTO toEmployeeDTO(Employee employee);

    Employee toEmployee(EmployeeCUDTO employeeCUDTO);
}
