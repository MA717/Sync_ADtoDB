package com.example.ADDB.Service;

import com.example.ADDB.Entity.Employee;
import com.example.ADDB.Model.EmployeeModel;
import com.example.ADDB.Repository.EmployeeRepository;
import com.example.ADDB.ldap.queries.EmployeeRepositoyLdap;
import com.example.ADDB.ldap.queries.LdapQueryAllEmployees;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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


    public void init_DB() {
        List<EmployeeModel> employeeList = employeeRepositoryldap.queryMany(new LdapQueryAllEmployees());
        for (int i = 0; i < employeeList.size(); i++) {
            Employee employee=  saveEmployee(employeeList.get(i));
            if (employee == null) {
                log.info("Error occured when trying to add employe with index {} and givenName", i, employeeList.get(i).getGivenName());
            }
        }

        connectEmpWithManager(employeeList);

    }


    public List<String> getManagerFirstAndLastName(EmployeeModel employee) {

        List<String> extractManagerName = new ArrayList<>();
        String manager = employee.getManager();
        if (manager != null ) {
            String managerName = manager.substring(manager.indexOf("=") + 1, manager.indexOf(","));
            String firstName = managerName.substring(0, managerName.indexOf(" "));
            String lastName = managerName.substring(managerName.indexOf(" ") + 1);

            extractManagerName.add(firstName);
            extractManagerName.add(lastName);

            return extractManagerName;
        }
        else
            return null ;
    }

    public Employee getManager (EmployeeModel employee )
    {
        List<String> managerName = getManagerFirstAndLastName(employee); // need to check if the manager of employee changed
        if ( managerName != null ) {
            return employeeRepository.findByFirstNameAndLastName(managerName.get(0), managerName.get(1));
        }
        else return  null ; // has no manager
    }


    public void connectEmpWithManager( EmployeeModel employee){
     Employee employee1 = employeeRepository.findByUsername(employee.getUsername());
     employee1.setManager(getManager(employee));
     employeeRepository.save(employee1);
    }

    public void connectEmpWithManager(List<EmployeeModel> employelList) {
        for (int i = 0; i < employelList.size(); i++) {
            Employee employee = employeeRepository.findByUsername(employelList.get(i).getUsername());
            if (employelList.get(i).getManager() != null) {
             Employee manager = getManager(employelList.get(i));
                if (manager != null) {
                    employee.setManager(manager);
                    employeeRepository.save(employee);
                }
            }
        }
    }

    public Employee saveEmployee(EmployeeModel employeeModel) {
        Employee employee = Employee.builder()
                .email(employeeModel.getEmail())
                .department(employeeModel.getDepartment())
                .title(employeeModel.getTitle())
                .mobileNumber(employeeModel.getMobileNumber())
                .telephoneNumber(employeeModel.getTelephoneNumber())
                .username(employeeModel.getUsername())
                .givenName(employeeModel.getGivenName())
                .surname(employeeModel.getSurname())
                .build();

        Employee employee1 = employeeRepository.save(employee);

        return employee1 ;
    }


}
