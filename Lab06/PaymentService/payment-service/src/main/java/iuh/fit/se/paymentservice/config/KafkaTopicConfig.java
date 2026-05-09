package iuh.fit.se.paymentservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic bookingEventsTopic(KafkaTopicProperties properties) {
        return TopicBuilder.name(properties.getBookingEvents())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentEventsTopic(KafkaTopicProperties properties) {
        return TopicBuilder.name(properties.getPaymentEvents())
                .partitions(3)
                .replicas(1)
                .build();
    }
}
