package com.example.ADDB.EventConfiguration;



import com.example.ADDB.Entity.Employee_Changes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

@Configuration
public class EventProducerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventProducerConfiguration.class);

    @Bean
    public Sinks.Many<Message<String>> many() {
        return Sinks.many().unicast().onBackpressureBuffer();
    }


    @Bean
    public Sinks.Many<Message<Employee_Changes>> manyChanged(){return Sinks.many().unicast().onBackpressureBuffer();}



    @Bean
    public Supplier<Flux<Message<String>>> supply(Sinks.Many<Message<String>> many) {
        return () -> many.asFlux()
                .doOnNext(m -> LOGGER.info("Manually sending message {}", m))
                .doOnError(t -> LOGGER.error("Error encountered", t));
    }



    @Bean
    public Supplier<Flux<Message<Employee_Changes>>> supplyChange ( Sinks.Many<Message<Employee_Changes>> manyChanged){
        return () -> manyChanged.asFlux()
                .doOnNext(m -> LOGGER.info("Manually sending message {}", m))
                .doOnError(t -> LOGGER.error("Error encountered", t));

    }
}