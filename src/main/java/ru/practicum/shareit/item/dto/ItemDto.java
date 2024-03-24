package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    private ItemRequest request;
    @NotNull
    private Boolean available;
}
