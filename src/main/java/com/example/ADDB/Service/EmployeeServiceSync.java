package com.example.ADDB.Service;

import antlr.CharQueue;
import com.example.ADDB.Entity.Changes;
import com.example.ADDB.Entity.Employee_Changes;
import com.example.ADDB.Model.EmployeeModel;
import com.example.ADDB.Repository.EmployeeRepository;
import com.example.ADDB.Entity.Employee;
import com.example.ADDB.ldap.queries.EmployeeRepositoyLdap;
import com.example.ADDB.ldap.queries.LdapQueryAllEmployees;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeServiceSync {


    private final Sinks.Many<Message<Employee_Changes>> manyChanged;
    private final EmployeeRepositoyLdap employeeRepositoryldap;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;


    public void dbtoAdSyncronization() {
        Set<Long> presentedEmployee = new HashSet<>();
        List<EmployeeModel> employeeList = employeeRepositoryldap.queryMany(new LdapQueryAllEmployees());
        employeeList.stream()
                .forEach(x -> compareEmployeeAttribute(x, presentedEmployee));
        CheckforDeletedUser(presentedEmployee);

    }


    void CheckforDeletedUser(Set<Long> updatedEmployee) {
        List<Employee> employees = employeeRepository.findAll();
        employees.stream()
                .filter(x -> !updatedEmployee.contains(x.getId()))
                .forEach(x -> employeeRepository.deleteById(x.getId()));
    }


    private void fireChangeEvent(Employee employee, List<Changes> changesList) {
        Employee_Changes employeeChanges = Employee_Changes.builder().changesList(changesList).employee(employee).build();
        manyChanged.emitNext(MessageBuilder.withPayload(employeeChanges).build(), Sinks.EmitFailureHandler.FAIL_FAST);
        log.info(" Changes has occured in the Employee");
        employeeRepository.save(employee);
    }

    public Employee addNewEmployee(EmployeeModel employeeModel) {
        Employee employee = employeeService.saveEmployee(employeeModel);
        employeeService.connectEmpWithManager(employeeModel);
        return employee ;
    }

    private void compareEmployeeAttribute(EmployeeModel employeeModel, Set<Long> presentedEmployee) {

        // employe model in ldap
        Employee employee1 = employeeRepository.findByUsername(employeeModel.getUsername());
        if (employee1 != null) {
            List<Changes> changesList = employee1.equals(employeeModel, employeeService.getManager(employeeModel));
            if (!changesList.isEmpty()) {
                fireChangeEvent(employee1, changesList);
            }

            presentedEmployee.add(employee1.getId());
        }
        else {
           Employee employee2 =  addNewEmployee(employeeModel);
            presentedEmployee.add(employee2.getId());
        }


    }
}
