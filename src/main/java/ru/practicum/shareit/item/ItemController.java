package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get items for user {}", userId);
        return itemService.getItemsByUser(userId);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                    @Valid @RequestBody ItemDto item) {
        log.info("Add item for user {}", userId);
        return itemService.addNewItem(userId, item);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                        @PathVariable("id") long id) {
        log.info("Get item by id {}", id);
        return itemService.getItem(userId, id);
    }

    @PatchMapping("/{id}")
    public ItemDto edit(@RequestHeader("X-Sharer-User-Id") long userId,
                     @PathVariable("id") long id,
                     @RequestBody ItemDto item) {
        log.info("Edit item by id {}", id);
        return itemService.updateItem(userId, id, item);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByKeyword(@RequestParam("text") String keyword) {
        log.info("Search items by keyword {}", keyword);
        return itemService.searchItemsByKeyword(keyword);
    }
}
