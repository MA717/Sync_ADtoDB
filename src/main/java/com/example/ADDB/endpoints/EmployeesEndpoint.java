package com.example.ADDB.endpoints;
import com.example.ADDB.model.Employee;
import com.example.ADDB.sync.servicesync.EmployeeDbService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/employees", produces = "application/json")

public class EmployeesEndpoint {
    private final EmployeeDbService personRepoDb;


    @GetMapping("")
    public List<Employee> allEmployees(

            @RequestParam(name = "searchTerm", required = false)
            String searchTerm
    ) {
        if (searchTerm == null) {
            return this.personRepoDb.getAllEmployeesModel();
        }

        return this.personRepoDb.getEmployeesModel(searchTerm);
    }


    @GetMapping("/{username}")
    public Employee getEmployeeDetails(@PathVariable(value = "username") String username)  {
        return this.personRepoDb.getEmployeeByUsername(username);
    }

    @GetMapping("/{username}/manager")
    public Employee getManagerOfEmployee(@PathVariable(value = "username") String username) {
        return personRepoDb.getManager(username);
    }

    @GetMapping("/{username}/subordinates")
    public List<Employee> getSubordinatesOfEmployee(@PathVariable(value = "username") String username) {
       return this.personRepoDb.getSubordinatesOfEmployee(username);
    }






}
