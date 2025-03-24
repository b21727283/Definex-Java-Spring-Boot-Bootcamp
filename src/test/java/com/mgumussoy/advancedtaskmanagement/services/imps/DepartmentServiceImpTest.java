package com.mgumussoy.advancedtaskmanagement.services.imps;

import com.mgumussoy.advancedtaskmanagement.dtos.DepartmentDTO;
import com.mgumussoy.advancedtaskmanagement.dtos.ProjectDTO;
import com.mgumussoy.advancedtaskmanagement.dtos.UserDTO;
import com.mgumussoy.advancedtaskmanagement.entities.Department;
import com.mgumussoy.advancedtaskmanagement.exceptions.DepartmentNotFoundException;
import com.mgumussoy.advancedtaskmanagement.repositories.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DepartmentServiceImpTest {

    @InjectMocks
    private DepartmentServiceImp departmentServiceImp;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ModelMapper modelMapper;

    private Department department;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        department = Department.builder().departmentName("HR").build();
        department.setId(1L);
    }

    @Test
    void getProjects_success() {
        List<ProjectDTO> projectDTOList = new ArrayList<>();
        department.setProjects(new ArrayList<>());
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(modelMapper.map(any(), eq(ProjectDTO.class))).thenReturn(ProjectDTO.builder().title("Proj").build());

        List<ProjectDTO> result = departmentServiceImp.getProjects(1L);
        assertNotNull(result);
    }

    @Test
    void getUsers_success() throws DepartmentNotFoundException {
        department.setUsers(new ArrayList<>());
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(modelMapper.map(any(), eq(UserDTO.class))).thenReturn(UserDTO.builder().username("john").build());

        List<UserDTO> result = departmentServiceImp.getUsers(1L);
        assertNotNull(result);
    }

    @Test
    void createDepartment_success() {
        DepartmentDTO dto = DepartmentDTO.builder().departmentName("HR").build();
        when(modelMapper.map(dto, Department.class)).thenReturn(department);
        when(departmentRepository.save(any(Department.class))).thenReturn(department);
        when(modelMapper.map(department, DepartmentDTO.class)).thenReturn(DepartmentDTO.builder().id(1L).departmentName("HR").build());

        DepartmentDTO result = departmentServiceImp.createDepartment(dto);
        assertEquals(1L, result.getId());
    }

    @Test
    void getDepartment_success() throws DepartmentNotFoundException {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(modelMapper.map(department, DepartmentDTO.class)).thenReturn(DepartmentDTO.builder().id(1L).departmentName("HR").build());
        DepartmentDTO result = departmentServiceImp.getDepartment(1L);
        assertEquals("HR", result.getDepartmentName());
    }

    @Test
    void getDepartment_NotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> departmentServiceImp.getDepartment(1L));
    }

    @Test
    void deleteDepartment_success() throws DepartmentNotFoundException {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        departmentServiceImp.deleteDepartment(1L);
        ArgumentCaptor<Department> captor = ArgumentCaptor.forClass(Department.class);
        verify(departmentRepository).save(captor.capture());
        assertTrue(captor.getValue().isDeleted());
    }

    @Test
    void updateDepartment_success() throws DepartmentNotFoundException {
        DepartmentDTO dto = DepartmentDTO.builder().departmentName("Finance").build();
        Department departmentEntity = Department.builder().departmentName("Finance").build();
        departmentEntity.setId(1L);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.save(any(Department.class))).thenReturn(department);
        when(modelMapper.map(dto, Department.class)).thenReturn(departmentEntity);
        departmentServiceImp.updateDepartment(1L, dto);
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void getProjects_NotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> departmentServiceImp.getProjects(1L));
    }

    @Test
    void getUsers_NotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> departmentServiceImp.getUsers(1L));
    }

    @Test
    void deleteDepartment_NotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> departmentServiceImp.deleteDepartment(1L));
    }

    @Test
    void updateDepartment_NotFound() {
        DepartmentDTO dto = DepartmentDTO.builder().departmentName("Finance").build();
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> departmentServiceImp.updateDepartment(1L, dto));
    }

    @Test
    void getDepartment_DeletedDepartment() {
        department.setDeleted(true);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        assertThrows(DepartmentNotFoundException.class, () -> departmentServiceImp.getDepartment(1L));
    }
}