package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<Item> getByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get items for user {}", userId);
        return itemService.getItemsByUser(userId);
    }

    @PostMapping
    public Item add(@RequestHeader("X-Sharer-User-Id") long userId,
                    @Valid @RequestBody ItemDto item) {
        log.info("Add item for user {}", userId);
        return itemService.addNewItem(userId, item);
    }

    @GetMapping("/{id}")
    public Item getById(@RequestHeader("X-Sharer-User-Id") long userId,
                        @PathVariable("id") long id) {
        log.info("Get item by id {}", id);
        return itemService.getItem(userId, id);
    }

    @PatchMapping("/{id}")
    public Item edit(@RequestHeader("X-Sharer-User-Id") long userId,
                     @PathVariable("id") long id,
                     @RequestBody ItemDto item) {
        log.info("Edit item by id {}", id);
        return itemService.updateItem(userId, id, item);
    }

    @GetMapping("/search")
    public List<Item> searchItemsByKeyword(@RequestParam("text") String keyword) {
        log.info("Search items by keyword {}", keyword);
        return itemService.searchItemsByKeyword(keyword);
    }
}
