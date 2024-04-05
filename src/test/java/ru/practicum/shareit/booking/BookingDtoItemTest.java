package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoItemTest {

    @Autowired
    private JacksonTester<BookingItemDto> json;

    @Test
    void testBookingItemDto() throws Exception {
        BookingItemDto bookingDtoItem = BookingItemDto.builder()
                .id(2L)
                .bookerId(3L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        JsonContent<BookingItemDto> result = json.write(bookingDtoItem);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.bookerId");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo((int) bookingDtoItem.getId());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo((int) bookingDtoItem.getBookerId());
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDtoItem.getStart().format(DateTimeFormatter.ISO_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDtoItem.getEnd().format(DateTimeFormatter.ISO_DATE_TIME));
    }
}
