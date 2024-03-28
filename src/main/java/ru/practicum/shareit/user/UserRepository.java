package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserRepository {
    User save(User user);

    User findById(long id);

    User findByEmail(String email);

    User update(long id, User user);

    void delete(long id);

    Collection<User> findAll();
}
