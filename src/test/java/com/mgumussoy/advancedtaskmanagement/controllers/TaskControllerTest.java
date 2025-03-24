package com.mgumussoy.advancedtaskmanagement.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgumussoy.advancedtaskmanagement.configs.GlobalExceptionHandler;
import com.mgumussoy.advancedtaskmanagement.dtos.TaskDTO;
import com.mgumussoy.advancedtaskmanagement.enums.TaskPriority;
import com.mgumussoy.advancedtaskmanagement.enums.TaskState;
import com.mgumussoy.advancedtaskmanagement.exceptions.TaskNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.UserEntityNotFoundException;
import com.mgumussoy.advancedtaskmanagement.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TaskControllerTest {

    private static final String API_BASE_PATH = "/tasks";
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ------------------ Create Task Tests ------------------
    @Test
    void createTask_ShouldReturnOk() throws Exception {
        TaskDTO requestDto = TaskDTO.builder()
                .userStory("User story")
                .acceptanceCriteria("Acceptance criteria")
                .state(TaskState.COMPLETED)
                .priority(TaskPriority.MEDIUM)
                .assigneeId(Long.valueOf(1L))
                .projectId(Long.valueOf(1L))
                .build();

        TaskDTO responseDto = TaskDTO.builder()
                .id(Long.valueOf(1L))
                .userStory("User story")
                .acceptanceCriteria("Acceptance criteria")
                .state(TaskState.COMPLETED)
                .priority(TaskPriority.HIGH)
                .assigneeId(Long.valueOf(1L))
                .projectId(Long.valueOf(1L))
                .build();

        when(taskService.saveTask(any(TaskDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post(API_BASE_PATH + "/create")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    void createTask_ThrowsTaskNotFoundException() throws Exception {
        TaskDTO requestDto = TaskDTO.builder()
                .userStory("User story")
                .acceptanceCriteria("Acceptance criteria")
                .state(TaskState.IN_ANALYSIS)
                .priority(TaskPriority.LOW)
                .assigneeId(Long.valueOf(1L))
                .projectId(Long.valueOf(1L))
                .build();

        doThrow(new TaskNotFoundException())
                .when(taskService).saveTask(any(TaskDTO.class));

        mockMvc.perform(post(API_BASE_PATH + "/create")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ------------------ Update Task Tests ------------------
    @Test
    void updateTask_ShouldReturnOk() throws Exception {
        Long taskId = (Long) 1L;
        TaskDTO requestDto = TaskDTO.builder()
                .id(taskId)
                .userStory("Updated user story")
                .acceptanceCriteria("Updated criteria")
                .state(TaskState.BLOCKED)
                .priority(TaskPriority.HIGHEST)
                .assigneeId(Long.valueOf(1L))
                .projectId(Long.valueOf(1L))
                .build();

        when(taskService.updateTask(any(), any(TaskDTO.class))).thenReturn(requestDto);

        mockMvc.perform(put(API_BASE_PATH + "/" + taskId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)));
    }

    @Test
    void updateTask_ThrowsTaskNotFoundException() throws Exception {
        Long taskId = (Long) 1L;
        TaskDTO requestDto = TaskDTO.builder()
                .id(taskId)
                .userStory("Updated user story")
                .acceptanceCriteria("Updated criteria")
                .state(TaskState.COMPLETED)
                .priority(TaskPriority.HIGHEST)
                .assigneeId(Long.valueOf(1L))
                .projectId(Long.valueOf(1L))
                .build();

        doThrow(new TaskNotFoundException())
                .when(taskService).updateTask(any(), any(TaskDTO.class));

        mockMvc.perform(put(API_BASE_PATH + "/" + taskId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ------------------ Delete Task Tests ------------------
    @Test
    void deleteTask_ShouldReturnOk() throws Exception {
        Long taskId = (Long) 1L;
        mockMvc.perform(delete(API_BASE_PATH + "/" + taskId))
                .andExpect(status().isOk())
                .andExpect(content().string("Task deleted successfully!"));
    }

    @Test
    void deleteTask_ThrowsTaskNotFoundException() throws Exception {
        Long taskId = (Long) 1L;
        doThrow(new TaskNotFoundException())
                .when(taskService).deleteTask(any());

        mockMvc.perform(delete(API_BASE_PATH + "/" + taskId))
                .andExpect(status().isNotFound());
    }

    // ------------------ Get Task Tests ------------------
    @Test
    void getTask_ShouldReturnOk() throws Exception {
        Long taskId = (Long) 1L;
        TaskDTO responseDto = TaskDTO.builder()
                .id(taskId)
                .userStory("User story")
                .acceptanceCriteria("Acceptance criteria")
                .state(TaskState.BLOCKED)
                .priority(TaskPriority.HIGHEST)
                .assigneeId(Long.valueOf(1L))
                .projectId(Long.valueOf(1L))
                .build();

        when(taskService.getTask(any())).thenReturn(responseDto);

        mockMvc.perform(get(API_BASE_PATH + "/" + taskId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    void getTask_ThrowsTaskNotFoundException() throws Exception {
        Long taskId = (Long) 1L;

        doThrow(new TaskNotFoundException())
                .when(taskService).getTask(any());

        mockMvc.perform(get(API_BASE_PATH + "/" + taskId))
                .andExpect(status().isNotFound());
    }

    // ------------------ Assign Task Tests ------------------
    @Test
    void assignTask_ShouldReturnOk() throws Exception {
        Long taskId = (Long) 1L;
        Long userId = (Long) 2L;

        mockMvc.perform(post(API_BASE_PATH + "/" + taskId + "/assign/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().string("Task assigned successfully!"));
    }

    @Test
    void assignTask_ThrowsTaskNotFoundException() throws Exception {
        Long taskId = (Long) 1L;
        Long userId = (Long) 2L;

        doThrow(new TaskNotFoundException())
                .when(taskService).assignTask(any(), any());

        mockMvc.perform(post(API_BASE_PATH + "/" + taskId + "/assign/" + userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void assignTask_ThrowsUserEntityNotFoundException() throws Exception {
        Long taskId = (Long) 1L;
        Long userId = (Long) 2L;

        doThrow(new UserEntityNotFoundException())
                .when(taskService).assignTask(any(), any());

        mockMvc.perform(post(API_BASE_PATH + "/" + taskId + "/assign/" + userId))
                .andExpect(status().isNotFound());
    }
}