package com.example.ADDB;

import com.azure.spring.messaging.eventhubs.support.EventHubsHeaders;
import com.example.ADDB.Entity.Employee_Changes;
import com.example.ADDB.ldap.queries.EmployeeRepositoyLdap;
import com.example.ADDB.ldap.queries.LdapQueryAllEmployees;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;

import java.util.function.Consumer;
import java.util.function.Function;

@SpringBootApplication
public class AdDbSyncronizerApplication {

	public static final Logger LOGGER = LoggerFactory.getLogger(AdDbSyncronizerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AdDbSyncronizerApplication.class, args);
	}
	@Bean
	public Consumer<Message<Employee_Changes>> consume() {
		return message -> {

			LOGGER.info("Moin Employe  : {} has Changed " , message.getPayload().getEmployee());
			int size =  message.getPayload().getChangesList().size() ;
			for ( int i = 0 ; i < size ; i++ ){
				LOGGER.info("Employee Changed Attribute {} " , message.getPayload().getChangesList().get(i));
			}
			LOGGER.info("New message received: '{}', partition key: {}, sequence number: {}, offset: {}, enqueued time: {}",

					message.getPayload(),
					message.getHeaders().get(EventHubsHeaders.PARTITION_KEY),
					message.getHeaders().get(EventHubsHeaders.SEQUENCE_NUMBER),
					message.getHeaders().get(EventHubsHeaders.OFFSET),
					message.getHeaders().get(EventHubsHeaders.ENQUEUED_TIME)
			);

		};
	}


//	@Bean
//	public Function<Message<UserChange> , Message<UserChange>> processing (){
//		return message -> {
//
//			LOGGER.info("Moin : {}" , message.getPayload().getUser());
//			int size =  message.getPayload().getChangedAttributes().size() ;
//			for ( int i = 0 ; i < size ; i++ ){
//				LOGGER.info("User Details {} " , message.getPayload().getChangedAttributes().get(i));
//			}
//
//			return message;
//		};
//


//	}

}
