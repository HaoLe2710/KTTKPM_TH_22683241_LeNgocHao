package iuh.fit.se.booking_service.service.impl;

import iuh.fit.se.booking_service.config.KafkaTopicProperties;
import iuh.fit.se.booking_service.entity.Booking;
import iuh.fit.se.booking_service.event.BookingCreatedEvent;
import iuh.fit.se.booking_service.exception.EventPublishingException;
import iuh.fit.se.booking_service.mapper.BookingMapper;
import iuh.fit.se.booking_service.service.BookingEventPublisher;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaBookingEventPublisher implements BookingEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaBookingEventPublisher.class);

    private final KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate;
    private final KafkaTopicProperties kafkaTopicProperties;
    private final BookingMapper bookingMapper;

    @Override
    public void publishBookingCreated(Booking booking) {
        BookingCreatedEvent event = bookingMapper.toCreatedEvent(booking);
        String topic = kafkaTopicProperties.getBookingTopic();
        String key = String.valueOf(booking.getId());

        try {
            SendResult<String, BookingCreatedEvent> sendResult = kafkaTemplate
                    .send(topic, key, event)
                    .get(10, TimeUnit.SECONDS);

            log.info(
                    "Published BOOKING_CREATED event: topic={}, key={}, partition={}, offset={}",
                    topic,
                    key,
                    sendResult.getRecordMetadata().partition(),
                    sendResult.getRecordMetadata().offset());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while publishing BOOKING_CREATED for bookingId={}", booking.getId(), ex);
            throw new EventPublishingException("Publishing BOOKING_CREATED bi gian doan", ex);
        } catch (Exception ex) {
            log.error("Failed to publish BOOKING_CREATED for bookingId={}", booking.getId(), ex);
            throw new EventPublishingException("Khong the publish BOOKING_CREATED", ex);
        }
    }
}
