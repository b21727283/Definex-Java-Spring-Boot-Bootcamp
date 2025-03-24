package com.mgumussoy.advancedtaskmanagement.services.imps;

import com.mgumussoy.advancedtaskmanagement.dtos.TaskDTO;
import com.mgumussoy.advancedtaskmanagement.entities.Project;
import com.mgumussoy.advancedtaskmanagement.entities.TaskEntity;
import com.mgumussoy.advancedtaskmanagement.entities.UserEntity;
import com.mgumussoy.advancedtaskmanagement.enums.TaskState;
import com.mgumussoy.advancedtaskmanagement.exceptions.*;
import com.mgumussoy.advancedtaskmanagement.repositories.ProjectRepository;
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

public class TaskServiceImpTest {

    @InjectMocks
    private TaskServiceImp taskServiceImp;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserEntityRepository userEntityRepository;

    private TaskEntity taskEntity;
    private Project project;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new UserEntity();
        user.setId(10L);
        project = new Project();
        project.setId(20L);
        taskEntity = TaskEntity.builder()
                .userStory("Story")
                .acceptanceCriteria("Criteria")
                .state(TaskState.BACKLOG)
                .assignee(user)
                .project(project)
                .build();
        taskEntity.setId(1L);
    }

    @Test
    void saveTask_success() throws UserEntityNotFoundException, ProjectNotFoundException {
        TaskDTO dto = TaskDTO.builder()
                .userStory("Story")
                .acceptanceCriteria("Criteria")
                .state(TaskState.BACKLOG)
                .assigneeId(10L)
                .projectId(20L)
                .build();
        when(userEntityRepository.findById(10L)).thenReturn(Optional.of(user));
        when(projectRepository.findById(20L)).thenReturn(Optional.of(project));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);
        when(modelMapper.map(taskEntity, TaskDTO.class)).thenReturn(dto);
        TaskDTO result = taskServiceImp.saveTask(dto);
        assertEquals("Story", result.getUserStory());
    }

    @Test
    void saveTask_UserNotFound() {
        TaskDTO dto = TaskDTO.builder().assigneeId(10L).projectId(20L).build();
        when(userEntityRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(UserEntityNotFoundException.class, () -> taskServiceImp.saveTask(dto));
    }

    @Test
    void getTask_success() throws TaskNotFoundException {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        when(modelMapper.map(taskEntity, TaskDTO.class)).thenReturn(TaskDTO.builder().id(1L).build());
        TaskDTO dto = taskServiceImp.getTask(1L);
        assertEquals(1L, dto.getId());
    }

    @Test
    void getTask_NotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> taskServiceImp.getTask(1L));
    }

    @Test
    void deleteTask_success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        taskServiceImp.deleteTask(1L);
        ArgumentCaptor<TaskEntity> captor = ArgumentCaptor.forClass(TaskEntity.class);
        verify(taskRepository).save(captor.capture());
        assertTrue(captor.getValue().isDeleted());
    }

    @Test
    void assignTask_success() throws UserEntityNotFoundException, TaskNotFoundException {
        UserEntity newUser = new UserEntity();
        newUser.setId(30L);
        taskEntity.setAssignee(user);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        when(userEntityRepository.findById(30L)).thenReturn(Optional.of(newUser));
        taskServiceImp.assignTask(1L, 30L);
        assertSame(newUser, taskEntity.getAssignee());
        verify(taskRepository).save(taskEntity);
    }

    @Test
    void updateTask_success() throws UserEntityNotFoundException, ProjectNotFoundException, TaskNotFoundException {
        TaskDTO dto = TaskDTO.builder()
                .userStory("Updated Story")
                .acceptanceCriteria("Updated Criteria")
                .state(TaskState.IN_ANALYSIS)
                .assigneeId(10L)
                .projectId(20L)
                .build();
        dto.setId(1L);

        taskEntity.setState(TaskState.BACKLOG);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        when(userEntityRepository.findById(10L)).thenReturn(Optional.of(user));
        when(projectRepository.findById(20L)).thenReturn(Optional.of(project));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);
        when(modelMapper.map(any(TaskEntity.class), eq(TaskDTO.class))).thenReturn(dto);

        TaskDTO result = taskServiceImp.updateTask(1L, dto);
        assertEquals("Updated Story", result.getUserStory());
    }

    @Test
    void saveTask_ProjectNotFound() {
        TaskDTO dto = TaskDTO.builder()
                .userStory("Story")
                .acceptanceCriteria("Criteria")
                .state(TaskState.BACKLOG)
                .assigneeId(user.getId())
                .projectId(project.getId())
                .build();
        when(userEntityRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> taskServiceImp.saveTask(dto));
    }

    @Test
    void assignTask_TaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> taskServiceImp.assignTask(1L, 30L));
    }

    @Test
    void assignTask_UserNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        when(userEntityRepository.findById(30L)).thenReturn(Optional.empty());
        assertThrows(UserEntityNotFoundException.class, () -> taskServiceImp.assignTask(1L, 30L));
    }

    @Test
    void updateTask_TaskNotFound() {
        TaskDTO dto = TaskDTO.builder()
                .userStory("Updated Story")
                .acceptanceCriteria("Updated Criteria")
                .state(TaskState.BACKLOG)
                .assigneeId(user.getId())
                .projectId(project.getId())
                .build();
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> taskServiceImp.updateTask(1L, dto));
    }

    @Test
    void updateTask_UserNotFound() {
        TaskDTO dto = TaskDTO.builder()
                .userStory("Updated Story")
                .acceptanceCriteria("Updated Criteria")
                .state(TaskState.IN_ANALYSIS)
                .assigneeId(999L)
                .projectId(project.getId())
                .build();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        when(userEntityRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(UserEntityNotFoundException.class, () -> taskServiceImp.updateTask(1L, dto));
    }

    @Test
    void updateTask_ProjectNotFound() {
        TaskDTO dto = TaskDTO.builder()
                .userStory("Updated Story")
                .acceptanceCriteria("Updated Criteria")
                .state(TaskState.IN_ANALYSIS)
                .assigneeId(user.getId())
                .projectId(999L)
                .build();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        when(userEntityRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> taskServiceImp.updateTask(1L, dto));
    }

    @Test
    void updateTask_TaskStateCanNotBeChanged() {
        taskEntity.setState(TaskState.COMPLETED);
        TaskDTO dto = TaskDTO.builder()
                .userStory("Update Story")
                .acceptanceCriteria("Update Criteria")
                .state(TaskState.BACKLOG)
                .assigneeId(user.getId())
                .projectId(project.getId())
                .build();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        when(userEntityRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        assertThrows(TaskStateCanNotBeChanged.class, () -> taskServiceImp.updateTask(1L, dto));
    }

    @Test
    void updateTask_ReasonOfStateChangeMustBeEntered() {
        taskEntity.setState(TaskState.IN_DEVELOPMENT);
        TaskDTO dto = TaskDTO.builder()
                .userStory("New Story")
                .acceptanceCriteria("New Criteria")
                .state(TaskState.BLOCKED)
                .assigneeId(user.getId())
                .projectId(project.getId())
                .build();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
        when(userEntityRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        assertThrows(ReasonOfStateChangeMustBeEntered.class, () -> taskServiceImp.updateTask(1L, dto));
    }
}