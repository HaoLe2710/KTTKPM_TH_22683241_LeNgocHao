package iuh.fit.se.notificationservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic paymentEventsTopic(KafkaTopicProperties properties) {
        return TopicBuilder.name(properties.getPaymentEvents())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userEventsTopic(KafkaTopicProperties properties) {
        return TopicBuilder.name(properties.getUserEvents())
                .partitions(3)
                .replicas(1)
                .build();
    }
}
