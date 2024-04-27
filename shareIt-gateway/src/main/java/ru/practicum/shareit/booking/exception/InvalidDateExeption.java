package ru.practicum.shareit.booking.exception;

public class InvalidDateExeption extends RuntimeException {
    public InvalidDateExeption(String message) {
        super(message);
    }
}
