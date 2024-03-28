package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private int generateId = 0;

    @Override
    public User save(User user) {
        user.setId(++generateId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(long id) {
        return users.getOrDefault(id, null);
    }

    @Override
    public User findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User update(long id, User user) {
        User oldUser = users.get(id);
        user.setId(id);
        user.setName(user.getName() == null ? oldUser.getName() : user.getName());
        user.setEmail(user.getEmail() == null ? oldUser.getEmail() : user.getEmail());
        users.put(id, user);
        return user;
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

}
