package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get items for user {}", userId);
        return itemClient.getItemsByUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") long userId,
                       @Valid @RequestBody ItemDto item) {
        log.info("Add item for user {}", userId);
        return itemClient.addNewItem(userId, item);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable("id") long id) {
        log.info("Get item by id {}", id);
        return itemClient.getItem(userId, id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> edit(@RequestHeader("X-Sharer-User-Id") long userId,
                        @PathVariable("id") long id,
                        @RequestBody ItemDto item) {
        log.info("Edit item by id {}", id);
        return itemClient.updateItem(userId, id, item);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByKeyword(@RequestParam("text") String keyword) {
        log.info("Search items by keyword {}", keyword);
        return itemClient.searchItemsByKeyword(keyword);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }

}
