package iuh.fit.se.booking_service.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import iuh.fit.se.booking_service.event.BookingFailedEvent;
import iuh.fit.se.booking_service.event.EventType;
import iuh.fit.se.booking_service.event.PaymentCompletedEvent;
import iuh.fit.se.booking_service.exception.InvalidEventException;
import iuh.fit.se.booking_service.service.BookingService;
import iuh.fit.se.booking_service.service.EventMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final EventMessageMapper eventMessageMapper;
    private final BookingService bookingService;

    @KafkaListener(
            topics = "${app.kafka.payment-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePaymentEvent(
            String payload,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key) {
        try {
            JsonNode root = eventMessageMapper.readTree(payload);
            EventType eventType = EventType.valueOf(root.path("eventType").asText(""));

            switch (eventType) {
                case PAYMENT_COMPLETED -> {
                    PaymentCompletedEvent event = eventMessageMapper.readAndValidate(payload, PaymentCompletedEvent.class);
                    bookingService.handlePaymentCompleted(event);
                }
                case BOOKING_FAILED -> {
                    BookingFailedEvent event = eventMessageMapper.readAndValidate(payload, BookingFailedEvent.class);
                    bookingService.handleBookingFailed(event);
                }
                default -> throw new InvalidEventException("Unsupported event type: " + eventType);
            }

            log.info("Booking service processed event {} with key={}", eventType, key);
        } catch (IllegalArgumentException | InvalidEventException exception) {
            log.error("Ignoring invalid payment event payload={}", payload, exception);
        } catch (Exception exception) {
            log.error("Unexpected error while processing payment event payload={}", payload, exception);
        }
    }
}
