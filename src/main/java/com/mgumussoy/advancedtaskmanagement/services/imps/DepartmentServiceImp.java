package com.mgumussoy.advancedtaskmanagement.services.imps;

import com.mgumussoy.advancedtaskmanagement.dtos.DepartmentDTO;
import com.mgumussoy.advancedtaskmanagement.dtos.ProjectDTO;
import com.mgumussoy.advancedtaskmanagement.dtos.UserDTO;
import com.mgumussoy.advancedtaskmanagement.entities.Department;
import com.mgumussoy.advancedtaskmanagement.exceptions.DepartmentNotFoundException;
import com.mgumussoy.advancedtaskmanagement.repositories.DepartmentRepository;
import com.mgumussoy.advancedtaskmanagement.services.DepartmentService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImp implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public DepartmentServiceImp(DepartmentRepository departmentRepository, ModelMapper modelMapper) {
        this.departmentRepository = departmentRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ProjectDTO> getProjects(Long departmentId) {
        Department department = departmentRepository.findById(departmentId).orElseThrow(DepartmentNotFoundException::new);
        return department.getProjects().stream().map(project -> modelMapper.map(project, ProjectDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getUsers(Long departmentId) throws DepartmentNotFoundException {
        Department department = departmentRepository.findById(departmentId).orElseThrow(DepartmentNotFoundException::new);
        return department.getUsers().stream().map(user -> modelMapper.map(user, UserDTO.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        Department department = DtoToEntity(departmentDTO);
        return EntityToDto(departmentRepository.save(department));
    }

    @Override
    @Transactional
    public void deleteDepartment(Long departmentId) throws DepartmentNotFoundException {
        Department department = departmentRepository.findById(departmentId).orElseThrow(DepartmentNotFoundException::new);
        department.setDeleted(true);
        departmentRepository.save(department);
    }

    @Override
    @Transactional
    public void updateDepartment(Long departmentId, DepartmentDTO departmentDTO) throws DepartmentNotFoundException {
        findDepartmentById(departmentId);
        departmentDTO.setId(departmentId);
        departmentRepository.save(DtoToEntity(departmentDTO));
    }

    @Override
    public DepartmentDTO getDepartment(Long departmentId) throws DepartmentNotFoundException {
        return EntityToDto(findDepartmentById(departmentId));
    }

    private DepartmentDTO EntityToDto(Department departmentEntity) {
        return modelMapper.map(departmentEntity, DepartmentDTO.class);
    }

    private Department DtoToEntity(DepartmentDTO departmentDTO) {
        return modelMapper.map(departmentDTO, Department.class);
    }

    private Department findDepartmentById(Long departmentId) throws DepartmentNotFoundException {
        Department department = departmentRepository.findById(departmentId).orElseThrow(DepartmentNotFoundException::new);
        if (department.isDeleted()) throw new DepartmentNotFoundException();
        return department;
    }
}
