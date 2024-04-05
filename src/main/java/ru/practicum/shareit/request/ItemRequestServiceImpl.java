package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.NotFoundDataException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addItemRequest(long userId, ItemRequestDto itemRequestDto) {
        if (userRepository.findById(userId).isPresent()) {
            User user = userRepository.findById(userId).get();
            itemRequestDto.setCreated(LocalDateTime.now());
            ItemRequest itemRequest = ItemRequestMapper.fromDto(itemRequestDto, user);
            return ItemRequestMapper.toDto(itemRequestRepository.save(itemRequest));
        }
        throw new NotFoundDataException("User with id " + userId + " not found");
    }

    @Override
    public List<ItemRequestDto> getItemRequestsByOwner(long userId) {
        if (userRepository.findById(userId).isPresent()) {
            List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
            List<Long> itemRequestIds = itemRequests.stream()
                    .map(ItemRequest::getId)
                    .collect(Collectors.toList());
            List<ItemRequestDto> itemRequestDtos = itemRequests.stream()
                    .map(ItemRequestMapper::toDto)
                    .collect(Collectors.toList());
            List<Item> items = itemRepository.findAllByRequestIdIn(itemRequestIds);
            for (ItemRequestDto itemRequestDto : itemRequestDtos) {
                List<Item> requestItems = items.stream()
                        .filter(item -> item.getRequest().getId() == itemRequestDto.getId())
                        .collect(Collectors.toList());
                itemRequestDto.setItems(requestItems.stream()
                        .map(ItemMapper::toDto)
                        .collect(Collectors.toList()));
            }
            return itemRequestDtos;
        }
        throw new NotFoundDataException("User with id " + userId + " not found");
    }

    @Override
    public List<ItemRequestDto> getItemRequestsAllByOwner(long userId, long from, long size) {
        if (userRepository.findById(userId).isPresent()) {
            List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdIsNot(userId,
                    PageRequest.of((int) (from / size), (int) size, Sort.by(Sort.Direction.DESC, "created")));
            List<Long> itemRequestIds = itemRequests.stream()
                    .map(ItemRequest::getId)
                    .collect(Collectors.toList());
            List<ItemRequestDto> itemRequestDtos = itemRequests.stream()
                    .map(ItemRequestMapper:: toDto)
                    .collect(Collectors.toList());
            List<Item> items = itemRepository.findAllByRequestIdIn(itemRequestIds);
            for (ItemRequestDto itemRequestDto : itemRequestDtos) {
                List<Item> requestItems = items.stream()
                        .filter(item -> item.getRequest().getId() == itemRequestDto.getId())
                        .collect(Collectors.toList());
                itemRequestDto.setItems(requestItems.stream()
                        .map(ItemMapper::toDto)
                        .collect(Collectors.toList()));
            }
            return itemRequestDtos;
        }
        throw new NotFoundDataException("User with id " + userId + " not found");
    }

    @Override
    public ItemRequestDto getItemRequestById(long userId, long requestId) {
        if (userRepository.findById(userId).isPresent()) {
            if (itemRequestRepository.findById(requestId).isPresent()) {
                ItemRequest itemRequest = itemRequestRepository.findById(requestId).get();
                List<ItemDto> itemDtos = itemRepository.findAllByRequestId(requestId).stream()
                        .map(ItemMapper::toDto)
                        .collect(Collectors.toList());
                return ItemRequestMapper.toDto(itemRequest, itemDtos);
            }
            throw new NotFoundDataException("Item request with id " + userId + " not found");
        }
        throw new NotFoundDataException("User with id " + userId + " not found");
    }
}
