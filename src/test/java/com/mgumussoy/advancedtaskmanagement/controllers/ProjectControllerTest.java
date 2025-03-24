package com.mgumussoy.advancedtaskmanagement.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgumussoy.advancedtaskmanagement.configs.GlobalExceptionHandler;
import com.mgumussoy.advancedtaskmanagement.dtos.ProjectDTO;
import com.mgumussoy.advancedtaskmanagement.dtos.TaskDTO;
import com.mgumussoy.advancedtaskmanagement.enums.ProjectStatus;
import com.mgumussoy.advancedtaskmanagement.exceptions.DepartmentNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.ProjectNotFoundException;
import com.mgumussoy.advancedtaskmanagement.services.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectControllerTest {

    private static final String API_BASE_PATH = "/projects";
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProjectController projectController;

    @Mock
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ------------------ Create Project Tests ------------------
    @Test
    void createProject_ShouldReturnOk() throws Exception {
        ProjectDTO requestDto = ProjectDTO.builder()
                .department_id(1L)
                .title("Project X")
                .description("Description of project X")
                .status(ProjectStatus.COMPLETED)
                .build();

        ProjectDTO responseDto = ProjectDTO.builder()
                .id(1L)
                .department_id(1L)
                .title("Project X")
                .description("Description of project X")
                .status(ProjectStatus.CANCELLED)
                .build();

        when(projectService.createProject(any(ProjectDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post(API_BASE_PATH)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    void createProject_ThrowsDepartmentNotFoundException() throws Exception {
        ProjectDTO requestDto = ProjectDTO.builder()
                .department_id(1L)
                .title("Project X")
                .description("Description of project X")
                .status(ProjectStatus.COMPLETED)
                .build();

        doThrow(new DepartmentNotFoundException())
                .when(projectService).createProject(any(ProjectDTO.class));

        mockMvc.perform(post(API_BASE_PATH)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ------------------ Delete Project Tests ------------------
    @Test
    void deleteProject_ShouldReturnOk() throws Exception {
        long projectId = 1L;

        mockMvc.perform(delete(API_BASE_PATH + "/" + projectId))
                .andExpect(status().isOk())
                .andExpect(content().string("Project deleted successfully!"));
    }

    @Test
    void deleteProject_ThrowsProjectNotFoundException() throws Exception {
        long projectId = 1L;

        doThrow(new ProjectNotFoundException())
                .when(projectService).deleteProject(anyLong());

        mockMvc.perform(delete(API_BASE_PATH + "/" + projectId))
                .andExpect(status().isNotFound());
    }

    // ------------------ Update Project Tests ------------------
    @Test
    void updateProject_ShouldReturnOk() throws Exception {
        Long projectId = 1L;
        ProjectDTO requestDto = ProjectDTO.builder()
                .id(projectId)
                .department_id(1L)
                .title("Updated Project X")
                .description("Updated description")
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        mockMvc.perform(put(API_BASE_PATH + "/" + projectId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)));
    }

    @Test
    void updateProject_ThrowsProjectNotFoundException() throws Exception {
        Long projectId = 1L;
        ProjectDTO requestDto = ProjectDTO.builder()
                .id(projectId)
                .department_id(1L)
                .title("Updated Project X")
                .description("Updated description")
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        doThrow(new ProjectNotFoundException())
                .when(projectService).updateProject(anyLong(), any(ProjectDTO.class));

        mockMvc.perform(put(API_BASE_PATH + "/" + projectId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ------------------ Get Project Tests ------------------
    @Test
    void getProject_ShouldReturnOk() throws Exception {
        Long projectId = 1L;
        ProjectDTO responseDto = ProjectDTO.builder()
                .id(projectId)
                .department_id(1L)
                .title("Project X")
                .description("Description of project X")
                .status(null)
                .build();

        when(projectService.getProject(anyLong())).thenReturn(responseDto);

        mockMvc.perform(get(API_BASE_PATH + "/" + projectId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    void getProject_ThrowsProjectNotFoundException() throws Exception {
        long projectId = 1L;

        doThrow(new ProjectNotFoundException())
                .when(projectService).getProject(anyLong());

        mockMvc.perform(get(API_BASE_PATH + "/" + projectId))
                .andExpect(status().isNotFound());
    }

    // ------------------ Get Tasks of Project Tests ------------------
    @Test
    void getTasksOfProject_ShouldReturnOk() throws Exception {
        long projectId = 1L;
        List<TaskDTO> tasks = Collections.singletonList(TaskDTO.builder()
                .id(1L)
                .acceptanceCriteria("aa")
                .build());

        when(projectService.getTasksOfProject(anyLong())).thenReturn(tasks);

        mockMvc.perform(get(API_BASE_PATH + "/" + projectId + "/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(tasks)));
    }

    @Test
    void getTasksOfProject_ThrowsProjectNotFoundException() throws Exception {
        long projectId = 1L;

        doThrow(new ProjectNotFoundException())
                .when(projectService).getTasksOfProject(anyLong());

        mockMvc.perform(get(API_BASE_PATH + "/" + projectId + "/tasks"))
                .andExpect(status().isNotFound());
    }
}