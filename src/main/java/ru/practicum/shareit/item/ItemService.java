package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsByUser(long userId);

    ItemDto addNewItem(long userId, ItemDto item);

    ItemDto getItem(long userId, long id);

    ItemDto updateItem(long userId, long id, ItemDto item);

    List<ItemDto> searchItemsByKeyword(String keyword);
}
