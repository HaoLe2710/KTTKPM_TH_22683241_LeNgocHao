package iuh.fit.se.notificationservice.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.kafka.topics")
public class KafkaTopicProperties {

    @NotBlank
    private String paymentEvents = "payment-events";

    @NotBlank
    private String userEvents = "user-events";
}
