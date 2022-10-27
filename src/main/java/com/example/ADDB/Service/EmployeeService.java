package com.example.ADDB.Service;

import com.example.ADDB.entity.Employee;
import com.example.ADDB.entity.EmployeeMapper;
import com.example.ADDB.model.EmployeeModel;
import com.example.ADDB.repository.EmployeeRepository;
import com.example.ADDB.ldap.queries.EmployeeRepositoyLdap;
import com.example.ADDB.ldap.queries.LdapQueryAllEmployees;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeService {
    EmployeeRepository employeeRepository;
    EmployeeRepositoyLdap employeeRepositoryldap;


    public void initDb() {
        List<EmployeeModel> employeeList = employeeRepositoryldap.queryMany(new LdapQueryAllEmployees());

        employeeList.stream()
                .filter(x -> x != null)
                .filter(x -> !employeeRepository.findByDn(x.getDn().toString()).isPresent())
                .forEach(x -> saveEmployee(x));

        connectEmployeessWithManagers(employeeList);

    }


    public Optional<Employee> getManager(EmployeeModel employee) {
        return Optional.ofNullable(employeeRepository.findByDn(employee.getManager())).orElse(null);
    }


    public void connectEmpWithManager(EmployeeModel employeeModel) {
        employeeRepository.findByDn(employeeModel.getDn().toString())
                .ifPresent((employee) -> {
                    getManager(employeeModel).ifPresent((manager) -> {
                                employee.setManager(manager);
                                employeeRepository.save(employee);
                            }
                    );

                });

    }

    public void connectEmployeessWithManagers(List<EmployeeModel> employelList) {
        employelList.stream()
                .forEach(employee -> connectEmpWithManager(employee));

    }

    public Employee saveEmployee(EmployeeModel employeeModel) {

        Employee employee = EmployeeMapper.INSTANCE.employeeModeltoEmployee(employeeModel);
        return employeeRepository.save(employee);

    }


}
