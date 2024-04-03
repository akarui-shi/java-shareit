package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class BookingMapper {
    public static BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setStatus(booking.getStatus());
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setItem(booking.getItem());
        dto.setBooker(booking.getBooker());
        return dto;
    }

    public static BookingItemDto toDto(Booking booking, long bookerId) {
        BookingItemDto dto = new BookingItemDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setBookerId(bookerId);
        return dto;
    }

    public static Booking fromDto(BookingDto dto, Item item) {
        Booking booking = new Booking();
        booking.setStatus(dto.getStatus());
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItem(item);
        booking.setBooker(dto.getBooker());
        return booking;
    }

    public static Booking fromDto(NewBookingDto dto, Item item) {
        Booking booking = new Booking();
        booking.setStatus(dto.getStatus());
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItem(item);
        booking.setBooker(dto.getBooker());
        return booking;
    }
}