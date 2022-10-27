package com.example.ADDB.repository;

import com.example.ADDB.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long > {


    Employee findByUsername(String username);

    Optional<Employee> findByDn(String employee);
}
