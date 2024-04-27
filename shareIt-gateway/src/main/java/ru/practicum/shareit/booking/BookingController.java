package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.NewBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
											 @Valid @RequestBody NewBookingDto bookingDto) {
		log.info("Add new booking for user {} ", userId);
		return bookingClient.addBooking(userId, bookingDto);
	}


	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> update(@PathVariable long bookingId,
							 @RequestParam boolean approved,
							 @RequestHeader("X-Sharer-User-Id") long userId) {
		log.info("Update booking for user {} with item {}", userId, bookingId);
		return bookingClient.update(bookingId, userId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingById(@PathVariable long bookingId,
									 @RequestHeader("X-Sharer-User-Id") long userId) {
		log.info("Get booking for user {} with item {}", userId, bookingId);
		return bookingClient.getBookingById(bookingId, userId);
	}

	@GetMapping
	public ResponseEntity<Object> getAllBookingsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
												 @RequestParam(defaultValue = "ALL") String state,
												 @PositiveOrZero @RequestParam(defaultValue = "0") long from,
												 @Positive @RequestParam(defaultValue = "10") long size) {
		log.info("Get all bookings for user {} with state {}", userId, state);
		return bookingClient.getAllBookingsByUser(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllBookingsAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
														  @RequestParam(defaultValue = "ALL") String state,
														  @PositiveOrZero @RequestParam(defaultValue = "0") long from,
														  @Positive @RequestParam(defaultValue = "10") long size) {
		log.info("Get all bookings for user {} with state {}", userId, state);
		return bookingClient.getAllBookingsAllItemsByOwner(userId, state, from, size);
	}

}
