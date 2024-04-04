package ru.practicum.shareit.booking.exeption;

public class InvalidStateException extends RuntimeException {

    public InvalidStateException(String message) {
        super(message);
    }

}
