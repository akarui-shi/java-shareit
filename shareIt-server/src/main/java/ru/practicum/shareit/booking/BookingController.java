package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @Valid @RequestBody NewBookingDto bookingDto) {
        log.info("Add new booking for user {} ", userId);
        return bookingService.addBooking(userId, bookingDto);
    }


    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable long bookingId,
                             @RequestParam boolean approved,
                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Update booking for user {} with item {}", userId, bookingId);
        return bookingService.update(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get booking for user {} with item {}", userId, bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") long from,
                                                 @Positive @RequestParam(defaultValue = "10") long size) {
        log.info("Get all bookings for user {} with state {}", userId, state);
        return bookingService.getAllBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @RequestParam(defaultValue = "ALL") String state,
                                                          @PositiveOrZero @RequestParam(defaultValue = "0") long from,
                                                          @Positive @RequestParam(defaultValue = "10") long size) {
        log.info("Get all bookings for user {} with state {}", userId, state);
        return bookingService.getAllBookingsAllItemsByOwner(userId, state, from, size);
    }

}
