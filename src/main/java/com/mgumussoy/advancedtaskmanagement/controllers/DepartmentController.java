package com.mgumussoy.advancedtaskmanagement.controllers;

import com.mgumussoy.advancedtaskmanagement.dtos.DepartmentDTO;
import com.mgumussoy.advancedtaskmanagement.dtos.ProjectDTO;
import com.mgumussoy.advancedtaskmanagement.dtos.UserDTO;
import com.mgumussoy.advancedtaskmanagement.services.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
@PreAuthorize("hasAnyAuthority('Project_Group_Manager')")
public class DepartmentController {
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping
    public ResponseEntity<DepartmentDTO> createDepartment(@RequestBody @Valid DepartmentDTO departmentDTO) {
        DepartmentDTO createdDepartment = departmentService.createDepartment(departmentDTO);
        return ResponseEntity.ok(createdDepartment);
    }

    @DeleteMapping("/{departmentId}")
    public ResponseEntity<String> deleteDepartment(@PathVariable Long departmentId) {
        departmentService.deleteDepartment(departmentId);
        return ResponseEntity.ok("Department deleted successfully!");
    }

    @PutMapping("/{departmentId}")
    public ResponseEntity<DepartmentDTO> updateDepartment(@PathVariable Long departmentId, @RequestBody @Valid DepartmentDTO departmentDTO) {
        departmentService.updateDepartment(departmentId, departmentDTO);
        return ResponseEntity.ok(departmentDTO);
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<DepartmentDTO> getDepartment(@PathVariable Long departmentId) {
        DepartmentDTO departmentDTO = departmentService.getDepartment(departmentId);
        return ResponseEntity.ok(departmentDTO);
    }

    @GetMapping("/{departmentId}/projects")
    public ResponseEntity<List<ProjectDTO>> getProjects(@PathVariable Long departmentId) {
        List<ProjectDTO> projects = departmentService.getProjects(departmentId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{departmentId}/users")
    public ResponseEntity<List<UserDTO>> getUsers(@PathVariable Long departmentId) {
        List<UserDTO> users = departmentService.getUsers(departmentId);
        return ResponseEntity.ok(users);
    }
}
