package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplIntegrationTest {

    @Autowired
    private final UserServiceImpl userService;

    @Autowired
    private final UserRepository userRepository;

    private User user1;

    @BeforeEach
    void fillDataBase() {
        user1 = userRepository.save(User.builder().name("user1").email("user1@yandex.ru").build());
    }

    @AfterEach
    void clearDataBase() {
        userRepository.deleteAll();
    }

    @Test
    void addUserTest() {
        UserDto newUserDto = UserDto.builder().name("user").email("user@yandex.ru").build();
        UserDto expectedUserDto = UserDto.builder().name("user").email("user@yandex.ru").build();

        UserDto actualUserDto = userService.save(newUserDto);
        expectedUserDto.setId(actualUserDto.getId());

        assertThat(actualUserDto, equalTo(expectedUserDto));
    }

    @Test
    void updateUserTest() {
        UserDto toUpdateUserDto = UserDto.builder().name("user1-updated").build();
        UserDto expectedUserDto = UserDto.builder()
                .id(user1.getId())
                .name("user1-updated")
                .email(user1.getEmail())
                .build();

        UserDto actualUserDto = userService.update(user1.getId(), toUpdateUserDto);

        assertThat(actualUserDto, equalTo(expectedUserDto));
    }

    @Test
    void getAllUsersTest() {
        UserDto expectedUserDto = UserDto.builder()
                .id(user1.getId())
                .name(user1.getName())
                .email(user1.getEmail())
                .build();

        Collection<UserDto> actualList = userService.getAll();

        assertThat(actualList, equalTo(List.of(expectedUserDto)));
    }

    @Test
    void getUserByIdTest() {
        UserDto expectedUserDto = UserDto.builder()
                .id(user1.getId())
                .name(user1.getName())
                .email(user1.getEmail())
                .build();

        UserDto actualUserDto = userService.get(user1.getId());

        assertThat(actualUserDto, equalTo(expectedUserDto));
    }

}
