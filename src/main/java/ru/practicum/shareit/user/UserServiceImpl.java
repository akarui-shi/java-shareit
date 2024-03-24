package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.NotFoundDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.DuplicateEmailException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User save(UserDto user) {
        if (userRepository.findByEmail(user.getEmail()) == null) {
            return userRepository.save(UserMapper.fromDto(user));
        }
        throw new DuplicateEmailException("User with email " + user.getEmail() + " already exists");
    }

    @Override
    public User get(long id) {
        return userRepository.findById(id);
    }

    @Override
    public User update(long id, UserDto user) {
        if (userRepository.findById(id) != null) {
            if (user.getEmail() != null) {
                if (user.getEmail().equals(userRepository.findById(id).getEmail())) {
                    return userRepository.update(id, UserMapper.fromDto(user));
                } else if (userRepository.findByEmail(user.getEmail()) == null) {
                    return userRepository.update(id, UserMapper.fromDto(user));
                } else throw new DuplicateEmailException("User with email " + user.getEmail() + " already exists");
            } else return userRepository.update(id, UserMapper.fromDto(user));
        }
        throw new NotFoundDataException("User with id " + id + " not found");
    }

    @Override
    public void delete(long id) {
        userRepository.delete(id);
    }

    @Override
    public Collection<User> getAll() {
        return userRepository.findAll();
    }
}
