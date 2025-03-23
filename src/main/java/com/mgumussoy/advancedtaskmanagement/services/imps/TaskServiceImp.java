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
import com.mgumussoy.advancedtaskmanagement.services.TaskService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImp implements TaskService {

    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;
    private final ProjectRepository projectRepository;
    private final UserEntityRepository userEntityRepository;

    @Autowired
    public TaskServiceImp(TaskRepository taskRepository, ModelMapper modelMapper, ProjectRepository projectRepository, UserEntityRepository userEntityRepository) {
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
        this.projectRepository = projectRepository;
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    @Transactional
    public TaskDTO saveTask(TaskDTO taskDTO) throws UserEntityNotFoundException, ProjectNotFoundException {
        UserEntity userEntity = findAssigneeById(taskDTO.getAssigneeId());
        Project project = findProjectById(taskDTO.getProjectId());

        TaskEntity taskEntity = taskRepository.save(DTOToEntity(taskDTO, userEntity, project));

        userEntity.getTaskEntities().add(taskEntity);
        project.getTaskEntities().add(taskEntity);

        return modelMapper.map(taskEntity, TaskDTO.class);
    }

    @Override
    @Transactional
    public void assignTask(Long taskId, Long userId) throws UserEntityNotFoundException, TaskNotFoundException {
        TaskEntity taskEntity = getTaskEntity(taskId);
        UserEntity newUserEntity = findAssigneeById(userId);

        UserEntity oldUserEntity = taskEntity.getAssignee();
        if (oldUserEntity != null) {
            oldUserEntity.getTaskEntities().remove(taskEntity);
        }

        taskEntity.setAssignee(newUserEntity);
        newUserEntity.getTaskEntities().add(taskEntity);
        taskRepository.save(taskEntity);
    }

    @Override
    public TaskDTO getTask(Long taskId) throws TaskNotFoundException {
        return modelMapper.map(getTaskEntity(taskId), TaskDTO.class);
    }

    @Override
    @Transactional
    public TaskDTO updateTask(Long taskId, TaskDTO taskDTO) throws TaskNotFoundException, UserEntityNotFoundException, ProjectNotFoundException,
            TaskStateCanNotBeChanged, ReasonOfStateChangeMustBeEntered {

        TaskEntity oldTaskEntity = getTaskEntity(taskId);
        UserEntity oldUserEntity = oldTaskEntity.getAssignee();
        Project oldProject = oldTaskEntity.getProject();

        checkTaskState(taskDTO, oldTaskEntity);

        if (oldUserEntity != null) {
            oldUserEntity.getTaskEntities().remove(oldTaskEntity);
        }

        if (oldProject != null) {
            oldProject.getTaskEntities().remove(oldTaskEntity);
        }

        UserEntity newUserEntity = findAssigneeById(taskDTO.getAssigneeId());
        Project newProject = findProjectById(taskDTO.getProjectId());

        TaskEntity newTaskEntity = DTOToEntity(taskDTO, newUserEntity, newProject);
        newTaskEntity.setId(taskId);

        newTaskEntity = taskRepository.save(newTaskEntity);
        newUserEntity.getTaskEntities().add(newTaskEntity);
        newProject.getTaskEntities().add(newTaskEntity);

        return modelMapper.map(newTaskEntity, TaskDTO.class);
    }

    @Override
    public void deleteTask(Long taskId) {
        TaskEntity taskEntity = getTaskEntity(taskId);
        taskEntity.setDeleted(true);

        UserEntity userEntity = taskEntity.getAssignee();
        Project project = taskEntity.getProject();

        if (userEntity != null) {
            userEntity.getTaskEntities().remove(taskEntity);
        }

        if (project != null) {
            project.getTaskEntities().remove(taskEntity);
        }

        taskRepository.save(taskEntity);
    }

    private TaskEntity DTOToEntity(TaskDTO taskDTO, UserEntity userEntity, Project project) throws UserEntityNotFoundException, ProjectNotFoundException {
        return TaskEntity.builder()
                .userStory(taskDTO.getUserStory())
                .acceptanceCriteria(taskDTO.getAcceptanceCriteria())
                .state(taskDTO.getState())
                .priority(taskDTO.getPriority())
                .assignee(userEntity)
                .project(project)
                .build();
    }

    private TaskEntity getTaskEntity(Long taskId) throws TaskNotFoundException {
        TaskEntity taskEntity = taskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        if (taskEntity.isDeleted()) throw new TaskNotFoundException();
        return taskEntity;
    }

    private UserEntity findAssigneeById(Long userId) throws UserEntityNotFoundException {
        UserEntity user = userEntityRepository.findById(userId).orElseThrow(UserEntityNotFoundException::new);
        if (user.isDeleted()) throw new UserEntityNotFoundException();
        return user;
    }

    private Project findProjectById(Long projectId) throws ProjectNotFoundException {
        Project projectEntity = projectRepository.findById(projectId).orElseThrow(ProjectNotFoundException::new);
        if (projectEntity.isDeleted()) throw new ProjectNotFoundException();
        return projectEntity;
    }

    private void checkTaskState(TaskDTO taskDTO, TaskEntity taskEntity) throws TaskStateCanNotBeChanged, ReasonOfStateChangeMustBeEntered {
        TaskState actualState = taskEntity.getState();
        TaskState expectedState = taskDTO.getState();

        if (actualState == TaskState.COMPLETED) {
            throw new TaskStateCanNotBeChanged();
        }

        switch (expectedState) {
            case TaskState.BLOCKED: {
                if (actualState == TaskState.IN_ANALYSIS || actualState == TaskState.IN_DEVELOPMENT) {
                    if (taskDTO.getReasonForStateChange() == null) {
                        throw new ReasonOfStateChangeMustBeEntered();
                    }
                } else {
                    throw new TaskStateCanNotBeChanged();
                }
            }
            case TaskState.CANCELLED:
                if (taskDTO.getReasonForStateChange() == null) {
                    throw new ReasonOfStateChangeMustBeEntered();
                }
            case TaskState.COMPLETED:
                if (actualState != TaskState.IN_DEVELOPMENT) {
                    throw new TaskStateCanNotBeChanged();
                }
            case TaskState.IN_DEVELOPMENT:
                if (!(actualState == TaskState.IN_ANALYSIS)) {
                    throw new TaskStateCanNotBeChanged();
                }
            case TaskState.IN_ANALYSIS:
                if (!(actualState == TaskState.BACKLOG || actualState == TaskState.IN_DEVELOPMENT)) {
                    throw new TaskStateCanNotBeChanged();
                }
            case TaskState.BACKLOG:
                if (actualState != TaskState.IN_ANALYSIS) {
                    throw new TaskStateCanNotBeChanged();
                }
        }
    }

}
