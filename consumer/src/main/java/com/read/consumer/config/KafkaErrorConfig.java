package com.read.consumer.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.validate.ValidationException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Headers;

import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Configuration
@EnableKafka
public class KafkaErrorConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template, MeterRegistry meterRegistry) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(template,
                        (record, ex) ->
                                new TopicPartition(record.topic() + ".DLT", record.partition())
                );

        recoverer.setHeadersFunction((record, ex) -> {

            Headers headers = new RecordHeaders();

            headers.add("x-failed-at",
                    Instant.now().toString().getBytes(StandardCharsets.UTF_8));

            headers.add("x-consumer-service",
                    "read-service".getBytes(StandardCharsets.UTF_8));

            headers.add("x-original-topic",
                    record.topic().getBytes(StandardCharsets.UTF_8));

            headers.add("x-original-offset",
                    String.valueOf(record.offset()).getBytes(StandardCharsets.UTF_8));

            headers.add("x-original-partition",
                    String.valueOf(record.partition()).getBytes(StandardCharsets.UTF_8));

            return headers;
        });

//        FixedBackOff backOff = new FixedBackOff(1000L, 3);
        BackOff backOff = new ExponentialBackOffWithJitter(
                1000L,   // initial - 1s
                2.0,     // multiplier
                10000L,  // max interval - 10s
                3        // retries
        );
        DefaultErrorHandler errorHandler =
                new DefaultErrorHandler(recoverer, backOff);

        errorHandler.addNotRetryableExceptions(
                IllegalArgumentException.class,
                ValidationException.class
        );

        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            if (deliveryAttempt > 1) {
                meterRegistry.counter("kafka.retry.attempts").increment();
            }
        });
        return errorHandler;
    }
}