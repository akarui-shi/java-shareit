package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.NoFinishBookingForCommentException;
import ru.practicum.shareit.item.exception.NotFoundDataException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByUser(long userId) {
        if (userRepository.findById(userId).isPresent()) {
            List<ItemDto> items = itemRepository.findAllByOwnerId(userId).stream()
                    .map(ItemMapper::toDto)
                    .collect(Collectors.toList());
            for (ItemDto item : items) {
                item.setComments(commentRepository.findAllByItemId(item.getId()).stream()
                        .map(CommentMapper::toDto)
                        .collect(Collectors.toList()));
                Optional<Booking> lastBooking =
                        bookingRepository.findFirstByItemIdAndStartIsBeforeAndStatusOrderByStartDesc(
                                item.getId(), LocalDateTime.now(), Status.APPROVED);
                if (lastBooking.isPresent()) {
                    item.setLastBooking(BookingMapper.toDto(lastBooking.get(), lastBooking.get().getBooker().getId()));
                }
                Optional<Booking> nextBooking =
                        bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(
                                item.getId(), LocalDateTime.now(), Status.APPROVED);
                if (nextBooking.isPresent()) {
                    item.setNextBooking(BookingMapper.toDto(nextBooking.get(), nextBooking.get().getBooker().getId()));
                }
            }
            return items;
        }
        throw new NotFoundDataException("User with id " + userId + " not found");
    }

    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        if (userRepository.findById(userId).isPresent()) {
            User owner = userRepository.findById(userId).get();
            itemDto.setOwner(owner);
            Item item = ItemMapper.fromDto(itemDto);
            if (itemDto.getRequestId() != null) {
                ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                        new NotFoundDataException("Request with id " + itemDto.getRequestId() + " not found"));
                item.setRequest(itemRequest);
            }
            return ItemMapper.toDto(itemRepository.save(item));
        }
        throw new NotFoundDataException("User with id " + userId + " not found");
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItem(long userId, long id) {
        if (userRepository.findById(userId).isPresent() && itemRepository.findById(id).isPresent()) {
            Item item = itemRepository.findById(id).get();
            ItemDto itemDto = ItemMapper.toDto(item);
            itemDto.setComments(commentRepository.findAllByItemId(id).stream()
                    .map(CommentMapper::toDto)
                    .collect(Collectors.toList()));
            if (userId != item.getOwner().getId()) {
                return itemDto;
            }
            Optional<Booking> lastBooking =
                    bookingRepository.findFirstByItemIdAndStartIsBeforeAndStatusOrderByStartDesc(
                            id, LocalDateTime.now(), Status.APPROVED);
            if (lastBooking.isPresent()) {
                itemDto.setLastBooking(BookingMapper.toDto(lastBooking.get(), lastBooking.get().getBooker().getId()));
            }
            Optional<Booking> nextBooking =
                    bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(
                            id, LocalDateTime.now(), Status.APPROVED);
            if (nextBooking.isPresent()) {
                itemDto.setNextBooking(BookingMapper.toDto(nextBooking.get(), nextBooking.get().getBooker().getId()));
            }
            return itemDto;
        }
        throw new NotFoundDataException("Item by owner id " + id + " with id " + id + " not found");
    }

    @Override
    public ItemDto updateItem(long userId, long id, ItemDto itemDto) {
        if (itemRepository.findById(id).isPresent() && itemRepository.findById(id).get().getOwner().getId() == userId) {
            Item item = itemRepository.findById(id).get();
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            return ItemMapper.toDto(itemRepository.save(item));
        }
        throw new NotFoundDataException("Item by owner id " + id + " with id " + id + " not found");
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItemsByKeyword(String keyword) {
        if (keyword.isBlank()) {
            return List.of();
        }
        return itemRepository.search(keyword).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundDataException("User with id " + userId + " not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundDataException("Item with id " + itemId + " not found"));
        Booking booking = bookingRepository.findFirstByBookerIdAndItemIdAndStatusIsAndEndIsBeforeOrderByEndDesc(
                userId, itemId, Status.APPROVED, LocalDateTime.now()).orElseThrow(() ->
                new NoFinishBookingForCommentException("Booking with id " + itemId + " not found"));
        Comment comment = CommentMapper.fromDto(commentDto, author, item, LocalDateTime.now());
        return CommentMapper.toDto(commentRepository.save(comment));

    }
}
