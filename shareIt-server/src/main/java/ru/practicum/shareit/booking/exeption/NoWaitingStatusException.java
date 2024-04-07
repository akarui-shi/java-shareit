package ru.practicum.shareit.booking.exeption;

public class NoWaitingStatusException extends RuntimeException {
    public NoWaitingStatusException(String message) {
        super(message);
    }
}
