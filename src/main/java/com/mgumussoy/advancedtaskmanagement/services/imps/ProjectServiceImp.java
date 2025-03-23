package com.mgumussoy.advancedtaskmanagement.services.imps;

import com.mgumussoy.advancedtaskmanagement.dtos.ProjectDTO;
import com.mgumussoy.advancedtaskmanagement.dtos.TaskDTO;
import com.mgumussoy.advancedtaskmanagement.entities.Department;
import com.mgumussoy.advancedtaskmanagement.entities.Project;
import com.mgumussoy.advancedtaskmanagement.exceptions.DepartmentNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.ProjectNotFoundException;
import com.mgumussoy.advancedtaskmanagement.repositories.DepartmentRepository;
import com.mgumussoy.advancedtaskmanagement.repositories.ProjectRepository;
import com.mgumussoy.advancedtaskmanagement.services.ProjectService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImp implements ProjectService {
    private final ProjectRepository projectRepository;
    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ProjectServiceImp(ProjectRepository projectRepository, DepartmentRepository departmentRepository,
                             ModelMapper modelMapper) {
        this.projectRepository = projectRepository;
        this.departmentRepository = departmentRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<TaskDTO> getTasksOfProject(Long projectId) throws ProjectNotFoundException {
        Project project = findProjectById(projectId);
        return project.getTaskEntities().stream().map(task -> modelMapper.map(task, TaskDTO.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectDTO createProject(ProjectDTO projectDTO) throws DepartmentNotFoundException {
        Department department = findDepartmentById(projectDTO.getDepartment_id());
        Project project = projectRepository.save(dtoToEntity(projectDTO, department));
        department.getProjects().add(project);
        return modelMapper.map(project, ProjectDTO.class);
    }

    @Override
    @Transactional
    public void deleteProject(Long projectId) throws ProjectNotFoundException {
        Project project = findProjectById(projectId);
        project.setDeleted(true);
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void updateProject(Long projectId, ProjectDTO newProjectDTO) throws ProjectNotFoundException {
        Project project = findProjectById(projectId);
        newProjectDTO.setId(projectId);
        Department department = findDepartmentById(project.getDepartment().getId());
        Project updatedProject = projectRepository.save(dtoToEntity(newProjectDTO, department));
        department.getProjects().add(updatedProject);
    }

    @Override
    public ProjectDTO getProject(Long projectId) throws ProjectNotFoundException {
        Project project = findProjectById(projectId);
        return modelMapper.map(project, ProjectDTO.class);
    }

    private Project findProjectById(Long projectId) throws ProjectNotFoundException {
        Project projectEntity = projectRepository.findById(projectId).orElseThrow(ProjectNotFoundException::new);
        if (projectEntity.isDeleted()) throw new ProjectNotFoundException();
        return projectEntity;
    }

    private Project dtoToEntity(ProjectDTO projectDTO, Department department) throws DepartmentNotFoundException {
        return Project.builder()
                .department(department)
                .title(projectDTO.getTitle())
                .description(projectDTO.getDescription())
                .status(projectDTO.getStatus())
                .build();
    }

    private Department findDepartmentById(Long departmentId) throws DepartmentNotFoundException {
        Department department = departmentRepository.findById(departmentId).orElseThrow(DepartmentNotFoundException::new);
        if (department.isDeleted()) throw new DepartmentNotFoundException();
        return department;
    }
}