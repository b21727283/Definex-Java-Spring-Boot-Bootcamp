package com.mgumussoy.advancedtaskmanagement.services;

import com.mgumussoy.advancedtaskmanagement.dtos.DepartmentDTO;
import com.mgumussoy.advancedtaskmanagement.dtos.ProjectDTO;
import com.mgumussoy.advancedtaskmanagement.dtos.UserDTO;
import com.mgumussoy.advancedtaskmanagement.exceptions.DepartmentNotFoundException;

import java.util.List;

public interface DepartmentService {
    List<ProjectDTO> getProjects(Long departmentId);

    List<UserDTO> getUsers(Long departmentId) throws DepartmentNotFoundException;

    DepartmentDTO createDepartment(DepartmentDTO departmentDTO);

    void deleteDepartment(Long departmentId);

    void updateDepartment(Long departmentId, DepartmentDTO departmentDTO);

    DepartmentDTO getDepartment(Long departmentId);
}
