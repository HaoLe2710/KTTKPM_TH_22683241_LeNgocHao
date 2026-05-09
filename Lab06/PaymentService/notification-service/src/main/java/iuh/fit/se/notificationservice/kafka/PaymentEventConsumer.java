package iuh.fit.se.notificationservice.kafka;

import iuh.fit.se.notificationservice.enums.EventType;
import iuh.fit.se.notificationservice.event.BookingFailedEvent;
import iuh.fit.se.notificationservice.event.PaymentCompletedEvent;
import iuh.fit.se.notificationservice.exception.InvalidEventException;
import iuh.fit.se.notificationservice.service.EventMessageMapper;
import iuh.fit.se.notificationservice.service.NotificationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final EventMessageMapper eventMessageMapper;
    private final NotificationLogService notificationLogService;

    @KafkaListener(
            topics = "${app.kafka.topics.payment-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePaymentEvent(String payload, @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key) {
        try {
            JsonNode root = eventMessageMapper.readTree(payload);
            EventType eventType = EventType.valueOf(root.path("eventType").asText(""));

            switch (eventType) {
                case PAYMENT_COMPLETED -> {
                    PaymentCompletedEvent event = eventMessageMapper.readAndValidate(payload, PaymentCompletedEvent.class);
                    notificationLogService.handlePaymentCompleted(event);
                }
                case BOOKING_FAILED -> {
                    BookingFailedEvent event = eventMessageMapper.readAndValidate(payload, BookingFailedEvent.class);
                    notificationLogService.handleBookingFailed(event);
                }
                default -> throw new InvalidEventException("Unsupported event type: " + eventType);
            }

            log.info("Notification service processed event {} with key={}", eventType, key);
        } catch (IllegalArgumentException | InvalidEventException exception) {
            log.error("Ignoring invalid payment event payload={}", payload, exception);
        } catch (Exception exception) {
            log.error("Unexpected error while processing payment event payload={}", payload, exception);
        }
    }
}
