package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CommentMapperTest {

    @Test
    void commentToDtoTest() {
        User owner = User.builder().id(1L).name("owner").email("owner@yandex.ru").build();
        User commenter = User.builder().id(2L).name("commenter").email("commenter@yandex.ru").build();
        LocalDateTime createdTime = LocalDateTime.now();
        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .text("comment")
                .item(item)
                .author(commenter)
                .created(createdTime)
                .build();

        CommentDto expectedCommentDto = CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated()).build();

        CommentDto actualCommentDto = CommentMapper.toDto(comment);

        assertThat(actualCommentDto, equalTo(expectedCommentDto));
    }

    @Test
    void commentFromDtoTest() {
        User owner = User.builder().id(1L).name("owner").email("owner@yandex.ru").build();
        User commenter = User.builder().id(2L).name("commenter").email("commenter@yandex.ru").build();
        LocalDateTime createdTime = LocalDateTime.now();
        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        CommentDto commentDto = CommentDto.builder().text("comment").build();

        Comment expectedComment = Comment.builder()
                .id(0L)
                .text(commentDto.getText())
                .item(item)
                .author(commenter)
                .created(createdTime)
                .build();

        Comment actualComment = CommentMapper.fromDto(commentDto, commenter, item, createdTime);

        assertThat(actualComment.getId(), equalTo(expectedComment.getId()));
        assertThat(actualComment.getText(), equalTo(expectedComment.getText()));
        assertThat(actualComment.getItem().getId(), equalTo(expectedComment.getItem().getId()));
        assertThat(actualComment.getAuthor().getId(), equalTo(expectedComment.getAuthor().getId()));
        assertThat(actualComment.getCreated(), equalTo(expectedComment.getCreated()));
    }
}
