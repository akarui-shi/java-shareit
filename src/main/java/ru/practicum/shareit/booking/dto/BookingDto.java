package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

@Data
public class BookingDto {
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private User booker;
    private Status status;
}
