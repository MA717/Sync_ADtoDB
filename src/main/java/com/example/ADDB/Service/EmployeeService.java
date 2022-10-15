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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeService {
    EmployeeRepository employeeRepository;
    EmployeeRepositoyLdap employeeRepositoryldap;

    public boolean addEmployee(Employee employee) {
        employeeRepository.save(employee);
        return true;
    }


    public void initDb() {
        List<EmployeeModel> employeeList = employeeRepositoryldap.queryMany(new LdapQueryAllEmployees());

        employeeList.stream()
                .filter(x -> x != null)
                .forEach(x -> saveEmployee(x));

        connectEmpWithManager(employeeList);

    }


    public List<String> getManagerFirstAndLastName(EmployeeModel employee) {

        List<String> extractManagerName = new ArrayList<>();
        String manager = employee.getManager();
        if (manager != null) {
            String managerName = manager.substring(manager.indexOf("=") + 1, manager.indexOf(","));
            String firstName = managerName.substring(0, managerName.indexOf(" "));
            String lastName = managerName.substring(managerName.indexOf(" ") + 1);

            extractManagerName.add(firstName);
            extractManagerName.add(lastName);

            return extractManagerName;
        } else
            return null;
    }

    public Employee getManager(EmployeeModel employee) {
        return employeeRepository.findByDn(employee.getManager());
    }


    public void connectEmpWithManager(EmployeeModel employee) {
        Employee employee1 = employeeRepository.findByUsername(employee.getUsername());
        employee1.setManager(getManager(employee));
        employeeRepository.save(employee1);
    }

    public void connectEmpWithManager(List<EmployeeModel> employelList) {

        for (EmployeeModel employeeModel : employelList) {
            Employee employee = employeeRepository.findByUsername(employeeModel.getUsername());
            if (employee.getManager() != null) {
                Employee manager = getManager(employeeModel);
                if (manager != null) {
                    employee.setManager(manager);
                    employeeRepository.save(employee);
                }
            }
        }


    }

    public Employee saveEmployee(EmployeeModel employeeModel) {

        Employee employee = EmployeeMapper.INSTANCE.employeeModeltoEmployee(employeeModel);
           return  employeeRepository.save(employee);

    }


}
