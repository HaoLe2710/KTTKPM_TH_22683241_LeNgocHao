package iuh.fit.se.booking_service.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaTopicProperties {

    @NotBlank(message = "Kafka booking topic must not be blank")
    private String bookingTopic;

    @NotBlank(message = "Kafka payment topic must not be blank")
    private String paymentTopic;
}
