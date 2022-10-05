package com.example.ADDB.Service;

import com.example.ADDB.Entity.Employee;
import com.example.ADDB.Model.EmployeeModel;
import com.example.ADDB.Repository.EmployeeRepository;
import com.example.ADDB.ldap.queries.EmployeeRepositoyLdap;
import com.example.ADDB.ldap.queries.LdapQueryAllEmployees;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
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
            Boolean result = saveEmployee(employeeList.get(i));
            if (result == false) {
                log.info("Error occured when trying to add employe with index {} and givenName", i, employeeList.get(i).getGivenName());
            }
        }

        connectEmpWithManager(employeeList);

    }


    public List<String> getManagerFirstAndLastName(EmployeeModel employee) {

        List<String> extractManagerName = new ArrayList<>();
        String manager = employee.getManager();
        String managerName = manager.substring(manager.indexOf("=") + 1, manager.indexOf(","));
        String firstName = managerName.substring(0, managerName.indexOf(" "));
        String lastName = managerName.substring(managerName.indexOf(" ")+1);

        extractManagerName.add(firstName);
        extractManagerName.add(lastName);

        return extractManagerName;
    }

    public void connectEmpWithManager(List<EmployeeModel> employelList) {
        for (int i = 0; i < employelList.size(); i++) {
            Employee employee = employeeRepository.findByUsername(employelList.get(i).getUsername());
            if (employelList.get(i).getManager() != null) {

                List<String> managerName = getManagerFirstAndLastName(employelList.get(i));
                Employee manager = employeeRepository.findByFirstNameAndLastName(managerName.get(0), managerName.get(1));
                if (manager != null) {
                    employee.setManager(manager);
                    employeeRepository.save(employee);
                }
            }
        }
    }

    public boolean saveEmployee(EmployeeModel employeeModel) {
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
        if (employee1 != null) {
            return true;
        } else return false;
    }


}
