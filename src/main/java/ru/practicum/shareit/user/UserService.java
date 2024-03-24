package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    User save(UserDto user);

    User get(long id);

    User update(long id, UserDto user);

    void delete(long id);

    Collection<User> getAll();
}
