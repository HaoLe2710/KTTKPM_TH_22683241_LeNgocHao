package iuh.fit.se.paymentservice.kafka;

import iuh.fit.se.paymentservice.enums.EventType;
import iuh.fit.se.paymentservice.event.BookingCreatedEvent;
import iuh.fit.se.paymentservice.exception.InvalidEventException;
import iuh.fit.se.paymentservice.service.EventMessageMapper;
import iuh.fit.se.paymentservice.service.PaymentProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingEventConsumer {

    private final EventMessageMapper eventMessageMapper;
    private final PaymentProcessorService paymentProcessorService;

    @KafkaListener(
            topics = "${app.kafka.topics.booking-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeBookingCreated(String payload, @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key) {
        try {
            BookingCreatedEvent event = eventMessageMapper.readAndValidate(payload, BookingCreatedEvent.class);

            if (event.getEventType() != EventType.BOOKING_CREATED) {
                throw new InvalidEventException("Unexpected event type: " + event.getEventType());
            }

            log.info("Received BOOKING_CREATED event from topic for booking #{} with key={}", event.getBookingId(), key);
            paymentProcessorService.processBookingCreated(event);
        } catch (InvalidEventException exception) {
            log.error("Ignoring invalid booking event payload={}", payload, exception);
        } catch (Exception exception) {
            log.error("Unexpected error while processing booking event payload={}", payload, exception);
        }
    }
}
