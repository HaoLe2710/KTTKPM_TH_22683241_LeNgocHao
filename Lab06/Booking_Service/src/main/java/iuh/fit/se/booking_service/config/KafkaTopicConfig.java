package iuh.fit.se.booking_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic bookingEventsTopic(KafkaTopicProperties properties) {
        return TopicBuilder.name(properties.getBookingTopic())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentEventsTopic(KafkaTopicProperties properties) {
        return TopicBuilder.name(properties.getPaymentTopic())
                .partitions(3)
                .replicas(1)
                .build();
    }
}
