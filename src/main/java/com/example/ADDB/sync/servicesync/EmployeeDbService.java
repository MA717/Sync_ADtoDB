package com.example.ADDB.sync.servicesync;


import com.example.ADDB.entity.EmployeeEntity;
import com.example.ADDB.entity.EmployeeMapper;
import com.example.ADDB.model.Employee;
import com.example.ADDB.sync.repositorysync.EmployeeSyncRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmployeeDbService {

    private final EmployeeSyncRepository employeeSyncRepository;

    private static List<Employee> sortEmployees(List<Employee> employees) {
        return employees.stream()
                .sorted(Comparator.comparing(Employee::getGivenName).thenComparing(Employee::getSurname))
                .collect(Collectors.toList());
    }

    public Employee getEmployeeByUsername(String username) {
        return EmployeeMapper.INSTANCE.employeeEntitytoEmployeeModel(employeeSyncRepository.findByUsername(username));
    }

    List<Employee> MappingEmployeeEntitytoEmployeeModel(List<EmployeeEntity> employeeEntities) {
        return EmployeeMapper.INSTANCE.mapEmployeeEntityListtoEmployeeModelList(employeeEntities);
    }

    public List<Employee> getAllEmployeesModel() {
        return sortEmployees(MappingEmployeeEntitytoEmployeeModel(employeeSyncRepository.findAll()));
    }

    public List<Employee> getEmployeesModel(String searchTerm) {
        return sortEmployees(MappingEmployeeEntitytoEmployeeModel(employeeSyncRepository.getEmployeeMatch(searchTerm)));
    }

    public List<Employee> getSubordinatesOfEmployee(String username) {
        return sortEmployees(MappingEmployeeEntitytoEmployeeModel(employeeSyncRepository.findByManagerUsername(username)));
    }

    public Employee getManager(String username) {
        return EmployeeMapper.INSTANCE.employeeEntitytoEmployeeModel(employeeSyncRepository.getManagerByUsername(username));
    }


}
