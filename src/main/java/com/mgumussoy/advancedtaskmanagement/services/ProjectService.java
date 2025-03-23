package com.mgumussoy.advancedtaskmanagement.services;

import com.mgumussoy.advancedtaskmanagement.dtos.ProjectDTO;
import com.mgumussoy.advancedtaskmanagement.dtos.TaskDTO;
import com.mgumussoy.advancedtaskmanagement.exceptions.DepartmentNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.ProjectNotFoundException;

import java.util.List;

public interface ProjectService {
    List<TaskDTO> getTasksOfProject(Long projectId) throws ProjectNotFoundException;

    ProjectDTO createProject(ProjectDTO projectDTO) throws DepartmentNotFoundException;

    void deleteProject(Long projectId) throws ProjectNotFoundException;

    void updateProject(Long projectId, ProjectDTO newProjectDTO) throws ProjectNotFoundException;

    ProjectDTO getProject(Long projectId) throws ProjectNotFoundException;
}
