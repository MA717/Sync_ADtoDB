package com.example.ADDB.Service;


import com.example.ADDB.entity.Changes;
import com.example.ADDB.entity.Employee;
import com.example.ADDB.entity.EmployeeMapper;
import com.example.ADDB.entity.Employee_Changes;
import com.example.ADDB.ldap.queries.EmployeeRepositoyLdap;
import com.example.ADDB.ldap.queries.LdapQueryAllEmployees;
import com.example.ADDB.model.EmployeeModel;
import com.example.ADDB.repository.EmployeeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.net.URI;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeServiceSync {


    private final Sinks.Many<CloudEvent> manyChanged;
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
                .forEach(x -> {
                    try {
                        deletedEmployeeAction(x, changesList);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });

    }


    private Employee deletedEmployeeAction(Employee employee, List<Changes> changesList) throws JsonProcessingException {
        fireChangeEvent(employee, changesList);
        employeeRepository.deleteById(employee.getId());
        return employee;
    }

    private Employee createEmployeeAction(EmployeeModel employeeModel) throws JsonProcessingException {
        Employee employee = addNewEmployee(employeeModel);
        List<Changes> changesList = new ArrayList<>();
        changesList.add(Changes.NEWEMPLOYEE_Created);
        fireChangeEvent(employee, changesList);
        return employee;
    }


    private void fireChangeEvent(Employee employee, List<Changes> changesList) throws JsonProcessingException {
        Employee_Changes employeeChanges = Employee_Changes.builder().changesList(changesList).employee(employee).build();
        log.info(" Changes has occured in the Employee");
        employeeRepository.save(employee);
        ObjectMapper mapper = new ObjectMapper();

        manyChanged.emitNext(CloudEventBuilder
                        .v1()
                        .withId(UUID.randomUUID().toString())
                        .withDataContentType("application/json")
                        .withSource(URI.create("http//localhost:8080/employees/syncronize"))
                        .withType("Employee_Changes")
                        .withData(mapper.writeValueAsBytes(employeeChanges))
                        .build()
                , Sinks.EmitFailureHandler.FAIL_FAST);
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

                            if (!changesList.isEmpty()) {
                                try {
                                    fireChangeEvent(employee1, changesList);
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            presentedEmployee.add(employee1.getId());
                        }, () -> {

                            try {
                                Employee employee = createEmployeeAction(employeeModel);
                                presentedEmployee.add(employee.getId());
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }


                        }
                );


    }
}
