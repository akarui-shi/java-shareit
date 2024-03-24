package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.NotFoundDataException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<Item> getItemsByUser(long userId) {
        if (userRepository.findById(userId) != null) {
            return itemRepository.findAllByUserId(userId);
        }
        throw new NotFoundDataException("User with id " + userId + " not found");
    }

    @Override
    public Item addNewItem(long userId, ItemDto item) {
        if (userRepository.findById(userId) != null) {
            return itemRepository.save(userId, ItemMapper.fromDto(item));
        }
        throw new NotFoundDataException("User with id " + userId + " not found");
    }

    @Override
    public Item getItem(long userId, long id) {
        if (userRepository.findById(userId) != null && itemRepository.findById(id) != null) {
            return itemRepository.findByIdAndUserId(userId, id);
        }
        throw new NotFoundDataException("Item by owner id " + id + " with id " + id + " not found");
    }

    @Override
    public Item updateItem(long userId, long id, ItemDto item) {
        if (itemRepository.findById(id) != null && itemRepository.findById(id).getOwnerId() == userId) {
            return itemRepository.updateItem(userId, id, ItemMapper.fromDto(item));
        }
        throw new NotFoundDataException("Item by owner id " + id + " with id " + id + " not found");
    }

    @Override
    public List<Item> searchItemsByKeyword(String keyword) {
        if (keyword.isBlank()) {
            return List.of();
        }
        return itemRepository.searchItemsByKeyword(keyword);
    }
}
