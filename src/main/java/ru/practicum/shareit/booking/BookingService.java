package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import java.util.List;

public interface BookingService {

    BookingDto update(long bookingId, long userId, boolean approved);

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getAllBookingsByUser(long userId, String state, long from, long size);

    List<BookingDto> getAllBookingsAllItemsByOwner(long userId, String state, long from, long size);

    BookingDto addBooking(long userId, NewBookingDto bookingDto);
}
