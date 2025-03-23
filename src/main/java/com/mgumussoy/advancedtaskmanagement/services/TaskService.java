package com.mgumussoy.advancedtaskmanagement.services;

import com.mgumussoy.advancedtaskmanagement.dtos.TaskDTO;

public interface TaskService {
    TaskDTO saveTask(TaskDTO taskDTO);

    void assignTask(Long taskId, Long userId);

    TaskDTO getTask(Long taskId);

    TaskDTO updateTask(Long taskId, TaskDTO taskDTO);

    void deleteTask(Long taskId);
}
