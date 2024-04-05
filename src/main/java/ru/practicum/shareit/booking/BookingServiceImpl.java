package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.exeption.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.exception.NotFoundDataException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto addBooking(long userId, NewBookingDto bookingDto) {
        if (userRepository.findById(userId).isPresent()) {
            bookingDto.setBooker(userRepository.findById(userId).get());
            if (itemRepository.findById(bookingDto.getItemId()).isPresent()) {
                Item item = itemRepository.findById(bookingDto.getItemId()).get();
                if (item.getOwner().getId() == userId) {
                    throw new NotBookingRelationException("User (id = " + userId + ") can't book item (id = "
                            + item.getId() + ") because he doesn't own it");
                }
                if (item.getAvailable()) {
                    if (isDateValid(bookingDto)) {
                            bookingDto.setStatus(Status.WAITING);
                        return BookingMapper.toDto(bookingRepository.save(BookingMapper.fromDto(bookingDto, item)));
                    } else throw new InvalidDateExeption("Invalid date");
                } else throw new AccessItemDeniedException("Item not available");
            }
            throw new NotFoundDataException("Item with id " + bookingDto.getItemId() + " not found");
        }
        throw new NotFoundDataException("User with id " + userId + " not found");
    }

    @Override
    public BookingDto update(long bookingId, long userId, boolean approved) {
        if (userRepository.findById(userId).isPresent()) {
            if (bookingRepository.findById(bookingId).isPresent()) {
                Booking booking = bookingRepository.findById(bookingId).get();
                if (booking.getItem().getOwner().getId() != userId) {
                    throw new NotBookingRelationException("User (id = " + userId + ") can't approve item (id = "
                            + booking.getItem().getId() + ") because not being owner.");
                }
                if (!booking.getStatus().equals(Status.WAITING)) {
                    throw new NoWaitingStatusException("Can't approve/reject not waiting booking.");
                }
                if (approved) {
                    booking.setStatus(Status.APPROVED);
                    return BookingMapper.toDto(bookingRepository.save(booking));
                } else {
                    booking.setStatus(Status.REJECTED);
                    return BookingMapper.toDto(bookingRepository.save(booking));
                }
            }
            throw new NotFoundDataException("Booking with id " + bookingId + " not found");
        }
        throw new NotFoundDataException("User with id " + userId + " not found");
    }

    @Override
    public BookingDto getBookingById(long bookingId, long userId) {
        if (userRepository.findById(userId).isPresent()) {
            if (bookingRepository.findById(bookingId).isPresent()) {
                Booking booking = bookingRepository.findById(bookingId).get();
                if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
                    return BookingMapper.toDto(booking);
                }
                throw new NotBookingRelationException("User (id = " + userId
                        + ") has no relation booker to booking (id = " + bookingId + ")");
            }
            throw new NotFoundDataException("Booking with id " + bookingId + " not found");
        }
        throw new NotFoundDataException("User with id " + userId + " not found");
    }

    @Override
    public List<BookingDto> getAllBookingsByUser(long userId, String state, long from, long size) {
        if (userRepository.findById(userId).isPresent()) {
            List<Booking> bookings;
            PageRequest pageRequest = PageRequest.of((int) (from / size), (int) size);
            switch (state) {
                case "ALL" :
                    bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest);
                    break;
                case "CURRENT" :
                    bookings = bookingRepository.findAllByBookerIdAndTimeRangeOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                    break;
                case "PAST" :
                    bookings = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                    break;
                case "FUTURE" :
                    bookings = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                    break;
                case "WAITING" :
                    bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pageRequest);
                    break;
                case "REJECTED" :
                    bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageRequest);
                    break;
                default:
                    throw new InvalidStateException("Unknown state: " + state);
            }
            return bookings.stream()
                    .map(BookingMapper::toDto)
                    .collect(Collectors.toList());
        }
        throw new NotFoundDataException("User with id " + userId + " not found");
    }

    @Override
    public List<BookingDto> getAllBookingsAllItemsByOwner(long userId, String state, long from, long size) {
        if (userRepository.findById(userId).isPresent()) {
            List<Booking> bookings;
            PageRequest pageRequest = PageRequest.of((int) (from / size), (int) size);
            switch (state) {
                case "ALL" :
                    bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, pageRequest);
                    break;
                case "CURRENT" :
                    bookings = bookingRepository.findAllByOwnerIdAndTimeRangeOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                    break;
                case "PAST" :
                    bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                    break;
                case "FUTURE" :
                    bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                    break;
                case "WAITING" :
                    bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pageRequest);
                    break;
                case "REJECTED" :
                    bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageRequest);
                    break;
                default:
                    throw new InvalidStateException("Unknown state: " + state);
            }
            return bookings.stream()
                    .map(BookingMapper::toDto)
                    .collect(Collectors.toList());
        }
        throw new NotFoundDataException("User with id " + userId + " not found");
    }

    private boolean isDateValid(NewBookingDto bookingDto) {
        if (bookingDto.getStart().isEqual(bookingDto.getEnd())
                || bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            return false;
        }
        return true;
    }
}
