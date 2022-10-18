package com.example.ADDB.Service;


import com.example.ADDB.Entity.Changes;
import com.example.ADDB.Entity.Employee;
import com.example.ADDB.Entity.EmployeeMapper;
import com.example.ADDB.Entity.Employee_Changes;
import com.example.ADDB.Model.EmployeeModel;
import com.example.ADDB.Repository.EmployeeRepository;
import com.example.ADDB.ldap.queries.EmployeeRepositoyLdap;
import com.example.ADDB.ldap.queries.LdapQueryAllEmployees;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeServiceSync {


    private final Sinks.Many<Message<Employee_Changes>> manyChanged;

    private final Sinks.Many<Message<Collection<Employee>>> employeeBuckets;
    private final EmployeeRepositoyLdap employeeRepositoryldap;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;


    public void dbtoAdSyncronization() {
        Set<Long> presentedEmployee = new HashSet<>();
        List<EmployeeModel> employeeList = employeeRepositoryldap.queryMany(new LdapQueryAllEmployees());
//        employeeList.stream()
//                .forEach(x -> compareEmployeeAttribute(x, presentedEmployee));
//
//
        for (EmployeeModel employeeModel : employeeList) {
            compareEmployeeAttribute(employeeModel, presentedEmployee);
        }
        CheckforDeletedUser(presentedEmployee);

    }


    public void consumerInitializer() {

        fireInitializerEvent(employeeRepository.findAll());
    }

    void CheckforDeletedUser(Set<Long> updatedEmployee) {
        List<Changes> changesList = new ArrayList<>();
        changesList.add(Changes.EMPLOYEE_DELETED);

        List<Employee> employees = employeeRepository.findAll();
//        employees.stream()
//                .filter(x -> !updatedEmployee.contains(x.getId()))
//                .map(x -> deletedEmployeeEvent(x, changesList));

        for( Employee employee : employees )
        {
            if (! updatedEmployee.contains(employee.getId()))
            {
                deletedEmployeeEvent(employee, changesList);
            }
        }

    }


    public Employee deletedEmployeeEvent(Employee employee, List<Changes> changesList) {
        fireChangeEvent(employee, changesList);
        employeeRepository.deleteById(employee.getId());
        return employee;
    }

    public void fireInitializerEvent(Collection<Employee> employeeList) {
        employeeBuckets.emitNext(MessageBuilder.withPayload(employeeList).build(), Sinks.EmitFailureHandler.FAIL_FAST);
        log.info(" Sent Employees Buckets{} ");

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
        return employee;
    }

    Employee employeeModeltoEmployeeMapper(EmployeeModel employeeModel, Employee Manager) {
        Employee employee = EmployeeMapper.INSTANCE.employeeModeltoEmployee(employeeModel);
        employee.setManager(employeeService.getManager(employeeModel));
        return employee;
    }

    private void compareEmployeeAttribute(EmployeeModel employeeModel, Set<Long> presentedEmployee) {

        // employe model in ldap
        Employee employee1 = employeeRepository.findByDn(employeeModel.getDn().toString());
        List<Changes> changesList;
        if (employee1 != null) {
            changesList = employee1.compareChanges(employeeModeltoEmployeeMapper(employeeModel, employeeService.getManager(employeeModel)));

            if (!changesList.isEmpty()) {
                fireChangeEvent(employee1, changesList);
            }

            presentedEmployee.add(employee1.getId());
        } else {
            Employee employee2 = addNewEmployee(employeeModel);
            presentedEmployee.add(employee2.getId());
            changesList = new ArrayList<>();
            changesList.add(Changes.NEWEMPLOYEE_Created);
            fireChangeEvent(employee2, changesList);
        }


    }
}
