package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto save(UserDto user);

    UserDto get(long id);

    UserDto update(long id, UserDto user);

    void delete(long id);

    Collection<UserDto> getAll();
}
