package com.read.consumer.config;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Headers;

import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Configuration
@EnableKafka
public class KafkaErrorConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {

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

        FixedBackOff backOff = new FixedBackOff(1000L, 3);

        return new DefaultErrorHandler(recoverer, backOff);
    }
}