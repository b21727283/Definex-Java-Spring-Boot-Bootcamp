package com.mgumussoy.advancedtaskmanagement.services.imps;

import com.mgumussoy.advancedtaskmanagement.dtos.ProjectDTO;
import com.mgumussoy.advancedtaskmanagement.dtos.TaskDTO;
import com.mgumussoy.advancedtaskmanagement.entities.Department;
import com.mgumussoy.advancedtaskmanagement.entities.Project;
import com.mgumussoy.advancedtaskmanagement.exceptions.DepartmentNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.ProjectNotFoundException;
import com.mgumussoy.advancedtaskmanagement.repositories.DepartmentRepository;
import com.mgumussoy.advancedtaskmanagement.repositories.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectServiceImpTest {

    @InjectMocks
    private ProjectServiceImp projectServiceImp;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ModelMapper modelMapper;

    private Department department;
    private Project project;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        department = new Department();
        department.setId(1L);
        project = Project.builder().title("Proj1").description("Desc").build();
        project.setId(1L);
        project.setDepartment(department);
    }

    @Test
    void getTasksOfProject_success() throws ProjectNotFoundException {
        project.setTaskEntities(new ArrayList<>());
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        List<TaskDTO> tasks = projectServiceImp.getTasksOfProject(1L);
        assertNotNull(tasks);
    }

    @Test
    void createProject_success() throws DepartmentNotFoundException {
        ProjectDTO dto = ProjectDTO.builder().department_id(1L).title("Proj1").description("Desc").status(null).build();
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(modelMapper.map(project, ProjectDTO.class)).thenReturn(ProjectDTO.builder().id(1L).title("Proj1").build());
        ProjectDTO result = projectServiceImp.createProject(dto);
        assertEquals(1L, result.getId());
    }

    @Test
    void createProject_DepartmentNotFound() {
        ProjectDTO dto = ProjectDTO.builder().department_id(1L).build();
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> projectServiceImp.createProject(dto));
    }

    @Test
    void getProject_success() throws ProjectNotFoundException {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(modelMapper.map(project, ProjectDTO.class)).thenReturn(ProjectDTO.builder().id(1L).title("Proj1").build());
        ProjectDTO result = projectServiceImp.getProject(1L);
        assertEquals("Proj1", result.getTitle());
    }

    @Test
    void getProject_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> projectServiceImp.getProject(1L));
    }

    @Test
    void deleteProject_success() throws ProjectNotFoundException {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        projectServiceImp.deleteProject(1L);
        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());
        assertTrue(captor.getValue().isDeleted());
    }

    @Test
    void updateProject_success() throws ProjectNotFoundException, DepartmentNotFoundException {
        ProjectDTO dto = ProjectDTO.builder().title("Updated").department_id(1L).description("NewDesc").status(null).build();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        projectServiceImp.updateProject(1L, dto);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void getTasksOfProject_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> projectServiceImp.getTasksOfProject(1L));
    }

    @Test
    void getProject_DeletedProject() {
        project.setDeleted(true);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        assertThrows(ProjectNotFoundException.class, () -> projectServiceImp.getProject(1L));
    }

    @Test
    void deleteProject_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> projectServiceImp.deleteProject(1L));
    }

    @Test
    void updateProject_NotFound() {
        ProjectDTO dto = ProjectDTO.builder().title("Updated").department_id(1L).description("NewDesc").status(null).build();
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> projectServiceImp.updateProject(1L, dto));
    }

    @Test
    void updateProject_DepartmentDeleted() {
        ProjectDTO dto = ProjectDTO.builder().title("Updated").department_id(1L).description("NewDesc").status(null).build();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        department.setDeleted(true);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        assertThrows(DepartmentNotFoundException.class, () -> projectServiceImp.updateProject(1L, dto));
    }
}