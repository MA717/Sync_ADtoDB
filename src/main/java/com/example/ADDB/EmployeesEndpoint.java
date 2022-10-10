package com.example.ADDB;

import com.example.ADDB.Model.EmployeeModel;

import com.example.ADDB.Service.EmployeeService;
import com.example.ADDB.Service.EmployeeServiceSync;
import com.example.ADDB.ldap.queries.EmployeeRepositoyLdap;
import com.example.ADDB.ldap.queries.LdapQueryAllEmployees;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/employees", produces="application/json")

public class EmployeesEndpoint {

    private final EmployeeService personServ;
    private final EmployeeServiceSync personSync;
    @GetMapping("")
    public Boolean  allEmployees() {

        personServ.init_DB();
        return true;


    }

    @GetMapping("/syncronize")
    public void beginSyncronization(){
        personSync.dbtoAdSyncronization();
    }
}