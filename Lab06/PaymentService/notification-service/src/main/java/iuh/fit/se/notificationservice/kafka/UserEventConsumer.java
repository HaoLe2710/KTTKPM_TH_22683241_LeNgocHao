package iuh.fit.se.notificationservice.kafka;

import iuh.fit.se.notificationservice.enums.EventType;
import iuh.fit.se.notificationservice.event.UserRegisteredEvent;
import iuh.fit.se.notificationservice.exception.InvalidEventException;
import iuh.fit.se.notificationservice.service.EventMessageMapper;
import iuh.fit.se.notificationservice.service.NotificationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final EventMessageMapper eventMessageMapper;
    private final NotificationLogService notificationLogService;

    @KafkaListener(
            topics = "${app.kafka.topics.user-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUserRegistered(
            String payload,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key) {
        try {
            UserRegisteredEvent event = eventMessageMapper.readAndValidate(payload, UserRegisteredEvent.class);

            if (event.getEventType() != EventType.USER_REGISTERED) {
                throw new InvalidEventException("Unexpected event type: " + event.getEventType());
            }

            notificationLogService.handleUserRegistered(event);
            log.info("Notification service processed USER_REGISTERED with key={}", key);
        } catch (InvalidEventException exception) {
            log.error("Ignoring invalid user event payload={}", payload, exception);
        } catch (Exception exception) {
            log.error("Unexpected error while processing user event payload={}", payload, exception);
        }
    }
}
