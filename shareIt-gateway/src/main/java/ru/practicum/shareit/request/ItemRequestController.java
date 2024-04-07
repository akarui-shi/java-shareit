package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Add item request for user {}", userId);
        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get item requests for user {}", userId);
        return itemRequestClient.getItemRequestsByOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @PositiveOrZero @RequestParam(defaultValue = "0") long from,
                                                          @Positive @RequestParam(defaultValue = "10") long size) {
        log.info("Get all item requests for user {}", userId);
        return itemRequestClient.getItemRequestsAllByOwner(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        log.info("Get item request by id {}", requestId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
