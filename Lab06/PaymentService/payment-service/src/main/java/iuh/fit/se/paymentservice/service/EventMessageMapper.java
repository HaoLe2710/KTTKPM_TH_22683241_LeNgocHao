package iuh.fit.se.paymentservice.service;

import iuh.fit.se.paymentservice.exception.InvalidEventException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class EventMessageMapper {

    private final ObjectMapper objectMapper;
    private final Validator validator;

    public <T> T readAndValidate(String payload, Class<T> targetType) {
        try {
            T event = objectMapper.readValue(payload, targetType);
            validate(event);
            return event;
        } catch (Exception exception) {
            throw new InvalidEventException("Failed to deserialize event payload", exception);
        }
    }

    public String write(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception exception) {
            throw new InvalidEventException("Failed to serialize event payload", exception);
        }
    }

    private <T> void validate(T event) {
        Set<ConstraintViolation<T>> violations = validator.validate(event);
        if (violations.isEmpty()) {
            return;
        }

        String message = violations.stream()
                .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                .collect(Collectors.joining(", "));

        throw new InvalidEventException("Event validation failed: " + message);
    }
}
