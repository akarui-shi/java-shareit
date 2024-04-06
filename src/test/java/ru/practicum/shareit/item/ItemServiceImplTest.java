package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    void addItem_whenItemValid_thenItemSaved() {
        long userId = 1L;
        ItemDto newItemDto = ItemDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .build();
        User user = User.builder()
                .id(userId)
                .name("user")
                .email("user@yandex.ru")
                .build();
        ItemDto expectedItemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(ItemMapper.fromDto(expectedItemDto));

        ItemDto actualItemDto = itemService.addNewItem(userId, newItemDto);

        assertThat(actualItemDto, equalTo(expectedItemDto));
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void addItemTest() {
        long userId = 1L;
        long requestId = 1L;
        long itemId = 1L;
        ItemDto newItemDto = ItemDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .requestId(requestId)
                .build();
        ItemDto expectedItemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .requestId(requestId)
                .build();
        User user = User.builder()
                .id(userId)
                .name("user")
                .email("user@yandex.ru")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(requestId)
                .build();
        Item savedItem = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .request(itemRequest)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        ItemDto actualItemDto = itemService.addNewItem(userId, newItemDto);

        assertThat(actualItemDto, equalTo(expectedItemDto));
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void addItemNotFoundUserExceptionTest() {
        long userId = 1L;
        long requestId = 1L;
        ItemDto expectedItemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .requestId(requestId)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundDataException.class, () -> itemService.addNewItem(userId, expectedItemDto));
    }

    @Test
    void updateItemTest() {
        long userId = 1L;
        long itemId = 1L;
        ItemDto itemDtoToUpdate = ItemDto.builder()
                .name("item-updated")
                .description("description-updated")
                .available(false)
                .build();
        ItemDto expectedItemDto = ItemDto.builder()
                .id(itemId)
                .name("item-updated")
                .description("description-updated")
                .available(false)
                .build();
        User user = User.builder()
                .id(userId)
                .name("user")
                .email("user@yandex.ru")
                .build();
        Item getItem = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(user)
                .build();
        Item updatedItem = Item.builder()
                .id(itemId)
                .name("item-updated")
                .description("description-updated")
                .available(false)
                .owner(user)
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(getItem));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        ItemDto actualItemDto = itemService.updateItem(itemId, userId, itemDtoToUpdate);

        assertThat(actualItemDto, equalTo(expectedItemDto));
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void getItemByIdTest() {
        long userId = 1;
        long commentatorId = 2;
        long itemId = 1;
        long commentId = 1;
        User owner = User.builder()
                .id(userId)
                .name("user")
                .email("user@yandex.ru")
                .build();
        User commentator = User.builder()
                .id(commentatorId)
                .name("commentator")
                .email("commentator@yandex.ru")
                .build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        Comment comment = Comment.builder()
                .id(commentId)
                .text("comment")
                .item(item)
                .author(commentator)
                .created(LocalDateTime.of(2024, 03, 01, 12, 0))
                .build();
        Booking lastBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 02, 01, 12, 0))
                .end(LocalDateTime.of(2024, 02, 02, 12, 0))
                .item(item)
                .booker(commentator)
                .status(Status.APPROVED)
                .build();
        Booking nextBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2024, 04, 01, 12, 0))
                .end(LocalDateTime.of(2024, 04, 02, 12, 0))
                .item(item)
                .booker(commentator)
                .status(Status.APPROVED)
                .build();
        ItemDto expectedItemDto = ItemDto.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .lastBooking(BookingMapper.toDto(lastBooking, commentatorId))
                .nextBooking(BookingMapper.toDto(nextBooking, commentatorId))
                .comments(List.of(CommentMapper.toDto(comment)))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(List.of(comment));
        when(bookingRepository.findFirstByItemIdAndStartIsBeforeAndStatusOrderByStartDesc(
                eq(itemId), any(LocalDateTime.class), eq(Status.APPROVED))).thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(
                eq(itemId), any(LocalDateTime.class), eq(Status.APPROVED))).thenReturn(Optional.of(nextBooking));


        ItemDto actualItemDto = itemService.getItem(userId, itemId);

        assertThat(actualItemDto, equalTo(expectedItemDto));
    }

    @Test
    void getItemByIdUserNotOwnerTest() {
        long userId = 3;
        long ownerId = 1;
        long commentatorId = 2;
        long itemId = 1;
        long commentId = 1;
        User owner = User.builder()
                .id(ownerId)
                .name("user")
                .email("user@yandex.ru")
                .build();
        User commentator = User.builder()
                .id(commentatorId)
                .name("commentator")
                .email("commentator@yandex.ru")
                .build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        Comment comment = Comment.builder()
                .id(commentId)
                .text("comment")
                .item(item)
                .author(commentator)
                .created(LocalDateTime.of(2024, 03, 01, 12, 0))
                .build();
        ItemDto expectedItemDto = ItemDto.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .comments(List.of(CommentMapper.toDto(comment)))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(List.of(comment));

        ItemDto actualItemDto = itemService.getItem(userId, itemId);

        assertThat(actualItemDto, equalTo(expectedItemDto));
    }

    @Test
    void getAllItemsByUserIdTest() {
        long userId = 1;
        long itemId = 1;
        User owner = User.builder()
                .id(userId)
                .name("user")
                .email("user@yandex.ru")
                .build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        ItemDto expectedItemDto = ItemDto.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .comments(List.of())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(eq(userId))).thenReturn(List.of(item));
        when(commentRepository.findAllByItemId(any(Long.class))).thenReturn(List.of());

        List<ItemDto> actualList = itemService.getItemsByUser(userId);

        assertThat(actualList, equalTo(List.of(expectedItemDto)));
    }

    @Test
    void getAllItemsWithTextTest() {
        long userId = 1;
        long itemId = 1;
        String searchString = "script";
        User owner = User.builder()
                .id(userId)
                .name("user")
                .email("user@yandex.ru")
                .build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        ItemDto expectedItemDto = ItemDto.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .build();

        when(itemRepository.search(eq(searchString)))
                .thenReturn(List.of(item));

        List<ItemDto> actualList = itemService.searchItemsByKeyword(searchString);

        assertThat(actualList, equalTo(List.of(expectedItemDto)));
    }

    @Test
    void getAllItemsWithTextBlankTest() {
        String searchString = "";

        List<ItemDto> actualList = itemService.searchItemsByKeyword(searchString);

        assertThat(actualList, equalTo(List.of()));
    }

    @Test
    void addCommentTest() {
        long authorId = 2;
        long itemId = 1;
        long commentId = 1;
        long bookingId = 1;
        User author = User.builder()
                .id(authorId)
                .name("author")
                .email("author@yandex.ru")
                .build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(User.builder()
                        .id(1L)
                        .name("owner")
                        .email("owner@yandex.ru")
                        .build())
                .build();
        Booking finishedBooking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.of(2024, 03, 01, 12, 0))
                .end(LocalDateTime.of(2024, 03, 02, 12, 0))
                .item(item)
                .booker(author)
                .status(Status.APPROVED)
                .build();
        CommentDto newCommentDto = CommentDto.builder()
                .text("comment")
                .build();
        CommentDto expectedCommentDto = CommentDto.builder()
                .id(commentId)
                .text("comment")
                .authorName("author")
                .created(LocalDateTime.of(2024, 03, 03, 12, 0))
                .build();
        Comment savedComment = Comment.builder()
                .id(commentId)
                .text("comment")
                .item(item)
                .author(author)
                .created(LocalDateTime.of(2024, 03, 03, 12, 0))
                .build();

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByBookerIdAndItemIdAndStatusIsAndEndIsBeforeOrderByEndDesc(
                    eq(authorId), eq(itemId), eq(Status.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.of(finishedBooking));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentDto actualCommentDto = itemService.addComment(authorId, itemId, newCommentDto);

        assertThat(actualCommentDto, equalTo(expectedCommentDto));
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addCommentNoCompletedBookingTest() {
        long authorId = 2;
        long itemId = 1;
        User author = User.builder()
                .id(authorId)
                .name("author")
                .email("author@yandex.ru")
                .build();
        CommentDto newCommentDto = CommentDto.builder()
                .text("comment")
                .build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(User.builder()
                        .id(1L)
                        .name("owner")
                        .email("owner@yandex.ru")
                        .build())
                .build();

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByBookerIdAndItemIdAndStatusIsAndEndIsBeforeOrderByEndDesc(
                eq(authorId), eq(itemId), eq(Status.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        assertThrows(NoFinishBookingForCommentException.class,
                () -> itemService.addComment(authorId, itemId, newCommentDto));
    }

}
