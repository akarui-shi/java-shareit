package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.NotFoundDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.DuplicateEmailException;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto save(UserDto user) {
        try {
            return UserMapper.toDto(userRepository.save(UserMapper.fromDto(user)));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("User with email " + user.getEmail() + " already exists");
        }
    }

    @Override
    public UserDto get(long id) {
        return UserMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundDataException("User with id "  + id + " not found")));
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundDataException("No user found with id = " + id));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        try {
            return UserMapper.toDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("User with email " + user.getEmail() + " already exists");
        }
    }

    @Override
    public void delete(long id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
        }
        throw new NotFoundDataException("User with id "  + id + " not found");
    }

    @Override
    public Collection<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }
}
