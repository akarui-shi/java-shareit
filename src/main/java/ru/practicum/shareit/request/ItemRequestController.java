package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Add item request for user {}", userId);
        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get item requests for user {}", userId);
        return itemRequestService.getItemRequestsByOwner(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getItemRequestsAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @PositiveOrZero @RequestParam(defaultValue = "0") long from,
                                                          @Positive @RequestParam(defaultValue = "10") long size) {
        log.info("Get all item requests for user {}", userId);
        return itemRequestService.getItemRequestsAllByOwner(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        log.info("Get item request by id {}", requestId);
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
