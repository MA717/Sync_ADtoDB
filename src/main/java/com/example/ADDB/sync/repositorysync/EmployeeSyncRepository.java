package com.example.ADDB.sync.repositorysync;


import com.example.ADDB.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeSyncRepository extends JpaRepository<EmployeeEntity, Long> {
    Optional<EmployeeEntity> findByDn(String dn);

    EmployeeEntity findByUsername(String username);

    @Query("SELECT employee FROM  EmployeeEntity employee INNER JOIN EmployeeEntity manager ON manager.id = employee.manager.id where manager.username= :username")
    List<EmployeeEntity> findByManagerUsername(@Param("username") String username);


    @Query("SELECT manager FROM EmployeeEntity employee INNER JOIN EmployeeEntity manager ON manager.id = employee.manager.id where employee.username = :username")
    EmployeeEntity getManagerByUsername(@Param("username") String username);


    @Query("SELECT employee From EmployeeEntity  employee where " +
            "    employee.username LIKE CONCAT('%',:searchTerm,'%') " +
            "or employee.givenName LIKE CONCAT('%',:searchTerm,'%')  " +
            " or employee.surname  LIKE CONCAT('%',:searchTerm,'%')   ")
    List<EmployeeEntity> getEmployeeMatch(@Param("searchTerm") String searchTerm);
    @Transactional
    void deleteByDn(String dn);
}




