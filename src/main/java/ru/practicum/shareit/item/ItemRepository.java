package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> findAllByUserId(long userId);

    Item save(long userId, Item item);

    Item findByIdAndUserId(long userId, long id);

    Item updateItem(long userId, long id, Item item);

    List<Item> searchItemsByKeyword(String keyword);

    Item findById(long id);
}
