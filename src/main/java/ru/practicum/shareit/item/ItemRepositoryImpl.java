package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private int generateId = 0;

    @Override
    public List<Item> findAllByUserId(long userId) {
        return items.values().stream().filter(item -> item.getOwnerId() == userId).collect(Collectors.toList());
    }

    @Override
    public Item save(long userId, Item item) {
        item.setId(++generateId);
        item.setOwnerId(userId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item findByIdAndUserId(long userId, long id) {
        return items.get(id);
    }

    @Override
    public Item updateItem(long userId, long id, Item item) {
        Item oldItem = items.get(id);
        item.setId(id);
        item.setName(item.getName() == null ? oldItem.getName() : item.getName());
        item.setDescription(item.getDescription() == null ? oldItem.getDescription() : item.getDescription());
        item.setAvailable(item.getAvailable() == null ? oldItem.getAvailable() : item.getAvailable());
        item.setOwnerId(userId);
        items.put(id, item);
        return item;
    }

    @Override
    public List<Item> searchItemsByKeyword(String keyword) {
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().equals(keyword.toLowerCase())
                        || item.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public Item findById(long id) {
        return items.getOrDefault(id, null);
    }
}
