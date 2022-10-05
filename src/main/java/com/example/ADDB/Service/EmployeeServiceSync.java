package com.example.ADDB.Service;

import com.example.ADDB.Entity.Changes;
import com.example.ADDB.Model.EmployeeModel;
import com.example.ADDB.Repository.EmployeeRepository;
import com.example.ADDB.Entity.Employee;
import com.example.ADDB.ldap.queries.EmployeeRepositoyLdap;
import com.example.ADDB.ldap.queries.LdapQueryAllEmployees;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeServiceSync {
    EmployeeRepositoyLdap employeeRepositoryldap;
    EmployeeRepository employeeRepository;

    EmployeeService employeeService;
    HashMap<Long, Boolean> checkMap = new HashMap<>();


    void dbtoAdSyncronization() {
        List<EmployeeModel> employeeList = employeeRepositoryldap.queryMany(new LdapQueryAllEmployees());

        for (int i = 0; i < employeeList.size(); i++) {
            EmployeeModel employee = employeeList.get(i);
            compareEmployeeAttribute(employee);

        }
    }

    private void compareEmployeeAttribute(EmployeeModel employee) {
        Employee employee1 = employeeRepository.findByUsername(employee.getUsername());
        if (employee1 != null) {
            List<Changes> changesList = employee1.equals(employee);
            if (!changesList.isEmpty()) {
                //changes occured and saved in the changeList
                log.info(" Changes has occured in the Employee");
                employeeRepository.save(employee1);
                checkMap.put(employee1.getId(), true);
            }
        } else {
            // neu Mitarbeiter wurde eingestellt
            Employee employee2 = employeeService.saveEmployee(employee);
            employeeService.connectEmpWithManager(employee);
            checkMap.put(employee2.getId(), true);
        }


    }
}
