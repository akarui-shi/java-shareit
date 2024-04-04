package ru.practicum.shareit.booking.exeption;

public class InvalidDateExeption extends RuntimeException {
    public InvalidDateExeption(String message) {
        super(message);
    }
}
