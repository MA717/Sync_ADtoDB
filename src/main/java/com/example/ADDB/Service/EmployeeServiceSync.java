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

import java.util.ArrayList;
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

        for (EmployeeModel employeeModel : employeeList) {
            compareEmployeeAttribute(employeeModel, presentedEmployee);
        }
        CheckforDeletedUser(presentedEmployee);
    }


    public List<Employee> consumerInitializer() {
        return employeeRepository.findAll();
    }

    void CheckforDeletedUser(Set<Long> updatedEmployee) {
        List<Changes> changesList = new ArrayList<>();
        changesList.add(Changes.EMPLOYEE_DELETED);

        List<Employee> employees = employeeRepository.findAll();
        employees.stream()
                .filter(x -> !(updatedEmployee.contains(x.getId())))
                .forEach(x -> deletedEmployeeAction(x, changesList));

    }


    private Employee deletedEmployeeAction(Employee employee, List<Changes> changesList) {
        fireChangeEvent(employee, changesList);
        employeeRepository.deleteById(employee.getId());
        return employee;
    }

    private Employee createEmployeeAction(EmployeeModel employeeModel) {
        Employee employee = addNewEmployee(employeeModel);
        List<Changes> changesList = new ArrayList<>();
        changesList.add(Changes.NEWEMPLOYEE_Created);
        fireChangeEvent(employee, changesList);
        return employee;
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

    public Employee employeeModeltoEmployeeMapper(EmployeeModel employeeModel) {
        Employee employee = EmployeeMapper.INSTANCE.employeeModeltoEmployee(employeeModel);
        employeeService.getManager(employeeModel).ifPresentOrElse((manager) -> {
            employee.setManager(manager);

        }, () -> {
            employee.setManager(null);
        });

        return employee;
    }


    private void compareEmployeeAttribute(EmployeeModel employeeModel, Set<Long> presentedEmployee) {

        employeeRepository
                .findByDn(employeeModel.getDn().toString())
                .ifPresentOrElse((employee1) -> {
                            List<Changes> changesList = employee1.compareChanges(employeeModeltoEmployeeMapper(employeeModel));
                            log.info("Test Founded ");
                            if (!changesList.isEmpty()) {
                                fireChangeEvent(employee1, changesList);
                            }
                            presentedEmployee.add(employee1.getId());
                        }, () -> {
                            log.info("Test New Employee Founded ");
                            Employee employee = createEmployeeAction(employeeModel);
                            presentedEmployee.add(employee.getId());

                        }
                );


    }
}
