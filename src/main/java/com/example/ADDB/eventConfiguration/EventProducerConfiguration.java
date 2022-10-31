package com.example.ADDB.eventConfiguration;

import io.cloudevents.CloudEvent;
import io.cloudevents.spring.messaging.CloudEventMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

@Slf4j
@Configuration
public class EventProducerConfiguration {


    @Bean
    public Sinks.Many<CloudEvent> manyChanged() {
        return Sinks.many().unicast().onBackpressureBuffer();
    }


    @Bean
    public Supplier<Flux<CloudEvent>> supplyChange(Sinks.Many<CloudEvent> manyChanged) {
        return () -> manyChanged.asFlux()
                .doOnNext(m -> log.info("Manually sending message {}", m))
                .doOnError(t -> log.error("Error encountered", t));


    }

    @Bean
    public CloudEventMessageConverter cloudEventMessageConverter() {

        return new CloudEventMessageConverter();
    }


}