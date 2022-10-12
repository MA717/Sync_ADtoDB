package com.example.ADDB.Service;

import com.example.ADDB.Entity.Changes;
import com.example.ADDB.Entity.Employee_Changes;
import com.example.ADDB.Model.EmployeeModel;
import com.example.ADDB.Repository.EmployeeRepository;
import com.example.ADDB.Entity.Employee;
import com.example.ADDB.ldap.queries.EmployeeRepositoyLdap;
import com.example.ADDB.ldap.queries.LdapQueryAllEmployees;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeServiceSync {

    @Autowired
    public Sinks.Many<Message<Employee_Changes>> manyChanged;

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

        // employe model in ldap
        Employee employee1 = employeeRepository.findByUsername(employee.getUsername());
        // gehe davon aus dass username attribute nicht verändert wird
        if (employee1 != null) {
            List<Changes> changesList = employee1.equals(employee , employeeService.getManager(employee));
            if (!changesList.isEmpty()) {
              Employee_Changes employeeChanges =  Employee_Changes.builder().changesList(changesList).employee(employee1).build();
                // object with the new attributes of the employee and the all the changed that occured

                manyChanged.emitNext(MessageBuilder.withPayload(employeeChanges).build() , Sinks.EmitFailureHandler.FAIL_FAST);

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
