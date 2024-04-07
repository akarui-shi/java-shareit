package ru.practicum.shareit.booking.exeption;

public class AccessItemDeniedException extends RuntimeException {
    public AccessItemDeniedException(String message) {
        super(message);
    }
}
