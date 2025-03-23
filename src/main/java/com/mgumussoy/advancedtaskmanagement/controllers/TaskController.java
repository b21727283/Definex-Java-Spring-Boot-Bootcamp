package com.mgumussoy.advancedtaskmanagement.controllers;

import com.mgumussoy.advancedtaskmanagement.dtos.TaskDTO;
import com.mgumussoy.advancedtaskmanagement.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PreAuthorize("hasAuthority('Team_Member')")
    @PostMapping("/create")
    public ResponseEntity<TaskDTO> createTask(@RequestBody @Valid TaskDTO taskDTO) {
        TaskDTO createdTask = taskService.saveTask(taskDTO);
        return ResponseEntity.ok(createdTask);
    }

    @PreAuthorize("hasAuthority('Team_Member')")
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long taskId, @RequestBody @Valid TaskDTO taskDTO) {
        TaskDTO updatedTask = taskService.updateTask(taskId, taskDTO);
        return ResponseEntity.ok(updatedTask);
    }

    @PreAuthorize("hasAuthority('Team_Member')")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok("Task deleted successfully!");
    }

    @PreAuthorize("hasAuthority('Team_Member')")
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Long taskId) {
        TaskDTO taskDTO = taskService.getTask(taskId);
        return ResponseEntity.ok(taskDTO);
    }

    @PreAuthorize("hasAuthority('Team_Member')")
    @PostMapping("/{taskId}/assign/{userId}")
    public ResponseEntity<String> assignTask(@PathVariable Long taskId, @PathVariable Long userId) {
        taskService.assignTask(taskId, userId);
        return ResponseEntity.ok("Task assigned successfully!");
    }
}