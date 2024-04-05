package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService mockUserService;

    @Autowired
    private MockMvc mvc;

    private UserDto userDtoAddIn;
    private UserDto userDtoAddOut;
    private UserDto userDtoUpdateIn;
    private UserDto userDtoUpdateOut;

    @BeforeEach
    void setUp() {
        userDtoAddIn = UserDto.builder()
                .name("user")
                .email("user@yandex.ru")
                .build();
        userDtoAddOut = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@yandex.ru")
                .build();
        userDtoUpdateIn = UserDto.builder()
                .name("update")
                .build();
        userDtoUpdateOut = UserDto.builder()
                .id(1L)
                .name("update")
                .email("user@yandex.ru")
                .build();
    }

    @Test
    void addUserTest() throws Exception {
        when(mockUserService.save(userDtoAddIn))
                .thenReturn(userDtoAddOut);
        mvc.perform(post("/users")
                    .content(mapper.writeValueAsString(userDtoAddIn))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoAddOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoAddOut.getName())))
                .andExpect(jsonPath("$.email", is(userDtoAddOut.getEmail())));
    }

    @Test
    void updateUserTest() throws Exception {
        when(mockUserService.update(1L, userDtoUpdateIn))
                .thenReturn(userDtoUpdateOut);
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDtoUpdateIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoUpdateOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoUpdateOut.getName())))
                .andExpect(jsonPath("$.email", is(userDtoUpdateOut.getEmail())));
    }

    @Test
    void getAllUsersTest() throws Exception {
        when(mockUserService.getAll())
                .thenReturn(List.of(userDtoUpdateOut));
        mvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(userDtoUpdateOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDtoUpdateOut.getName())))
                .andExpect(jsonPath("$[0].email", is(userDtoUpdateOut.getEmail())));
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(mockUserService.get(1L))
                .thenReturn(userDtoUpdateOut);
        mvc.perform(get("/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoUpdateOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoUpdateOut.getName())))
                .andExpect(jsonPath("$.email", is(userDtoUpdateOut.getEmail())));
    }

    @Test
    void deleteUserByIdTest() throws Exception {
        mvc.perform(delete("/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(mockUserService).delete(1L);
    }

    @Test
    @DisplayName("Некорректный формат email")
    void invalidEmailFormatTest() throws Exception {
        UserDto userDtoAddIn = UserDto.builder()
                .email("invalid_email_format") // Некорректный формат email
                .name("user")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoAddIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
