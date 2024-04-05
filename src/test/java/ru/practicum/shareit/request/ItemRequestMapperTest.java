package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ItemRequestMapperTest {

    @Test
    void ItemRequestToDtoTest() {
        User requester = User.builder().id(2L).name("requester").email("requester@yandex.ru").build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("request")
                .requester(requester)
                .created(LocalDateTime.now())
                .build();

        ItemRequestDto expectedItemRequestDto1 = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(itemRequest.getRequester().getId())
                .created(itemRequest.getCreated())
                .items(List.of())
                .build();

        ItemRequestDto actualItemRequestDto1 = ItemRequestMapper.toDto(itemRequest);

        assertThat(actualItemRequestDto1, equalTo(expectedItemRequestDto1));
    }

    @Test
    void ItemRequestFromDtoTest() {
        User requester = User.builder().id(2L).name("requester").email("requester@yandex.ru").build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("request")
                .created(LocalDateTime.now())
                .build();

        ItemRequest expectedItemRequest = ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requester(requester)
                .created(itemRequestDto.getCreated())
                .build();

        ItemRequest actualItemRequest = ItemRequestMapper.fromDto(itemRequestDto, requester);

        assertThat(actualItemRequest.getId(), equalTo(expectedItemRequest.getId()));
        assertThat(actualItemRequest.getDescription(), equalTo(expectedItemRequest.getDescription()));
        assertThat(actualItemRequest.getRequester().getId(), equalTo(expectedItemRequest.getRequester().getId()));
        assertThat(actualItemRequest.getCreated(), equalTo(expectedItemRequest.getCreated()));
    }
}
