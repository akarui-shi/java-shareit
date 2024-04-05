package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BookingMapperTest {
    @Test
    void bookingToDtoTest1() {
        User owner = User.builder().id(1L).name("owner").email("owner@yandex.ru").build();
        User booker = User.builder().id(2L).name("booker").email("booker@yandex.ru").build();
        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(Item.builder().id(item.getId()).name(item.getName()).build())
                .booker(User.builder().id(booker.getId()).build())
                .build();

        BookingDto actualBookingDto = BookingMapper.toDto(booking);
        assertThat(actualBookingDto.getId(), equalTo(expectedBookingDto.getId()));
        assertThat(actualBookingDto.getStart(), equalTo(expectedBookingDto.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(expectedBookingDto.getEnd()));
        assertThat(actualBookingDto.getStatus(), equalTo(expectedBookingDto.getStatus()));
        assertThat(actualBookingDto.getItem().getId(), equalTo(expectedBookingDto.getItem().getId()));
        assertThat(actualBookingDto.getItem().getName(), equalTo(expectedBookingDto.getItem().getName()));
        assertThat(actualBookingDto.getBooker().getId(), equalTo(expectedBookingDto.getBooker().getId()));
    }

    @Test
    void bookingToDtoTest2() {
        User owner = User.builder().id(1L).name("owner").email("owner@yandex.ru").build();
        User booker = User.builder().id(2L).name("booker").email("booker@yandex.ru").build();
        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        BookingItemDto expectedBookingDtoItem = BookingItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();

        BookingDto actualBookingDto = BookingMapper.toDto(booking);

        assertThat(actualBookingDto.getId(), equalTo(expectedBookingDtoItem.getId()));
        assertThat(actualBookingDto.getBooker().getId(), equalTo(expectedBookingDtoItem.getBookerId()));
        assertThat(actualBookingDto.getStart(), equalTo(expectedBookingDtoItem.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(expectedBookingDtoItem.getEnd()));
    }

    @Test
    void bookingFromDtoTest1() {
        User owner = User.builder().id(1L).name("owner").email("owner@yandex.ru").build();
        User booker = User.builder().id(2L).name("booker").email("booker@yandex.ru").build();
        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        NewBookingDto newBookingDto = NewBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(booker)
                .status(Status.WAITING)
                .build();

        Booking expectedBooking = Booking.builder()
                .id(0L)
                .start(newBookingDto.getStart())
                .end(newBookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        Booking actualBooking = BookingMapper.fromDto(newBookingDto, item);

        assertThat(actualBooking.getId(), equalTo(expectedBooking.getId()));
        assertThat(actualBooking.getStart(), equalTo(expectedBooking.getStart()));
        assertThat(actualBooking.getEnd(), equalTo(expectedBooking.getEnd()));
        assertThat(actualBooking.getItem().getId(), equalTo(expectedBooking.getItem().getId()));
        assertThat(actualBooking.getBooker().getId(), equalTo(expectedBooking.getBooker().getId()));
        assertThat(actualBooking.getStatus(), equalTo(expectedBooking.getStatus()));

    }

    @Test
    void bookingFromDtoTest2() {
        User owner = User.builder().id(1L).name("owner").email("owner@yandex.ru").build();
        User booker = User.builder().id(2L).name("booker").email("booker@yandex.ru").build();
        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        BookingDto bookingDto = BookingDto.builder()
                .id(0L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(booker)
                .status(Status.WAITING)
                .build();

        Booking expectedBooking = Booking.builder()
                .id(0L)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        Booking actualBooking = BookingMapper.fromDto(bookingDto, item);

        assertThat(actualBooking.getId(), equalTo(expectedBooking.getId()));
        assertThat(actualBooking.getStart(), equalTo(expectedBooking.getStart()));
        assertThat(actualBooking.getEnd(), equalTo(expectedBooking.getEnd()));
        assertThat(actualBooking.getItem().getId(), equalTo(expectedBooking.getItem().getId()));
        assertThat(actualBooking.getBooker().getId(), equalTo(expectedBooking.getBooker().getId()));
        assertThat(actualBooking.getStatus(), equalTo(expectedBooking.getStatus()));

    }

}
