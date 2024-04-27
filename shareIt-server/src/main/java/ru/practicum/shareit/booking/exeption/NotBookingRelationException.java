package ru.practicum.shareit.booking.exeption;

public class NotBookingRelationException  extends RuntimeException {
    public NotBookingRelationException(String message) {
        super(message);
    }
}
