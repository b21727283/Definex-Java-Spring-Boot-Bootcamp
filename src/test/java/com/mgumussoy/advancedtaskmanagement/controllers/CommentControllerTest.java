package com.mgumussoy.advancedtaskmanagement.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgumussoy.advancedtaskmanagement.configs.GlobalExceptionHandler;
import com.mgumussoy.advancedtaskmanagement.dtos.CommentDTO;
import com.mgumussoy.advancedtaskmanagement.exceptions.CommentNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.TaskNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.UserEntityNotFoundException;
import com.mgumussoy.advancedtaskmanagement.services.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentControllerTest {

    private static final String API_BASE_PATH = "/comments";
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ------------------ Create Comment Tests ------------------
    @Test
    void createComment_ShouldReturnOk() throws Exception {
        CommentDTO requestDto = CommentDTO.builder()
                .text("Test comment")
                .authorId(1L)
                .taskId(1L)
                .build();
        CommentDTO responseDto = CommentDTO.builder()
                .id(1L)
                .text("Test comment")
                .authorId(1L)
                .taskId(1L)
                .build();

        when(commentService.createComment(any(CommentDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post(API_BASE_PATH)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    void createComment_ThrowsTaskNotFoundException() throws Exception {
        CommentDTO requestDto = CommentDTO.builder()
                .text("Test comment")
                .authorId(1L)
                .taskId(1L)
                .build();

        doThrow(new TaskNotFoundException())
                .when(commentService).createComment(any(CommentDTO.class));

        mockMvc.perform(post(API_BASE_PATH)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createComment_ThrowsUserEntityNotFoundException() throws Exception {
        CommentDTO requestDto = CommentDTO.builder()
                .text("Test comment")
                .authorId(1L)
                .taskId(1L)
                .build();

        doThrow(new UserEntityNotFoundException())
                .when(commentService).createComment(any(CommentDTO.class));

        mockMvc.perform(post(API_BASE_PATH)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ------------------ Delete Comment Tests ------------------
    @Test
    void deleteComment_ShouldReturnOk() throws Exception {
        long commentId = 1L;
        mockMvc.perform(delete(API_BASE_PATH + "/" + commentId))
                .andExpect(status().isOk())
                .andExpect(content().string("Comment deleted successfully!"));
    }

    @Test
    void deleteComment_ThrowsCommentNotFoundException() throws Exception {
        long commentId = 1L;
        doThrow(new CommentNotFoundException())
                .when(commentService).deleteComment(anyLong());

        mockMvc.perform(delete(API_BASE_PATH + "/" + commentId))
                .andExpect(status().isNotFound());
    }

    // ------------------ Update Comment Tests ------------------
    @Test
    void updateComment_ShouldReturnOk() throws Exception {
        long commentId = 1L;
        CommentDTO requestDto = CommentDTO.builder()
                .id(commentId)
                .text("Updated comment")
                .authorId(1L)
                .taskId(1L)
                .build();

        mockMvc.perform(put(API_BASE_PATH + "/" + commentId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)));
    }

    @Test
    void updateComment_ThrowsCommentNotFoundException() throws Exception {
        long commentId = 1L;
        CommentDTO requestDto = CommentDTO.builder()
                .id(commentId)
                .text("Updated comment")
                .authorId(1L)
                .taskId(1L)
                .build();

        doThrow(new CommentNotFoundException())
                .when(commentService).updateComment(anyLong(), any(CommentDTO.class));

        mockMvc.perform(put(API_BASE_PATH + "/" + commentId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateComment_ThrowsTaskNotFoundException() throws Exception {
        long commentId = 1L;
        CommentDTO requestDto = CommentDTO.builder()
                .id(commentId)
                .text("Updated comment")
                .authorId(1L)
                .taskId(1L)
                .build();

        doThrow(new TaskNotFoundException())
                .when(commentService).updateComment(anyLong(), any(CommentDTO.class));

        mockMvc.perform(put(API_BASE_PATH + "/" + commentId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateComment_ThrowsUserEntityNotFoundException() throws Exception {
        long commentId = 1L;
        CommentDTO requestDto = CommentDTO.builder()
                .id(commentId)
                .text("Updated comment")
                .authorId(1L)
                .taskId(1L)
                .build();

        doThrow(new UserEntityNotFoundException())
                .when(commentService).updateComment(anyLong(), any(CommentDTO.class));

        mockMvc.perform(put(API_BASE_PATH + "/" + commentId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ------------------ Get Comment Tests ------------------
    @Test
    void getComment_ShouldReturnOk() throws Exception {
        long commentId = 1L;
        CommentDTO responseDto = CommentDTO.builder()
                .id(commentId)
                .text("Test comment")
                .authorId(1L)
                .taskId(1L)
                .build();

        when(commentService.getComment(anyLong())).thenReturn(responseDto);

        mockMvc.perform(get(API_BASE_PATH + "/" + commentId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    void getComment_ThrowsCommentNotFoundException() throws Exception {
        long commentId = 1L;
        doThrow(new CommentNotFoundException())
                .when(commentService).getComment(anyLong());

        mockMvc.perform(get(API_BASE_PATH + "/" + commentId))
                .andExpect(status().isNotFound());
    }
}