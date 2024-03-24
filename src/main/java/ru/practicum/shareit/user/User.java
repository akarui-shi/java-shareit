package ru.practicum.shareit.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class User {
    private long id;
    private String name;
    @NotEmpty
    @Email(message = "Неверный формат email")
    private String email;
}
