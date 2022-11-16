package com.example.ADDB.sync.servicesync;


import com.example.ADDB.entity.Changes;
import com.example.ADDB.entity.EmployeeChanges;
import com.example.ADDB.entity.EmployeeEntity;
import com.example.ADDB.entity.EmployeeMapper;
import com.example.ADDB.ldap.queries.LdapQueryAllEmployees;
import com.example.ADDB.model.Employee;
import com.example.ADDB.sync.repositorysync.EmployeeSyncRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.ADDB.ldap.queries.EmployeeRepositoyLdap;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.net.URI;
import java.util.*;

import static org.javers.core.diff.ListCompareAlgorithm.LEVENSHTEIN_DISTANCE;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeSyncService {
    private final EmployeeRepositoyLdap employeeRepositoryLdap;
    private final EmployeeSyncRepository employeeSyncRepository;
    private final Sinks.Many<CloudEvent> manyChanged;


    public void syncBegin() {

        if (employeeSyncRepository.count() == 0) {
            initDb();
        } else {
            adtoDbSynchronize();
        }

    }


    public void initDb() {
        List<Employee> employeeList = employeeRepositoryLdap.queryMany(new LdapQueryAllEmployees());
        employeeList.stream()
                .filter(x -> employeeSyncRepository.findByDn(x.getDn().toString()).isEmpty())
                .forEach(this::saveEmployee);
        connectEmployeessWithManagers(employeeList);
    }

    public void adtoDbSynchronize() {
        Set<Long> presentedEmployee = new HashSet<>();
        List<Employee> employeeList = employeeRepositoryLdap.queryMany(new LdapQueryAllEmployees());

        for (Employee employeeModel : employeeList) {
            compareEmployeeAttribute(employeeModel, presentedEmployee);
        }
        checkForDeletedUser(presentedEmployee);
    }

    public EmployeeEntity employeeModeltoEmployeeEntity(Employee employeeModel) {
        EmployeeEntity employee = EmployeeMapper.INSTANCE.employeeModeltoEmployeeEntity(employeeModel);
        getManager(employeeModel).ifPresentOrElse(employee::setManager,
                () -> employee.setManager(null));

        return employee;
    }


    private void compareEmployeeAttribute(Employee employeeModel, Set<Long> presentedEmployee) {

        employeeSyncRepository
                .findByDn(employeeModel.getDn().toString())
                .ifPresentOrElse((employee) -> {
                            EmployeeEntity employeeNew = employeeModeltoEmployeeEntity(employeeModel);
                            if (compareChange(employee, employeeNew).isPresent()) {
                                fireChangeEvent(employee, Changes.EMPLOYEE_CHANGED);
                            }

                            presentedEmployee.add(employee.getId());
                        }, () -> {

                            EmployeeEntity employee = createEmployeeAction(employeeModel);
                            presentedEmployee.add(employee.getId());


                        }
                );


    }

    private Optional<Changes> compareChange(EmployeeEntity employeeEntityOld, EmployeeEntity employeeEntityNew) {

        Javers javers = JaversBuilder.javers().withListCompareAlgorithm(LEVENSHTEIN_DISTANCE).build();
        employeeEntityNew.setId(employeeEntityOld.getId());

        Diff diff = javers.compare(employeeEntityOld, employeeEntityNew);
        List<ValueChange> changedValue = diff.getChangesByType(ValueChange.class);

        if (!changedValue.isEmpty()) {
            proceedChange(employeeEntityOld, employeeEntityNew);
            return Optional.of(Changes.EMPLOYEE_CHANGED);
        } else {
            return Optional.empty();
        }

    }


    private void proceedChange(EmployeeEntity employeeEntityOld, EmployeeEntity employeeEntityNew) {

        employeeEntityOld.setGivenName(employeeEntityNew.getGivenName());
        employeeEntityOld.setSurname(employeeEntityNew.getSurname());
        employeeEntityOld.setEmail(employeeEntityNew.getEmail());
        employeeEntityOld.setMobileNumber(employeeEntityNew.getMobileNumber());
        employeeEntityOld.setDepartment(employeeEntityNew.getDepartment());
        employeeEntityOld.setTelephoneNumber(employeeEntityNew.getTelephoneNumber());
        employeeEntityOld.setTitle(employeeEntityNew.getTitle());
        employeeEntityOld.setUsername(employeeEntityNew.getUsername());
        employeeEntityOld.setManager(employeeEntityNew.getManager());

    }


    @SneakyThrows
    private void fireChangeEvent(EmployeeEntity employee, Changes change) {
        Employee employeeModel = EmployeeMapper.INSTANCE.employeeEntitytoEmployeeModel(employee);
        EmployeeChanges employeeChanges = EmployeeChanges.builder().changes(change).employee(employeeModel).build();
        log.info(" Changes has occurred in {}", employeeModel);
        employeeSyncRepository.save(employee);

        ObjectMapper mapper = new ObjectMapper();
        manyChanged.emitNext(CloudEventBuilder
                        .v1()
                        .withId(UUID.randomUUID().toString())
                        .withDataContentType("application/json")
                        .withSource(URI.create("service://employee-list-service"))
                        .withType("Employee_Changes")
                        .withData(mapper.writeValueAsBytes(employeeChanges))
                        .build()
                , Sinks.EmitFailureHandler.FAIL_FAST);
    }


    private EmployeeEntity saveEmployee(Employee employeeModel) {
        EmployeeEntity employee = EmployeeMapper.INSTANCE.employeeModeltoEmployeeEntity(employeeModel);
        return employeeSyncRepository.save(employee);
    }


    public Optional<EmployeeEntity> getManager(Employee employee) {
        return employeeSyncRepository.findByDn(employee.getManager());
    }

    public void connectEmployeessWithManagers(List<Employee> employelList) {
        employelList.forEach(this::connectEmpWithManager);

    }

    public void connectEmpWithManager(Employee employeeModel) {
        employeeSyncRepository.findByDn(employeeModel.getDn().toString())
                .ifPresent((employee) -> getManager(employeeModel)
                        .ifPresent((manager) -> {
                                    employee.setManager(manager);
                                    employeeSyncRepository.save(employee);
                                }
                        ));
    }


   private void checkForDeletedUser(Set<Long> updatedEmployee) {

        List<EmployeeEntity> employees = employeeSyncRepository.findAll();
        employees.stream()
                .filter(x -> !(updatedEmployee.contains(x.getId())))
                .forEach(this::deletedEmployeeAction);

    }


    private EmployeeEntity deletedEmployeeAction(EmployeeEntity employee) {
        employeeSyncRepository.deleteByDn(employee.getDn());
        fireChangeEvent(employee, Changes.EMPLOYEE_DELETED);
        return employee;
    }


    private EmployeeEntity createEmployeeAction(Employee employeeModel) {
        EmployeeEntity employee = addNewEmployee(employeeModel);
        fireChangeEvent(employee, Changes.EMPLOYEE_CREATED);
        return employee;
    }

    public EmployeeEntity addNewEmployee(Employee employeeModel) {
        EmployeeEntity employee = saveEmployee(employeeModel);
        connectEmpWithManager(employeeModel);
        return employee;
    }


}
