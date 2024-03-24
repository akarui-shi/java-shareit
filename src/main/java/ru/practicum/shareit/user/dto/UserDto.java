package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class UserDto {
    @NotEmpty
    @Email(message = "Неверный формат email")
    private String email;
    private String name;
}
