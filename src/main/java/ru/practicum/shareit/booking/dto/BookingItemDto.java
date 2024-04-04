package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingItemDto {
    private long id;

    private long bookerId;

    private LocalDateTime start;

    private LocalDateTime end;
}