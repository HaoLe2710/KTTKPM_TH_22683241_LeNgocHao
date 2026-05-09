package iuh.fit.se.paymentservice.service;

import iuh.fit.se.paymentservice.config.KafkaTopicProperties;
import iuh.fit.se.paymentservice.event.BookingFailedEvent;
import iuh.fit.se.paymentservice.event.PaymentCompletedEvent;
import iuh.fit.se.paymentservice.exception.EventPublishException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTopicProperties topicProperties;
    private final EventMessageMapper eventMessageMapper;

    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        sendPaymentEvent(event.getBookingId().toString(), event, event.getEventType().name());
    }

    public void publishBookingFailed(BookingFailedEvent event) {
        sendPaymentEvent(event.getBookingId().toString(), event, event.getEventType().name());
    }

    private void sendPaymentEvent(String key, Object event, String eventType) {
        try {
            String payload = eventMessageMapper.write(event);
            SendResult<String, String> result = kafkaTemplate
                    .send(topicProperties.getPaymentEvents(), key, payload)
                    .get(10, TimeUnit.SECONDS);

            log.info(
                    "Published {} to topic={} for booking #{} at partition={}, offset={}",
                    eventType,
                    topicProperties.getPaymentEvents(),
                    key,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
            );
        } catch (Exception exception) {
            throw new EventPublishException("Failed to publish event " + eventType, exception);
        }
    }
}
