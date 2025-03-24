package com.mgumussoy.advancedtaskmanagement.services.imps;

import com.mgumussoy.advancedtaskmanagement.dtos.CommentDTO;
import com.mgumussoy.advancedtaskmanagement.entities.Comment;
import com.mgumussoy.advancedtaskmanagement.entities.TaskEntity;
import com.mgumussoy.advancedtaskmanagement.entities.UserEntity;
import com.mgumussoy.advancedtaskmanagement.exceptions.CommentNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.TaskNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.UserEntityNotFoundException;
import com.mgumussoy.advancedtaskmanagement.repositories.CommentRepository;
import com.mgumussoy.advancedtaskmanagement.repositories.TaskRepository;
import com.mgumussoy.advancedtaskmanagement.repositories.UserEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentServiceImpTest {

    @InjectMocks
    private CommentServiceImp commentServiceImp;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserEntityRepository userEntityRepository;

    private TaskEntity task;
    private UserEntity user;
    private Comment comment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        task = new TaskEntity();
        task.setId(10L);
        user = new UserEntity();
        user.setId(20L);
        comment = Comment.builder().text("Initial text").taskEntity(task).author(user).build();
        comment.setId(1L);
    }

    @Test
    void createComment_success() throws TaskNotFoundException, UserEntityNotFoundException {
        CommentDTO dto = CommentDTO.builder().text("New comment").taskId(10L).authorId(20L).build();
        Comment savedComment = Comment.builder().text("New comment").taskEntity(task).author(user).build();
        savedComment.setId(1L);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(userEntityRepository.findById(20L)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
        when(modelMapper.map(any(Comment.class), eq(CommentDTO.class))).thenReturn(CommentDTO.builder().id(1L)
                .text("New comment").taskId(10L).authorId(20L).build());

        CommentDTO result = commentServiceImp.createComment(dto);
        assertEquals(1L, result.getId());
        assertEquals("New comment", result.getText());
    }

    @Test
    void getComment_success() throws CommentNotFoundException {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(modelMapper.map(comment, CommentDTO.class)).thenReturn(CommentDTO.builder()
                .id(1L).text("Initial text").taskId(10L).authorId(20L).build());

        CommentDTO dto = commentServiceImp.getComment(1L);
        assertEquals("Initial text", dto.getText());
    }

    @Test
    void getComment_NotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () -> commentServiceImp.getComment(1L));
    }

    @Test
    void deleteComment_success() throws CommentNotFoundException {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        commentServiceImp.deleteComment(1L);
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        assertTrue(captor.getValue().isDeleted());
    }

    @Test
    void deleteComment_CommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () -> commentServiceImp.deleteComment(1L));
    }

    @Test
    void updateComment_success() throws TaskNotFoundException, UserEntityNotFoundException, CommentNotFoundException {
        CommentDTO newDto = CommentDTO.builder().text("Updated").taskId(10L).authorId(20L).build();
        TaskEntity newTask = new TaskEntity();
        newTask.setId(10L);
        UserEntity newUser = new UserEntity();
        newUser.setId(20L);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(taskRepository.findById(10L)).thenReturn(Optional.of(newTask));
        when(userEntityRepository.findById(20L)).thenReturn(Optional.of(newUser));
        commentServiceImp.updateComment(1L, newDto);

        assertEquals("Updated", comment.getText());
        assertSame(newTask, comment.getTaskEntity());
        assertSame(newUser, comment.getAuthor());
        verify(commentRepository, atLeastOnce()).save(comment);
    }

    @Test
    void updateComment_CommentNotFound() {
        CommentDTO newDto = CommentDTO.builder().text("Updated").taskId(10L).authorId(20L).build();
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () -> commentServiceImp.updateComment(1L, newDto));
    }

    @Test
    void updateComment_TaskNotFound() {
        CommentDTO newDto = CommentDTO.builder().text("Updated").taskId(10L).authorId(20L).build();
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(taskRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> commentServiceImp.updateComment(1L, newDto));
    }

    @Test
    void updateComment_UserEntityNotFound() {
        CommentDTO newDto = CommentDTO.builder().text("Updated").taskId(10L).authorId(20L).build();
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(userEntityRepository.findById(20L)).thenReturn(Optional.empty());
        assertThrows(UserEntityNotFoundException.class, () -> commentServiceImp.updateComment(1L, newDto));
    }
}