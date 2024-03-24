package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> getAll() {
        log.info("Getting all users: {}", userService.getAll().toString());
        return userService.getAll();
    }

    @PostMapping
    public User save(@Valid @RequestBody UserDto user) {
        log.info("Saving user {}", user);
        return userService.save(user);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable long id) {
        log.info("Getting user with id {}", id);
        return userService.get(id);
    }

    @PatchMapping("/{id}")
    public User update(@PathVariable long id,
                       @RequestBody UserDto user) {
        log.info("Updating user with id {} to {}", id, user);
        return userService.update(id, user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Deleting user with id {}", id);
        userService.delete(id);
    }

}
