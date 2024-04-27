package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Getting all users: {}", userClient.getAll().toString());
        return userClient.getAll();
    }

    @PostMapping
    public ResponseEntity<Object> save(@Valid @RequestBody UserDto user) {
        log.info("Saving user {}", user);
        return userClient.save(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable long id) {
        log.info("Getting user with id {}", id);
        return userClient.get(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable long id,
                          @RequestBody UserDto user) {
        log.info("Updating user with id {} to {}", id, user);
        return userClient.update(id, user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Deleting user with id {}", id);
        userClient.delete(id);
    }

}