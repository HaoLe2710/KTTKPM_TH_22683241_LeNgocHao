package iuh.fit.se.paymentservice.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.payment")
public class PaymentProperties {

    @Min(0)
    @Max(100)
    private int successRate = 0;
}
