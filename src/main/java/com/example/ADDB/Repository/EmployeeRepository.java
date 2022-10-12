package com.example.ADDB.Repository;

import com.example.ADDB.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long > {


    Employee findByUsername(String username);

    @Query( value = "SELECT * from Employee e where e.first_name = ?1 and e.last_name=?2 " , nativeQuery = true)
    Employee findByFirstNameAndLastName(String first_name , String last_name);

    Employee findByDn(String manager);
}
