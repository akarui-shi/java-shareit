package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ItemMapperTest {

    @Test
    void itemToDtoTest() {
        User owner = User.builder().id(1L).name("owner").email("owner@yandex.ru").build();
        Item item1 = Item.builder()
                .id(1L)
                .name("item1")
                .description("description1")
                .available(true)
                .owner(owner)
                .build();

        ItemDto expectedItemDto1 = ItemDto.builder()
                .id(item1.getId())
                .name(item1.getName())
                .description(item1.getDescription())
                .available(item1.getAvailable())
                .build();

        ItemDto actualItemDto1 = ItemMapper.toDto(item1);

        assertThat(actualItemDto1, equalTo(expectedItemDto1));
    }

    @Test
    void itemFromDtoTest() {
        ItemDto itemDto1 = ItemDto.builder()
                .id(1L)
                .name("item1")
                .description("description1")
                .available(true)
                .build();

        Item expectedItem1 = Item.builder()
                .id(itemDto1.getId())
                .name(itemDto1.getName())
                .description(itemDto1.getDescription())
                .available(itemDto1.getAvailable())
                .build();

        Item actualItem1 = ItemMapper.fromDto(itemDto1);

        assertThat(actualItem1.getId(), equalTo(expectedItem1.getId()));
        assertThat(actualItem1.getName(), equalTo(expectedItem1.getName()));
        assertThat(actualItem1.getDescription(), equalTo(expectedItem1.getDescription()));
        assertThat(actualItem1.getAvailable(), equalTo(expectedItem1.getAvailable()));
    }

}
