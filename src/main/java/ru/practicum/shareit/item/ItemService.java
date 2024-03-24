package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getItemsByUser(long userId);

    Item addNewItem(long userId, ItemDto item);

    Item getItem(long userId, long id);

    Item updateItem(long userId, long id, ItemDto item);

    List<Item> searchItemsByKeyword(String keyword);
}
