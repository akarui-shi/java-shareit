package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void addUserTest() {
        long userId = 1L;
        UserDto newUserDto = UserDto.builder().name("user").email("user@yandex.ru").build();
        User user = User.builder().id(userId).name("user").email("user@yandex.ru").build();
        UserDto expectedUserDto = UserDto.builder().id(userId).name("user").email("user@yandex.ru").build();

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto actualUserDto = userService.save(newUserDto);

        assertThat(actualUserDto, equalTo(expectedUserDto));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserTest() {
        long userId = 1L;
        UserDto toUpdateUserDto = UserDto.builder().name("user-updated").email("updated@yandex.ru").build();
        User user = User.builder().id(userId).name("user").email("user@yandex.ru").build();
        User updatedUser = User.builder().id(userId).name("user-updated").email("updated@yandex.ru").build();
        UserDto expectedUserDto = UserDto.builder().id(userId).name("user-updated").email("updated@yandex.ru").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto actualUserDto = userService.update(userId, toUpdateUserDto);

        assertThat(actualUserDto, equalTo(expectedUserDto));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getAllUsersTest() {
        long userId = 1;
        User user = User.builder().id(userId).name("user").email("user@yandex.ru").build();
        UserDto expectedUserDto = UserDto.builder().id(userId).name("user").email("user@yandex.ru").build();

        when(userRepository.findAll()).thenReturn(List.of(user));

        Collection<UserDto> actualUserDto = userService.getAll();

        assertThat(actualUserDto, equalTo(List.of(expectedUserDto)));
    }

    @Test
    void getUserByIdTest() {
        long userId = 1;
        User user = User.builder().id(userId).name("user").email("user@yandex.ru").build();
        UserDto expectedUserDto = UserDto.builder().id(userId).name("user").email("user@yandex.ru").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto actualUserDto = userService.get(userId);

        assertThat(actualUserDto, equalTo(expectedUserDto));
    }

}
