package com.example.ADDB.TestContainer;

import com.example.ADDB.entity.Employee;
import com.example.ADDB.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TestContainer {
    @Autowired
    EmployeeRepository employeeRepository;


    @Test
    public void Create_test_fom_DB() {

        Employee employee = Employee.builder()
                .email("mohamedmemowow")
                .title("Berater")
                .mobileNumber("01243243525")
                .surname("Ayman")
                .givenName("Mo")
                .department("IT")
                .username("Roni")
                .build();
        employeeRepository.save(employee);
        Employee employee1 = employeeRepository.findByUsername(employee.getUsername());
        employeeRepository.deleteById(employee1.getId());
        Employee employee2 = employeeRepository.findByUsername(employee.getUsername());
        assertEquals(employee2, null);
    }
}
