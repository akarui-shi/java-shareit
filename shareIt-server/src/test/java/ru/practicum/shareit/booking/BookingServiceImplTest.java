package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.exeption.AccessItemDeniedException;
import ru.practicum.shareit.booking.exeption.NoWaitingStatusException;
import ru.practicum.shareit.booking.exeption.NotBookingRelationException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.exception.NotFoundDataException;
import ru.practicum.shareit.item.model.Item;
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
public class BookingServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;

    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    void addBooking_whenBookingValid_thenBookingSaved() {
        long ownerId = 1L;
        long bookerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        NewBookingDto newBookingDto = NewBookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        User owner = User.builder().id(ownerId).name("user1").email("user1@yandex.ru").build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(newBookingDto.getStart())
                .end(newBookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(Status.WAITING).build();
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(bookingId)
                .start(newBookingDto.getStart())
                .end(newBookingDto.getEnd())
                .status(Status.WAITING)
                .item(item)
                .booker(booker)
                .build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto actualBookingDto = bookingService.addBooking(bookerId, newBookingDto);

        assertThat(actualBookingDto, equalTo(expectedBookingDto));
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void addBooking_whenBookerIsOwner_thenExceptionThrown() {
        long ownerId = 1L;
        long bookerId = 1L;
        long itemId = 1L;
        NewBookingDto newBookingDto = NewBookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        User owner = User.builder().id(ownerId).name("user1").email("user1@yandex.ru").build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner).build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(NotBookingRelationException.class, () -> bookingService.addBooking(bookerId, newBookingDto));
    }

    @Test
    void addBooking_whenItemUnavailable_thenExceptionThrown() {
        long ownerId = 1L;
        long bookerId = 2L;
        long itemId = 1L;
        NewBookingDto newBookingDto = NewBookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        User owner = User.builder().id(ownerId).name("user1").email("user1@yandex.ru").build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(false)
                .owner(owner).build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(AccessItemDeniedException.class, () -> bookingService.addBooking(bookerId, newBookingDto));
    }

    @Test
    void addBookingNotFoundUserException() {
        long bookerId = 2L;
        long itemId = 1L;
        NewBookingDto newBookingDto = NewBookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundDataException.class, () -> bookingService.addBooking(bookerId, newBookingDto));
    }

    @Test
    void addBookingNotFoundItemException() {
        long bookerId = 2L;
        long itemId = 1L;
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        NewBookingDto newBookingDto = NewBookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundDataException.class, () -> bookingService.addBooking(bookerId, newBookingDto));
    }

    @Test
    void approveOrRejectBooking_whenInputValid_thenBookingUpdated() {
        long ownerId = 1L;
        long bookerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        User owner = User.builder().id(ownerId).name("user1").email("user1@yandex.ru").build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Status.WAITING).build();
        Booking bookingUpdated = Booking.builder()
                .id(bookingId)
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(booker)
                .status(Status.APPROVED).build();
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(bookingId)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(Status.APPROVED)
                .item(item)
                .booker(booker)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingUpdated);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        BookingDto actualBookingDto = bookingService.update(bookingId, ownerId, true);

        assertThat(actualBookingDto, equalTo(expectedBookingDto));
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void approveOrRejectBooking_whenNotOwner_thenExceptionThrown() {
        long ownerId = 1L;
        long bookerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        User owner = User.builder().id(ownerId).name("user1").email("user1@yandex.ru").build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Status.WAITING).build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));

        assertThrows(NotBookingRelationException.class,
                () -> bookingService.update(bookingId, bookerId, true));
    }

    @Test
    void approveOrRejectBooking_whenStatusNotWaiting_thenExceptionThrown() {
        long ownerId = 1L;
        long bookerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        User owner = User.builder().id(ownerId).name("user1").email("user1@yandex.ru").build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Status.CANCELED).build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        assertThrows(NoWaitingStatusException.class,
                () -> bookingService.update(bookingId, ownerId, true));
    }

    @Test
    void getAllBookingsByUser_whenInputValid_thenReturnedListOfDto() {
        long ownerId = 1L;
        long bookerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        String state = "ALL";
        long from = 0;
        long size = 10;
        User owner = User.builder().id(ownerId).name("user1").email("user1@yandex.ru").build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Status.WAITING).build();
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(bookingId)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(Status.WAITING)
                .item(item)
                .booker(booker)
                .build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(eq(bookerId), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualList = bookingService.getAllBookingsByUser(bookerId, state, from, size);

        assertThat(actualList, equalTo(List.of(expectedBookingDto)));
    }

    @Test
    void getAllBookingsByUserNotFoundUserTest() {
        long bookerId = 2L;
        String state = "ALL";
        long from = 0;
        long size = 10;

        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundDataException.class, () -> bookingService.getAllBookingsByUser(bookerId, state, from, size));
    }

    @Test
    void getAllBookingsAllItemsByOwner_whenInputValid_thenReturnedListOfDto() {
        long ownerId = 1L;
        long bookerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        String state = "ALL";
        long from = 0;
        long size = 10;
        User owner = User.builder().id(ownerId).name("user1").email("user1@yandex.ru").build();
        User booker = User.builder().id(bookerId).name("user2").email("user2@yandex.ru").build();
        Item item = Item.builder()
                .id(itemId)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Status.WAITING).build();
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(bookingId)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(Status.WAITING)
                .item(item)
                .booker(booker)
                .build();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(eq(ownerId), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> actualList = bookingService.getAllBookingsAllItemsByOwner(ownerId, state, from, size);

        assertThat(actualList, equalTo(List.of(expectedBookingDto)));
    }

    @Test
    void getAllBookingsAllItemsByOwnerNotFoundUserException() {
        long ownerId = 1L;
        String state = "ALL";
        long from = 0;
        long size = 10;

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundDataException.class, () -> bookingService.getAllBookingsAllItemsByOwner(ownerId, state, from, size));
    }

    @Test
    void getBookingByIdNotFoundUserException() {
        long bookerId = 1L;
        long bookingId = 1L;
        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundDataException.class, () -> bookingService.getBookingById(bookerId, bookingId));
    }

    @Test
    void getBookingByIdNotFoundBookingException() {
        long bookerId = 1L;
        long bookingId = 1L;
        User booker = User.builder()
                .id(bookerId)
                .name("user2")
                .email("user2@yandex.ru")
                .build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundDataException.class, () -> bookingService.getBookingById(bookerId, bookingId));
    }


}
