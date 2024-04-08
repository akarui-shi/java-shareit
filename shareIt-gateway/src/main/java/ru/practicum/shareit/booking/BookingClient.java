package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.exception.InvalidDateExeption;
import ru.practicum.shareit.booking.exception.InvalidStateException;
import ru.practicum.shareit.booking.model.NewBookingDto;
import ru.practicum.shareit.client.BaseClient;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Map;

@Component
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";
    List<String> statuses = List.of("ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED");

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addBooking(long userId, NewBookingDto newBookingDto) {
        if (isDateValid(newBookingDto)) {
            return post("/", userId, newBookingDto);
        } else throw new InvalidDateExeption("Invalid date");
    }

    public ResponseEntity<Object> update(long bookingId, long userId, boolean approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved);
        return patch("/" + bookingId + "?approved=" + approved, userId, parameters);
    }

    public ResponseEntity<Object> getBookingById(long bookingId, long userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllBookingsByUser(
            long userId, String state, @PositiveOrZero long from, @Positive long size) {
        if (!statuses.contains(state)) {
            throw new InvalidStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size);
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getAllBookingsAllItemsByOwner(
            long userId, String state, @PositiveOrZero long from, @Positive long size) {
        if (!statuses.contains(state)) {
            throw new InvalidStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size);
        return get("/owner/?state={state}&from={from}&size={size}", userId, parameters);
    }

    private boolean isDateValid(NewBookingDto bookingDto) {
        if (bookingDto.getStart().isEqual(bookingDto.getEnd())
                || bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            return false;
        }
        return true;
    }
}
