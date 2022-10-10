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
import java.util.function.BiConsumer;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeServiceSync {
    EmployeeRepositoyLdap employeeRepositoryldap;
    EmployeeRepository employeeRepository;
    EmployeeService employeeService;
    HashMap<Long, Boolean> updatedEmployee = new HashMap<>();

    public void dbtoAdSyncronization() {

        List<EmployeeModel> employeeList = employeeRepositoryldap.queryMany(new LdapQueryAllEmployees());
        initiateEmployeList();
        for (int i = 0; i < employeeList.size(); i++) {
            EmployeeModel employee = employeeList.get(i);
            compareEmployeeAttribute(employee);
        }
        CheckforDeletedUser(updatedEmployee);
        updatedEmployee.clear(); // clear our list after finishing
    }

    void initiateEmployeList( ){
     List<Employee> employees =   employeeRepository.findAll();
        for ( int i = 0 ; i < employees.size() ; i++ )
        {
          updatedEmployee.put(employees.get(i).getId() , false)  ;
          // initialise our check list with false for all employees
        }
    }

    void CheckforDeletedUser(HashMap<Long, Boolean> updatedEmployee) {
        List<Employee> employees = employeeRepository.findAll();
        for (int i = 0; i < updatedEmployee.size(); i++) {
            if (updatedEmployee.get(employees.get(i).getId()) == false) // if employee not found in the updated list then delete it
            {
                employeeRepository.deleteById(employees.get(i).getId());
                // fire an Event
            }
        }
    }


    private void compareEmployeeAttribute(EmployeeModel employee) {


        Employee employee1 = employeeRepository.findByUsername(employee.getUsername());
        if (employee1 != null) {
            List<Changes> changesList = employee1.equals(employee , employeeService.getManager(employee));
            if (!changesList.isEmpty()) {
                //changes occured and saved in the changeList
                log.info(" Changes has occured in the Employee");
                employeeRepository.save(employee1);
            }
            updatedEmployee.put(employee1.getId(), true);


        } else {
            // neu Mitarbeiter wurde eingestellt
            Employee employee2 = employeeService.saveEmployee(employee);
            employeeService.connectEmpWithManager(employee);
            updatedEmployee.put(employee2.getId(), true);
        }


    }
}
