package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class Item {
    private long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    private long ownerId;
    private ItemRequest request;
    @NotNull
    private Boolean available;
}
