package com.example.ADDB.Service;

import com.example.ADDB.Entity.Employee;
import com.example.ADDB.Entity.EmployeeMapper;
import com.example.ADDB.Model.EmployeeModel;
import com.example.ADDB.Repository.EmployeeRepository;
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
                .forEach(x -> saveEmployee(x));
        connectEmpWithManager(employeeList);

    }


    public Optional<Employee> getManager(EmployeeModel employee) {
                return Optional.ofNullable(employeeRepository.findByDn(employee.getManager())).orElse(null)  ;
    }


    public void connectEmpWithManager(EmployeeModel employee) {
        Employee employee1 = employeeRepository.findByUsername(employee.getUsername());
        employee1.setManager(getManager(employee).get());
        employeeRepository.save(employee1);
    }

    public void connectEmpWithManager(List<EmployeeModel> employelList) {

        for (EmployeeModel employeeModel : employelList) {
            Employee employee = employeeRepository.findByUsername(employeeModel.getUsername());
            if (employee.getManager() != null) {
                Employee manager = getManager(employeeModel).get();
                if (manager != null) {
                    employee.setManager(manager);
                    employeeRepository.save(employee);
                }
            }
        }


    }

    public Employee saveEmployee(EmployeeModel employeeModel) {

        Employee employee = EmployeeMapper.INSTANCE.employeeModeltoEmployee(employeeModel);
        return employeeRepository.save(employee);

    }


}
