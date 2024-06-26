package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.NoFinishBookingForCommentException;
import ru.practicum.shareit.item.exception.NotFoundDataException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    ItemService itemService;

    private ItemDto itemDtoIn;
    private ItemDto itemDtoOut;
    private ItemDto itemDtoUpdatedIn;
    private ItemDto itemDtoUpdatedOut;
    private CommentDto commentDtoIn;
    private CommentDto commentDtoOut;

    @BeforeEach
    void setUp() {
        itemDtoIn = ItemDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .build();
        itemDtoOut = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .build();
        itemDtoUpdatedIn = ItemDto.builder()
                .name("updated")
                .build();
        itemDtoUpdatedOut = ItemDto.builder()
                .id(1L)
                .name("updated")
                .description("description")
                .available(true)
                .build();
        commentDtoIn = CommentDto.builder()
                .text("comment")
                .build();
        commentDtoOut = CommentDto.builder()
                .id(1L)
                .text("comment")
                .authorName("user")
                .created(LocalDateTime.of(2024, 03, 01, 12, 0))
                .build();
    }

    @Test
    void addItemTest() throws Exception {
        long userId = 1L;

        when(itemService.addNewItem(userId, itemDtoIn))
                .thenReturn(itemDtoOut);

        mvc.perform(post("/items")
                    .content(mapper.writeValueAsString(itemDtoIn))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .header("X-Sharer-User-Id", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable())));

        verify(itemService).addNewItem(userId, itemDtoIn);
    }

    @Test
    void updateItemTest() throws Exception {
        long itemId = 1L;
        long userId = 1L;

        when(itemService.updateItem(itemId, userId, itemDtoUpdatedIn))
                .thenReturn(itemDtoUpdatedOut);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(itemDtoUpdatedIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoUpdatedOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoUpdatedOut.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoUpdatedOut.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoUpdatedOut.getAvailable())));

        verify(itemService).updateItem(itemId, userId, itemDtoUpdatedIn);
    }

    @Test
    void updateItemNoSuchItemExceptionTest() throws Exception {
        long itemId = 1L;
        long userId = 1L;

        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenThrow(new NotFoundDataException("Error"));

        mvc.perform(patch("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(itemDtoUpdatedIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemTest() throws Exception {
        long itemId = 1L;
        long userId = 1L;

        when(itemService.getItem(userId, itemId))
                .thenReturn(itemDtoOut);

        mvc.perform(get("/items/{itemId}", itemId)
                    .header("X-Sharer-User-Id", userId)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable())));

        verify(itemService).getItem(userId, itemId);
    }

    @Test
    void getItemNoSuchUserExceptionTest() throws Exception {
        long itemId = 1L;
        long userId = 1L;

        when(itemService.getItem(anyLong(), anyLong())).thenThrow(new NotFoundDataException("Error"));

        mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemNoSuchItemExceptionTest() throws Exception {
        long itemId = 1L;
        long userId = 1L;

        when(itemService.getItem(anyLong(), anyLong())).thenThrow(new NotFoundDataException("Error"));

        mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemsByUserTest() throws Exception {
        long userId = 1L;
        long from = 0;
        long size = 10;

        when(itemService.getItemsByUser(userId))
                .thenReturn(List.of(itemDtoOut));

        mvc.perform(get("/items?from={from}&size={size}", from, size)
                    .header("X-Sharer-User-Id", userId)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoOut.getAvailable())));

        verify(itemService).getItemsByUser(userId);
    }

    @Test
    void getAllAvailableItemsWithTextTest() throws Exception {
        String searchString = "script";

        when(itemService.searchItemsByKeyword(searchString))
                .thenReturn(List.of(itemDtoOut));

        mvc.perform(get("/items/search?text={text}", searchString)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoOut.getAvailable())));

        verify(itemService).searchItemsByKeyword(searchString);
    }

    @Test
    void addCommentTest() throws Exception {
        long userId = 1;
        long itemId = 1;

        when(itemService.addComment(userId, itemId, commentDtoIn))
                .thenReturn(commentDtoOut);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                    .content(mapper.writeValueAsString(commentDtoIn))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .header("X-Sharer-User-Id", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoOut.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoOut.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDtoOut.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));

        verify(itemService).addComment(userId, itemId, commentDtoIn);
    }

    @Test
    void addCommentNoSuchUserExceptionTest() throws Exception {
        long userId = 1;
        long itemId = 1;

        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenThrow(new NotFoundDataException("Error"));

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(mapper.writeValueAsString(commentDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void addCommentNoSuchItemExceptionTest() throws Exception {
        long userId = 1;
        long itemId = 1;

        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenThrow(new NotFoundDataException("Error"));

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(mapper.writeValueAsString(commentDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void addCommentNoFinishBookingForCommentExceptionTest() throws Exception {
        long userId = 1;
        long itemId = 1;

        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenThrow(new NoFinishBookingForCommentException("Error"));

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(mapper.writeValueAsString(commentDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Некорректное описание элемента")
    void invalidDescriptionTest() throws Exception {
        long userId = 1L;
        ItemDto itemDtoIn = ItemDto.builder()
                .name("Test Item")
                .description("")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

}
