package com.example.ADDB.TestContainer;

import com.example.ADDB.entity.EmployeeEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TestContainer {
//    @Autowired
//    EmployeeRepository employeeRepository;



    @Test
    public void Create_test_fom_DB() {

        EmployeeEntity employeeEntity = EmployeeEntity.builder()
                .email("mohamedmemowow")
                .title("Berater")
                .mobileNumber("01243243525")
                .surname("Ayman")
                .givenName("Mo")
                .department("IT")
                .username("Roni")
                .build();

//        EmployeeEntity employee1 = employeeRepository.findByUsername(employeeEntity.getUsername());
//        employeeRepository.deleteById(employee1.getId());
//        EmployeeEntity employee2 = employeeRepository.findByUsername(employeeEntity.getUsername());
//        assertEquals(employee2, null);
    }
}
