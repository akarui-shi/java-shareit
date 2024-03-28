package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.NotFoundDataException;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getItemsByUser(long userId) {
        if (userRepository.findById(userId) != null) {
            return itemRepository.findAllByUserId(userId).stream()
                    .map(ItemMapper::toDto)
                    .collect(Collectors.toList());
        }
        throw new NotFoundDataException("User with id " + userId + " not found");
    }

    @Override
    public ItemDto addNewItem(long userId, ItemDto item) {
        if (userRepository.findById(userId) != null) {
            return ItemMapper.toDto(itemRepository.save(userId, ItemMapper.fromDto(item)));
        }
        throw new NotFoundDataException("User with id " + userId + " not found");
    }

    @Override
    public ItemDto getItem(long userId, long id) {
        if (userRepository.findById(userId) != null && itemRepository.findById(id) != null) {
            return ItemMapper.toDto(itemRepository.findByIdAndUserId(userId, id));
        }
        throw new NotFoundDataException("Item by owner id " + id + " with id " + id + " not found");
    }

    @Override
    public ItemDto updateItem(long userId, long id, ItemDto item) {
        if (itemRepository.findById(id) != null && itemRepository.findById(id).getOwnerId() == userId) {
            return ItemMapper.toDto(itemRepository.updateItem(userId, id, ItemMapper.fromDto(item)));
        }
        throw new NotFoundDataException("Item by owner id " + id + " with id " + id + " not found");
    }

    @Override
    public List<ItemDto> searchItemsByKeyword(String keyword) {
        if (keyword.isBlank()) {
            return List.of();
        }
        return itemRepository.searchItemsByKeyword(keyword).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}
