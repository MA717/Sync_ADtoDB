package com.example.ADDB;

import com.example.ADDB.entity.Employee;
import com.example.ADDB.Service.EmployeeService;
import com.example.ADDB.Service.EmployeeServiceSync;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/employees", produces = "application/json")

public class EmployeesEndpoint {

    private final EmployeeService personServ;
    private final EmployeeServiceSync personSync;


    @GetMapping("/consumerInitializer")
    public List<Employee> initializer() {
        return personSync.consumerInitializer();
    }

    @GetMapping("/initdb")
    public void allEmployees() {
        personServ.initDb();
    }

    @GetMapping("/syncronize")
    public void beginSyncronization() {
        personSync.dbtoAdSyncronization();
    }
}