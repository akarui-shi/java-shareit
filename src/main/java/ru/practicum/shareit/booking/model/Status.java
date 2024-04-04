package ru.practicum.shareit.booking.model;

public enum Status {
    WAITING, //новое бронирование
    APPROVED, //подтверждено владельцем
    REJECTED, //отклонено владельцем
    CANCELED //отменено создателем
}
