package com.mgumussoy.advancedtaskmanagement.controllers;

import com.mgumussoy.advancedtaskmanagement.dtos.ProjectDTO;
import com.mgumussoy.advancedtaskmanagement.dtos.TaskDTO;
import com.mgumussoy.advancedtaskmanagement.services.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PreAuthorize("hasAnyAuthority('Project_Group_Manager', 'Project_Manager', 'Team_Leader')")
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody @Valid ProjectDTO projectDTO) {
        ProjectDTO createdProject = projectService.createProject(projectDTO);
        return ResponseEntity.ok(createdProject);
    }

    @PreAuthorize("hasAnyAuthority('Project_Group_Manager', 'Project_Manager', 'Team_Leader')")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok("Project deleted successfully!");
    }

    @PreAuthorize("hasAnyAuthority('Project_Group_Manager', 'Project_Manager', 'Team_Leader')")
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long projectId, @RequestBody @Valid ProjectDTO projectDTO) {
        projectService.updateProject(projectId, projectDTO);
        return ResponseEntity.ok(projectDTO);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long projectId) {
        ProjectDTO projectDTO = projectService.getProject(projectId);
        return ResponseEntity.ok(projectDTO);
    }

    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<List<TaskDTO>> getTasksOfProject(@PathVariable Long projectId) {
        List<TaskDTO> tasks = projectService.getTasksOfProject(projectId);
        return ResponseEntity.ok(tasks);
    }
}