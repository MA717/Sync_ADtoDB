package com.example.ADDB.EventConfiguration;


import com.example.ADDB.Entity.Employee;
import com.example.ADDB.Entity.Employee_Changes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Collection;
import java.util.function.Supplier;

@Slf4j
@Configuration
public class EventProducerConfiguration {


    @Bean
    public Sinks.Many<Message<String>> many() {
        return Sinks.many().unicast().onBackpressureBuffer();
    }


    @Bean
    public Sinks.Many<Message<Employee_Changes>> manyChanged() {
        return Sinks.many().unicast().onBackpressureBuffer();
    }

    @Bean
    public Sinks.Many<Message<Collection<Employee>>> employeeBucket() {
        return Sinks.many().unicast().onBackpressureBuffer();
    }

    @Bean
    public Supplier<Flux<Message<String>>> supply(Sinks.Many<Message<String>> many) {
        return () -> many.asFlux()
                .doOnNext(m -> log.info("Manually sending message {}", m))
                .doOnError(t -> log.error("Error encountered", t));
    }

    @Bean
    public Supplier<Flux<Message<Employee_Changes>>> supplyChange(Sinks.Many<Message<Employee_Changes>> manyChanged) {
        return () -> manyChanged.asFlux()
                .doOnNext(m -> log.info("Manually sending message {}", m))
                .doOnError(t -> log.error("Error encountered", t));

    }


    @Bean
    public Supplier<Flux<Message<Collection<Employee>>>> supplyEmployees(Sinks.Many<Message<Collection<Employee>>> employeeBucket) {
        return () -> employeeBucket.asFlux()
                .doOnNext(m -> log.info("Manually sending all Employees {}", m))
                .doOnError(t -> log.error("Error encountered", t));
    }


}