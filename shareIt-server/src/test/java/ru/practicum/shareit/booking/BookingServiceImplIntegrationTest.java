package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(properties = "db.name=admin", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplIntegrationTest {

    @Autowired
    private final BookingServiceImpl bookingService;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final BookingRepository bookingRepository;

    private User user1;
    private User user2;
    private User user3;
    private Item item;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    void fillDataBase() {
        user1 = userRepository.save(User.builder().name("user1").email("user1@yandex.ru").build());
        user2 = userRepository.save(User.builder().name("user2").email("user2@yandex.ru").build());
        user3 = userRepository.save(User.builder().name("user3").email("user3@yandex.ru").build());
        item = itemRepository.save(Item.builder()
                .name("item")
                .description("description1")
                .available(true)
                .owner(user1)
                .build());
        booking1 = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item).booker(user2).status(Status.APPROVED).build());
        booking2 = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item).booker(user3).status(Status.WAITING).build());
    }

    @AfterEach
    void clearDataBase() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void addBookingTest() {
        NewBookingDto newBookingDto = NewBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingDto expectedBookingDto = BookingDto.builder()
                .start(newBookingDto.getStart())
                .end(newBookingDto.getEnd())
                .status(Status.WAITING)
                .item(Item.builder().id(item.getId()).name(item.getName()).build())
                .booker(User.builder().id(user2.getId()).build())
                .build();

        BookingDto actualBookingDto = bookingService.addBooking(user2.getId(), newBookingDto);
        expectedBookingDto.setId(actualBookingDto.getId());

        assertThat(actualBookingDto.getStart(), equalTo(expectedBookingDto.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(expectedBookingDto.getEnd()));
        assertThat(actualBookingDto.getStatus(), equalTo(expectedBookingDto.getStatus()));
        assertThat(actualBookingDto.getItem().getId(), equalTo(expectedBookingDto.getItem().getId()));
        assertThat(actualBookingDto.getItem().getName(), equalTo(expectedBookingDto.getItem().getName()));
        assertThat(actualBookingDto.getBooker().getId(), equalTo(expectedBookingDto.getBooker().getId()));
    }


    @Test
    void addBookingThenExceptionThrown() {
        NewBookingDto newBookingDto = NewBookingDto.builder()
                .itemId(item.getId())
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(NullPointerException.class,
                () -> bookingService.addBooking(user2.getId(), newBookingDto));
    }

    @Test
    void addBooking_whenEndNull_thenExceptionThrown() {
        NewBookingDto newBookingDto = NewBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .build();

        assertThrows(NullPointerException.class,
                () -> bookingService.addBooking(user2.getId(), newBookingDto));
    }

    @Test
    void updateBookingTest() {
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking2.getId())
                .start(booking2.getStart())
                .end(booking2.getEnd())
                .status(Status.APPROVED)
                .item(Item.builder().id(item.getId()).name(item.getName()).build())
                .booker(User.builder().id(user3.getId()).build())
                .build();

        BookingDto actualBookingDto = bookingService.update(
                booking2.getId(), user1.getId(), true);

        assertThat(actualBookingDto.getId(), equalTo(expectedBookingDto.getId()));
        assertThat(actualBookingDto.getStart(), equalTo(expectedBookingDto.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(expectedBookingDto.getEnd()));
        assertThat(actualBookingDto.getStatus(), equalTo(expectedBookingDto.getStatus()));
        assertThat(actualBookingDto.getItem().getId(), equalTo(expectedBookingDto.getItem().getId()));
        assertThat(actualBookingDto.getItem().getName(), equalTo(expectedBookingDto.getItem().getName()));
        assertThat(actualBookingDto.getBooker().getId(), equalTo(expectedBookingDto.getBooker().getId()));
    }

    @Test
    void getBookingByIdTest() {
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking1.getId())
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .status(booking1.getStatus())
                .item(Item.builder().id(item.getId()).name(item.getName()).build())
                .booker(User.builder().id(user2.getId()).build())
                .build();

        BookingDto actualBookingDto = bookingService.getBookingById(booking1.getId(), user2.getId());

        assertThat(actualBookingDto.getId(), equalTo(expectedBookingDto.getId()));
        assertThat(actualBookingDto.getStart(), equalTo(expectedBookingDto.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(expectedBookingDto.getEnd()));
        assertThat(actualBookingDto.getStatus(), equalTo(expectedBookingDto.getStatus()));
        assertThat(actualBookingDto.getItem().getId(), equalTo(expectedBookingDto.getItem().getId()));
        assertThat(actualBookingDto.getItem().getName(), equalTo(expectedBookingDto.getItem().getName()));
        assertThat(actualBookingDto.getBooker().getId(), equalTo(expectedBookingDto.getBooker().getId()));
    }

    @Test
    void getAllBookingsByUserTest() {
        String state = "ALL";
        long from = 0;
        long size = 10;
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking1.getId())
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .status(booking1.getStatus())
                .item(Item.builder().id(item.getId()).name(item.getName()).build())
                .booker(User.builder().id(user2.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsByUser(user2.getId(), state, from, size);
        BookingDto actualBookingDto = actualList.get(0);

        assertThat(actualBookingDto.getId(), equalTo(expectedBookingDto.getId()));
        assertThat(actualBookingDto.getStart(), equalTo(expectedBookingDto.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(expectedBookingDto.getEnd()));
        assertThat(actualBookingDto.getStatus(), equalTo(expectedBookingDto.getStatus()));
        assertThat(actualBookingDto.getItem().getId(), equalTo(expectedBookingDto.getItem().getId()));
        assertThat(actualBookingDto.getItem().getName(), equalTo(expectedBookingDto.getItem().getName()));
        assertThat(actualBookingDto.getBooker().getId(), equalTo(expectedBookingDto.getBooker().getId()));
    }

    @Test
    void getAllBookingsByUserStatePast() {
        String state = "PAST";
        long from = 0;
        long size = 10;
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking1.getId())
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .status(booking1.getStatus())
                .item(Item.builder().id(item.getId()).name(item.getName()).build())
                .booker(User.builder().id(user2.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsByUser(user2.getId(), state, from, size);

        BookingDto actualBookingDto = actualList.get(0);

        assertThat(actualBookingDto.getId(), equalTo(expectedBookingDto.getId()));
        assertThat(actualBookingDto.getStart(), equalTo(expectedBookingDto.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(expectedBookingDto.getEnd()));
        assertThat(actualBookingDto.getStatus(), equalTo(expectedBookingDto.getStatus()));
        assertThat(actualBookingDto.getItem().getId(), equalTo(expectedBookingDto.getItem().getId()));
        assertThat(actualBookingDto.getItem().getName(), equalTo(expectedBookingDto.getItem().getName()));
        assertThat(actualBookingDto.getBooker().getId(), equalTo(expectedBookingDto.getBooker().getId()));
    }

    @Test
    void getAllBookingsByUserFuture() {
        String state = "FUTURE";
        long from = 0;
        long size = 10;
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item).booker(user2).status(Status.WAITING).build());
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(Item.builder().id(item.getId()).name(item.getName()).build())
                .booker(User.builder().id(user2.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsByUser(user2.getId(), state, from, size);
        BookingDto actualBookingDto = actualList.get(0);

        assertThat(actualBookingDto.getId(), equalTo(expectedBookingDto.getId()));
        assertThat(actualBookingDto.getStart(), equalTo(expectedBookingDto.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(expectedBookingDto.getEnd()));
        assertThat(actualBookingDto.getStatus(), equalTo(expectedBookingDto.getStatus()));
        assertThat(actualBookingDto.getItem().getId(), equalTo(expectedBookingDto.getItem().getId()));
        assertThat(actualBookingDto.getItem().getName(), equalTo(expectedBookingDto.getItem().getName()));
        assertThat(actualBookingDto.getBooker().getId(), equalTo(expectedBookingDto.getBooker().getId()));
    }

    @Test
    void getAllBookingsByUserStateCurrent() {
        String state = "CURRENT";
        long from = 0;
        long size = 10;
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item).booker(user2).status(Status.APPROVED).build());
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(Item.builder().id(item.getId()).name(item.getName()).build())
                .booker(User.builder().id(user2.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsByUser(user2.getId(), state, from, size);
        BookingDto actualBookingDto = actualList.get(0);

        assertThat(actualBookingDto.getId(), equalTo(expectedBookingDto.getId()));
        assertThat(actualBookingDto.getStart(), equalTo(expectedBookingDto.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(expectedBookingDto.getEnd()));
        assertThat(actualBookingDto.getStatus(), equalTo(expectedBookingDto.getStatus()));
        assertThat(actualBookingDto.getItem().getId(), equalTo(expectedBookingDto.getItem().getId()));
        assertThat(actualBookingDto.getItem().getName(), equalTo(expectedBookingDto.getItem().getName()));
        assertThat(actualBookingDto.getBooker().getId(), equalTo(expectedBookingDto.getBooker().getId()));
    }

    @Test
    void getAllBookingsByUserStateWaiting() {
        String state = "WAITING";
        long from = 0;
        long size = 10;
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item).booker(user2).status(Status.WAITING).build());
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(Item.builder().id(item.getId()).name(item.getName()).build())
                .booker(User.builder().id(user2.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsByUser(user2.getId(), state, from, size);
        BookingDto actualBookingDto = actualList.get(0);

        assertThat(actualBookingDto.getId(), equalTo(expectedBookingDto.getId()));
        assertThat(actualBookingDto.getStart(), equalTo(expectedBookingDto.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(expectedBookingDto.getEnd()));
        assertThat(actualBookingDto.getStatus(), equalTo(expectedBookingDto.getStatus()));
        assertThat(actualBookingDto.getItem().getId(), equalTo(expectedBookingDto.getItem().getId()));
        assertThat(actualBookingDto.getItem().getName(), equalTo(expectedBookingDto.getItem().getName()));
        assertThat(actualBookingDto.getBooker().getId(), equalTo(expectedBookingDto.getBooker().getId()));
    }

    @Test
    void getAllBookingsByUserStateRejected() {
        String state = "REJECTED";
        long from = 0;
        long size = 10;
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item).booker(user2).status(Status.REJECTED).build());
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(Item.builder().id(item.getId()).name(item.getName()).build())
                .booker(User.builder().id(user2.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsByUser(user2.getId(), state, from, size);
        BookingDto actualBookingDto = actualList.get(0);

        assertThat(actualBookingDto.getId(), equalTo(expectedBookingDto.getId()));
        assertThat(actualBookingDto.getStart(), equalTo(expectedBookingDto.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(expectedBookingDto.getEnd()));
        assertThat(actualBookingDto.getStatus(), equalTo(expectedBookingDto.getStatus()));
        assertThat(actualBookingDto.getItem().getId(), equalTo(expectedBookingDto.getItem().getId()));
        assertThat(actualBookingDto.getItem().getName(), equalTo(expectedBookingDto.getItem().getName()));
        assertThat(actualBookingDto.getBooker().getId(), equalTo(expectedBookingDto.getBooker().getId()));
    }

    @Test
    void getAllBookingsAllItemsByOwnerStateAll() {
        String state = "ALL";
        long from = 0;
        long size = 10;
        BookingDto expectedBookingDto1 = BookingDto.builder()
                .id(booking1.getId())
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .status(booking1.getStatus())
                .item(Item.builder().id(item.getId()).name(item.getName()).build())
                .booker(User.builder().id(user2.getId()).build())
                .build();
        BookingDto expectedBookingDto2 = BookingDto.builder()
                .id(booking2.getId())
                .start(booking2.getStart())
                .end(booking2.getEnd())
                .status(booking2.getStatus())
                .item(Item.builder().id(item.getId()).name(item.getName()).build())
                .booker(User.builder().id(user3.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsAllItemsByOwner(user1.getId(), state, from, size);

        BookingDto actualBookingDto1 = actualList.get(1);
        BookingDto actualBookingDto2 = actualList.get(0);

        assertThat(actualBookingDto1.getId(), equalTo(expectedBookingDto1.getId()));
        assertThat(actualBookingDto1.getStart(), equalTo(expectedBookingDto1.getStart()));
        assertThat(actualBookingDto1.getEnd(), equalTo(expectedBookingDto1.getEnd()));
        assertThat(actualBookingDto1.getStatus(), equalTo(expectedBookingDto1.getStatus()));
        assertThat(actualBookingDto1.getItem().getId(), equalTo(expectedBookingDto1.getItem().getId()));
        assertThat(actualBookingDto1.getItem().getName(), equalTo(expectedBookingDto1.getItem().getName()));
        assertThat(actualBookingDto1.getBooker().getId(), equalTo(expectedBookingDto1.getBooker().getId()));

        assertThat(actualBookingDto2.getId(), equalTo(expectedBookingDto2.getId()));
        assertThat(actualBookingDto2.getStart(), equalTo(expectedBookingDto2.getStart()));
        assertThat(actualBookingDto2.getEnd(), equalTo(expectedBookingDto2.getEnd()));
        assertThat(actualBookingDto2.getStatus(), equalTo(expectedBookingDto2.getStatus()));
        assertThat(actualBookingDto2.getItem().getId(), equalTo(expectedBookingDto2.getItem().getId()));
        assertThat(actualBookingDto2.getItem().getName(), equalTo(expectedBookingDto2.getItem().getName()));
        assertThat(actualBookingDto2.getBooker().getId(), equalTo(expectedBookingDto2.getBooker().getId()));
    }

    @Test
    void getAllBookingsAllItemsByOwnerStatePast() {
        String state = "PAST";
        long from = 0;
        long size = 10;
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking1.getId())
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .status(booking1.getStatus())
                .item(Item.builder().id(item.getId()).name(item.getName()).build())
                .booker(User.builder().id(user2.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsAllItemsByOwner(user1.getId(), state, from, size);
        BookingDto actualBookingDto = actualList.get(0);

        assertThat(actualBookingDto.getId(), equalTo(expectedBookingDto.getId()));
        assertThat(actualBookingDto.getStart(), equalTo(expectedBookingDto.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(expectedBookingDto.getEnd()));
        assertThat(actualBookingDto.getStatus(), equalTo(expectedBookingDto.getStatus()));
        assertThat(actualBookingDto.getItem().getId(), equalTo(expectedBookingDto.getItem().getId()));
        assertThat(actualBookingDto.getItem().getName(), equalTo(expectedBookingDto.getItem().getName()));
        assertThat(actualBookingDto.getBooker().getId(), equalTo(expectedBookingDto.getBooker().getId()));
    }

    @Test
    void getAllBookingsAllItemsByOwnerStateFuture() {
        String state = "FUTURE";
        long from = 0;
        long size = 10;
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking2.getId())
                .start(booking2.getStart())
                .end(booking2.getEnd())
                .status(booking2.getStatus())
                .item(Item.builder().id(item.getId()).name(item.getName()).build())
                .booker(User.builder().id(user3.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsAllItemsByOwner(user1.getId(), state, from, size);
        BookingDto actualBookingDto = actualList.get(0);

        assertThat(actualBookingDto.getId(), equalTo(expectedBookingDto.getId()));
        assertThat(actualBookingDto.getStart(), equalTo(expectedBookingDto.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(expectedBookingDto.getEnd()));
        assertThat(actualBookingDto.getStatus(), equalTo(expectedBookingDto.getStatus()));
        assertThat(actualBookingDto.getItem().getId(), equalTo(expectedBookingDto.getItem().getId()));
        assertThat(actualBookingDto.getItem().getName(), equalTo(expectedBookingDto.getItem().getName()));
        assertThat(actualBookingDto.getBooker().getId(), equalTo(expectedBookingDto.getBooker().getId()));
    }

    @Test
    void getAllBookingsAllItemsByOwnerStateCurrent() {
        String state = "CURRENT";
        long from = 0;
        long size = 10;
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item).booker(user2).status(Status.APPROVED).build());
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(Item.builder().id(item.getId()).name(item.getName()).build())
                .booker(User.builder().id(user2.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsAllItemsByOwner(user1.getId(), state, from, size);
        BookingDto actualBookingDto = actualList.get(0);

        assertThat(actualBookingDto.getId(), equalTo(expectedBookingDto.getId()));
        assertThat(actualBookingDto.getStart(), equalTo(expectedBookingDto.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(expectedBookingDto.getEnd()));
        assertThat(actualBookingDto.getStatus(), equalTo(expectedBookingDto.getStatus()));
        assertThat(actualBookingDto.getItem().getId(), equalTo(expectedBookingDto.getItem().getId()));
        assertThat(actualBookingDto.getItem().getName(), equalTo(expectedBookingDto.getItem().getName()));
        assertThat(actualBookingDto.getBooker().getId(), equalTo(expectedBookingDto.getBooker().getId()));
    }

    @Test
    void getAllBookingsAllItemsByOwnerStateWaiting() {
        String state = "WAITING";
        long from = 0;
        long size = 10;
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking2.getId())
                .start(booking2.getStart())
                .end(booking2.getEnd())
                .status(booking2.getStatus())
                .item(Item.builder()
                        .id(booking2.getItem().getId())
                        .name(booking2.getItem().getName())
                        .description(booking2.getItem().getDescription())
                        .owner(booking2.getItem().getOwner())
                        .available(booking2.getItem().getAvailable())
                        .build())
                .booker(User.builder().id(booking2.getBooker().getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsAllItemsByOwner(user1.getId(), state, from, size);

        BookingDto actualBookingDto = actualList.get(0);

        assertThat(actualBookingDto.getId(), equalTo(expectedBookingDto.getId()));
        assertThat(actualBookingDto.getStart(), equalTo(expectedBookingDto.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(expectedBookingDto.getEnd()));
        assertThat(actualBookingDto.getStatus(), equalTo(expectedBookingDto.getStatus()));
        assertThat(actualBookingDto.getItem().getId(), equalTo(expectedBookingDto.getItem().getId()));
        assertThat(actualBookingDto.getItem().getName(), equalTo(expectedBookingDto.getItem().getName()));
        assertThat(actualBookingDto.getItem().getDescription(), equalTo(expectedBookingDto.getItem().getDescription()));
        assertThat(actualBookingDto.getItem().getOwner().getId(), equalTo(expectedBookingDto.getItem().getOwner().getId()));
        assertThat(actualBookingDto.getItem().getOwner().getName(), equalTo(expectedBookingDto.getItem().getOwner().getName()));
        assertThat(actualBookingDto.getItem().getOwner().getEmail(), equalTo(expectedBookingDto.getItem().getOwner().getEmail()));
        assertThat(actualBookingDto.getItem().getAvailable(), equalTo(expectedBookingDto.getItem().getAvailable()));
        assertThat(actualBookingDto.getBooker().getId(), equalTo(expectedBookingDto.getBooker().getId()));
    }

    @Test
    void getAllBookingsAllItemsByOwnerStateRejected() {
        String state = "REJECTED";
        long from = 0;
        long size = 10;
        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item).booker(user2).status(Status.REJECTED).build());
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(Item.builder().id(item.getId()).name(item.getName()).build())
                .booker(User.builder().id(user2.getId()).build())
                .build();

        List<BookingDto> actualList = bookingService.getAllBookingsAllItemsByOwner(user1.getId(), state, from, size);
        BookingDto actualBookingDto = actualList.get(0);

        assertThat(actualBookingDto.getId(), equalTo(expectedBookingDto.getId()));
        assertThat(actualBookingDto.getStart(), equalTo(expectedBookingDto.getStart()));
        assertThat(actualBookingDto.getEnd(), equalTo(expectedBookingDto.getEnd()));
        assertThat(actualBookingDto.getStatus(), equalTo(expectedBookingDto.getStatus()));
        assertThat(actualBookingDto.getItem().getId(), equalTo(expectedBookingDto.getItem().getId()));
        assertThat(actualBookingDto.getItem().getName(), equalTo(expectedBookingDto.getItem().getName()));
        assertThat(actualBookingDto.getBooker().getId(), equalTo(expectedBookingDto.getBooker().getId()));
    }
}
