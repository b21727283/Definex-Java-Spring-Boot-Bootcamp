package com.mgumussoy.advancedtaskmanagement.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgumussoy.advancedtaskmanagement.configs.GlobalExceptionHandler;
import com.mgumussoy.advancedtaskmanagement.dtos.DepartmentDTO;
import com.mgumussoy.advancedtaskmanagement.dtos.ProjectDTO;
import com.mgumussoy.advancedtaskmanagement.dtos.UserDTO;
import com.mgumussoy.advancedtaskmanagement.exceptions.DepartmentNotFoundException;
import com.mgumussoy.advancedtaskmanagement.services.DepartmentService;
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

public class DepartmentControllerTest {

    private static final String API_BASE_PATH = "/departments";
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @InjectMocks
    private DepartmentController departmentController;

    @Mock
    private DepartmentService departmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(departmentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ------------------ Create Department Tests ------------------
    @Test
    void createDepartment_ShouldReturnOk() throws Exception {
        DepartmentDTO requestDto = DepartmentDTO.builder()
                .departmentName("HR")
                .build();
        DepartmentDTO responseDto = DepartmentDTO.builder()
                .id(1L)
                .departmentName("HR")
                .build();

        when(departmentService.createDepartment(any(DepartmentDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post(API_BASE_PATH)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    // ------------------ Update Department Tests ------------------
    @Test
    void updateDepartment_ShouldReturnOk() throws Exception {
        Long departmentId = 1L;
        DepartmentDTO requestDto = DepartmentDTO.builder()
                .id(departmentId)
                .departmentName("Finance")
                .build();

        mockMvc.perform(put(API_BASE_PATH + "/" + departmentId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)));
    }

    @Test
    void updateDepartment_ThrowsDepartmentNotFoundException() throws Exception {
        Long departmentId = 1L;
        DepartmentDTO requestDto = DepartmentDTO.builder()
                .id(departmentId)
                .departmentName("Finance")
                .build();

        doThrow(new DepartmentNotFoundException()).when(departmentService)
                .updateDepartment(anyLong(), any(DepartmentDTO.class));

        mockMvc.perform(put(API_BASE_PATH + "/" + departmentId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ------------------ Delete Department Tests ------------------
    @Test
    void deleteDepartment_ShouldReturnOk() throws Exception {
        Long departmentId = 1L;

        mockMvc.perform(delete(API_BASE_PATH + "/" + departmentId))
                .andExpect(status().isOk())
                .andExpect(content().string("Department deleted successfully!"));
    }

    @Test
    void deleteDepartment_ThrowsDepartmentNotFoundException() throws Exception {
        Long departmentId = 1L;
        doThrow(new DepartmentNotFoundException()).when(departmentService)
                .deleteDepartment(anyLong());

        mockMvc.perform(delete(API_BASE_PATH + "/" + departmentId))
                .andExpect(status().isNotFound());
    }

    // ------------------ Get Department Tests ------------------
    @Test
    void getDepartment_ShouldReturnOk() throws Exception {
        Long departmentId = 1L;
        DepartmentDTO responseDto = DepartmentDTO.builder()
                .id(departmentId)
                .departmentName("IT")
                .build();

        when(departmentService.getDepartment(anyLong())).thenReturn(responseDto);

        mockMvc.perform(get(API_BASE_PATH + "/" + departmentId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    void getDepartment_ThrowsDepartmentNotFoundException() throws Exception {
        Long departmentId = 1L;
        doThrow(new DepartmentNotFoundException()).when(departmentService)
                .getDepartment(anyLong());

        mockMvc.perform(get(API_BASE_PATH + "/" + departmentId))
                .andExpect(status().isNotFound());
    }

    // ------------------ Get Projects Tests ------------------
    @Test
    void getProjects_ShouldReturnOk() throws Exception {
        Long departmentId = 1L;
        List<ProjectDTO> projects = Collections.singletonList(ProjectDTO.builder()
                .id(1L)
                .title("X Project")
                .build());

        when(departmentService.getProjects(anyLong())).thenReturn(projects);

        mockMvc.perform(get(API_BASE_PATH + "/" + departmentId + "/projects"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(projects)));
    }

    // ------------------ Get Users Tests ------------------
    @Test
    void getUsers_ShouldReturnOk() throws Exception {
        Long departmentId = 1L;
        List<UserDTO> users = Collections.singletonList(UserDTO.builder()
                .id(1L)
                .username("john.doe")
                .build());

        when(departmentService.getUsers(anyLong())).thenReturn(users);

        mockMvc.perform(get(API_BASE_PATH + "/" + departmentId + "/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    void getUsers_ThrowsDepartmentNotFoundException() throws Exception {
        Long departmentId = 1L;
        doThrow(new DepartmentNotFoundException()).when(departmentService)
                .getUsers(anyLong());

        mockMvc.perform(get(API_BASE_PATH + "/" + departmentId + "/users"))
                .andExpect(status().isNotFound());
    }
}