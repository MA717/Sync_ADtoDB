package com.example.ADDB;

import com.azure.spring.messaging.eventhubs.support.EventHubsHeaders;
import com.example.ADDB.Entity.Employee_Changes;
import com.example.ADDB.ldap.queries.EmployeeRepositoyLdap;
import com.example.ADDB.ldap.queries.LdapQueryAllEmployees;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AdDbSyncronizerApplication {

	public static final Logger LOGGER = LoggerFactory.getLogger(AdDbSyncronizerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AdDbSyncronizerApplication.class, args);
	}


}
