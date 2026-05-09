package iuh.fit.se.booking_service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import iuh.fit.se.booking_service.entity.converter.StringListJsonConverter;
import java.util.List;
import org.junit.jupiter.api.Test;

class StringListJsonConverterTest {

    private final StringListJsonConverter converter = new StringListJsonConverter();

    @Test
    void shouldSerializeAndDeserializeSeatsAsJson() {
        List<String> seats = List.of("A1", "A2");

        String json = converter.convertToDatabaseColumn(seats);
        List<String> restoredSeats = converter.convertToEntityAttribute(json);

        assertEquals("[\"A1\",\"A2\"]", json);
        assertEquals(seats, restoredSeats);
    }
}
