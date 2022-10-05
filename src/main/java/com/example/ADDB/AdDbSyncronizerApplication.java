package com.example.ADDB;

import com.example.ADDB.ldap.queries.EmployeeRepositoyLdap;
import com.example.ADDB.ldap.queries.LdapQueryAllEmployees;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class AdDbSyncronizerApplication {
	@Autowired
	private  ApplicationContext applicationContext;


	public static void main(String[] args) {
		SpringApplication.run(AdDbSyncronizerApplication.class, args);
	}

}
