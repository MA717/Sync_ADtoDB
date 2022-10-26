package com.example.ADDB.EventConfiguration;


import com.example.ADDB.Entity.Employee_Changes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

@Slf4j
@Configuration
public class EventProducerConfiguration {


    @Bean
    public Sinks.Many<Message<Employee_Changes>> manyChanged() {
        return Sinks.many().unicast().onBackpressureBuffer();
    }


    @Bean
    public Supplier<Flux<Message<Employee_Changes>>> supplyChange(Sinks.Many<Message<Employee_Changes>> manyChanged) {
        return () -> manyChanged.asFlux()
                .doOnNext(m -> log.info("Manually sending message {}", m))
                .doOnError(t -> log.error("Error encountered", t));

    }


}