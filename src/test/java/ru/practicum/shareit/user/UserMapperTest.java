package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UserMapperTest {

    @Test
    void UserToDtoTest() {
        User user = User.builder().id(1L).name("user").email("user@yandex.ru").build();
        UserDto expectedUserDto = UserDto.builder().id(1L).name("user").email("user@yandex.ru").build();
        UserDto actualUserDto = UserMapper.toDto(user);

        assertThat(actualUserDto, equalTo(expectedUserDto));
    }

    @Test
    void UserFromDtoTest() {
        UserDto userDto1 = UserDto.builder().id(1L).name("user").email("user@yandex.ru").build();
        UserDto userDto2 = UserDto.builder().name("user").email("user@yandex.ru").build();
        User expectedUser1 = User.builder().id(1L).name("user").email("user@yandex.ru").build();
        User expectedUser2 = User.builder().id(0L).name("user").email("user@yandex.ru").build();

        User actualUser1 = UserMapper.fromDto(userDto1);
        User actualUser2 = UserMapper.fromDto(userDto2);

        assertThat(actualUser1.getId(), equalTo(expectedUser1.getId()));
        assertThat(actualUser1.getName(), equalTo(expectedUser1.getName()));
        assertThat(actualUser1.getEmail(), equalTo(expectedUser1.getEmail()));
        assertThat(actualUser2.getId(), equalTo(expectedUser2.getId()));
        assertThat(actualUser2.getName(), equalTo(expectedUser2.getName()));
        assertThat(actualUser2.getEmail(), equalTo(expectedUser2.getEmail()));

    }
}
