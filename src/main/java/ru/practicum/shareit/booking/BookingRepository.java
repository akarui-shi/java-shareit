package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long id);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(long bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(long bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long id, Status status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long id);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(long bookerId, LocalDateTime start);

    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(long bookerId, LocalDateTime start);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long id, Status status);

    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusIsAndEndIsBeforeOrderByEndDesc(long userId, long itemId, Status status, LocalDateTime  now);

    Optional<Booking> findFirstByItemIdAndStartIsBeforeAndStatusOrderByStartDesc(long itemId, LocalDateTime start, Status status);

    Optional<Booking> findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(long itemId, LocalDateTime start, Status status);
}

