package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long id);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(long bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(long bookerId, LocalDateTime start);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start < :end AND b.end > :start " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerIdAndTimeRangeOrderByStartDesc(@Param("bookerId") long bookerId,
                                                                @Param("start") LocalDateTime start,
                                                                @Param("end") LocalDateTime end);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long id, Status status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long id);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(long bookerId, LocalDateTime start);

    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(long bookerId, LocalDateTime start);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :bookerId " +
            "AND b.start < :end AND b.end > :start " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByOwnerIdAndTimeRangeOrderByStartDesc(@Param("bookerId") long bookerId,
                                                               @Param("start") LocalDateTime start,
                                                               @Param("end") LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long id, Status status);

    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusIsAndEndIsBeforeOrderByEndDesc(long userId, long itemId, Status status, LocalDateTime  now);

    Optional<Booking> findFirstByItemIdAndStartIsBeforeAndStatusOrderByStartDesc(long itemId, LocalDateTime start, Status status);

    Optional<Booking> findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(long itemId, LocalDateTime start, Status status);
}

