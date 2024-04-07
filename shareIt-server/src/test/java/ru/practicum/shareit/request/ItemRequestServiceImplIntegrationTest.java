package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private final ItemRequestServiceImpl itemRequestService;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final ItemRequestRepository itemRequestRepository;

    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private Item item1;
    private Item item2;

    @BeforeEach
    void fillDataBase() {
        user1 = userRepository.save(User.builder().name("user1").email("user1@yandex.ru").build());
        user2 = userRepository.save(User.builder().name("user2").email("user2@yandex.ru").build());
        user3 = userRepository.save(User.builder().name("user3").email("user3@yandex.ru").build());
        user4 = userRepository.save(User.builder().name("user4").email("user4@yandex.ru").build());
        itemRequest1 = itemRequestRepository.save(ItemRequest.builder()
                .description("description-request1")
                .requester(user2)
                .created(LocalDateTime.now()).build());
        itemRequest2 = itemRequestRepository.save(ItemRequest.builder()
                .description("description-request2")
                .requester(user3)
                .created(LocalDateTime.now()).build());
        item1 = itemRepository.save(Item.builder()
                .name("item1")
                .description("description-item1")
                .available(true)
                .owner(user1)
                .request(itemRequest1).build());
        item2 = itemRepository.save(Item.builder()
                .name("item2")
                .description("description-item2")
                .available(true)
                .owner(user1)
                .request(itemRequest2).build());
    }

    @AfterEach
    void clearDataBase() {
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void addItemRequestTest() {
        ItemRequestDto newItemRequestDto = ItemRequestDto.builder()
                .description("description")
                .build();
        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder()
                .description("description")
                .requester(user4.getId())
                .items(List.of())
                .build();

        ItemRequestDto actualItemRequestDto = itemRequestService.addItemRequest(user4.getId(), newItemRequestDto);
        expectedItemRequestDto.setId(actualItemRequestDto.getId());
        expectedItemRequestDto.setCreated(actualItemRequestDto.getCreated());

        assertThat(actualItemRequestDto, equalTo(expectedItemRequestDto));
    }

    @Test
    void getItemRequestsByOwnerTest() {
        ItemDto itemDto = ItemDto.builder()
                .id(item1.getId())
                .name(item1.getName())
                .description(item1.getDescription())
                .available(item1.getAvailable())
                .requestId(itemRequest1.getId())
                .build();
        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder()
                .id(itemRequest1.getId())
                .description(itemRequest1.getDescription())
                .requester(user2.getId())
                .items(List.of(itemDto))
                .created(itemRequest1.getCreated())
                .build();

        List<ItemRequestDto> actualList = itemRequestService.getItemRequestsByOwner(user2.getId());

        assertThat(actualList, equalTo(List.of(expectedItemRequestDto)));
    }

    @Test
    void getItemRequestsAllButOwnerTest() {
        long from = 0L;
        long size = 10L;
        ItemDto itemDto = ItemDto.builder()
                .id(item2.getId())
                .name(item2.getName())
                .description(item2.getDescription())
                .available(item2.getAvailable())
                .requestId(itemRequest2.getId())
                .build();
        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder()
                .id(itemRequest2.getId())
                .description(itemRequest2.getDescription())
                .requester(user3.getId())
                .items(List.of(itemDto))
                .created(itemRequest2.getCreated())
                .build();

        List<ItemRequestDto> actualList = itemRequestService.getItemRequestsAllByOwner(user2.getId(), from, size);

        assertThat(actualList, equalTo(List.of(expectedItemRequestDto)));
    }

    @Test
    void getItemRequestByIdTest() {
        ItemDto itemDto = ItemDto.builder()
                .id(item1.getId())
                .name(item1.getName())
                .description(item1.getDescription())
                .available(item1.getAvailable())
                .requestId(itemRequest1.getId())
                .build();
        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder()
                .id(itemRequest1.getId())
                .description(itemRequest1.getDescription())
                .requester(user2.getId())
                .items(List.of(itemDto))
                .created(itemRequest1.getCreated())
                .build();

        ItemRequestDto actualItemRequestDto = itemRequestService.getItemRequestById(user2.getId(), itemRequest1.getId());

        assertThat(actualItemRequestDto, equalTo(expectedItemRequestDto));
    }
}
