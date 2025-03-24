package com.mgumussoy.advancedtaskmanagement.services.imps;

import com.mgumussoy.advancedtaskmanagement.dtos.UserDTO;
import com.mgumussoy.advancedtaskmanagement.entities.Authority;
import com.mgumussoy.advancedtaskmanagement.entities.Department;
import com.mgumussoy.advancedtaskmanagement.entities.UserEntity;
import com.mgumussoy.advancedtaskmanagement.exceptions.AuthorityNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.DepartmentNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.UserEntityNotFoundException;
import com.mgumussoy.advancedtaskmanagement.repositories.AuthorityRepository;
import com.mgumussoy.advancedtaskmanagement.repositories.DepartmentRepository;
import com.mgumussoy.advancedtaskmanagement.repositories.UserEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImpTest {

    @InjectMocks
    private UserServiceImp userServiceImp;

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    private Department department;
    private Authority authority;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        department = new Department();
        department.setId(1L);
        authority = Authority.builder().id(1L).authority("Admin").build();
        user = UserEntity.builder().username("john").password("pass").department(department).build();
        user.setId(1L);
        user.setAuthorities(List.of(authority));
    }

    @Test
    void getUserById_success() throws UserEntityNotFoundException {
        when(userEntityRepository.findById(1L)).thenReturn(Optional.of(user));
        UserDTO dto = userServiceImp.getUserById(1L);
        assertEquals("john", dto.getUsername());
    }

    @Test
    void getUserById_NotFound() {
        when(userEntityRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserEntityNotFoundException.class, () -> userServiceImp.getUserById(1L));
    }

    @Test
    void getUsers_success() {
        user.setDeleted(false);
        when(userEntityRepository.findAll()).thenReturn(List.of(user));
        List<UserDTO> users = userServiceImp.getUsers();
        assertEquals(1, users.size());
    }

    @Test
    void createUser_success() throws AuthorityNotFoundException, DepartmentNotFoundException {
        UserDTO dto = UserDTO.builder().id(1L).username("john").password("newpass").departmentId(1L).authorities(List.of("Admin")).build();
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(passwordEncoder.encode("newpass")).thenReturn("encoded");
        when(authorityRepository.findByAuthority("Admin")).thenReturn(Optional.of(authority));
        when(userEntityRepository.save(any(UserEntity.class))).thenReturn(user);
        UserDTO result = userServiceImp.createUser(dto);
        assertEquals("john", result.getUsername());
    }

    @Test
    void createUser_DepartmentNotFound() {
        UserDTO dto = UserDTO.builder().id(1L).username("john").password("pass").departmentId(1L).build();
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> userServiceImp.createUser(dto));
    }

    @Test
    void updateUser_success() throws UserEntityNotFoundException, DepartmentNotFoundException, AuthorityNotFoundException {
        UserDTO dto = UserDTO.builder().id(1L).username("johnUpdated").password("pass").departmentId(1L).build();
        when(userEntityRepository.findById(1L)).thenReturn(Optional.of(user));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userEntityRepository.save(any(UserEntity.class))).thenReturn(user);
        userServiceImp.updateUser(1L, dto);
        verify(userEntityRepository).save(any(UserEntity.class));
    }

    @Test
    void deleteUser_success() throws UserEntityNotFoundException {
        when(userEntityRepository.findById(1L)).thenReturn(Optional.of(user));
        userServiceImp.deleteUser(1L);
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userEntityRepository).save(captor.capture());
        assertTrue(captor.getValue().isDeleted());
    }

    @Test
    void createUser_AuthorityNotFound() {
        UserDTO dto = UserDTO.builder()
                .id(2L)
                .username("alice")
                .password("alicepass")
                .departmentId(1L)
                .authorities(List.of("User"))
                .build();
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(passwordEncoder.encode("alicepass")).thenReturn("encodedAlice");
        when(authorityRepository.findByAuthority("User")).thenReturn(Optional.empty());
        assertThrows(AuthorityNotFoundException.class, () -> userServiceImp.createUser(dto));
    }

    @Test
    void createUser_DepartmentDeleted() {
        department.setDeleted(true);
        UserDTO dto = UserDTO.builder()
                .id(3L)
                .username("bob")
                .password("bobpass")
                .departmentId(1L)
                .authorities(List.of("Admin"))
                .build();
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        assertThrows(DepartmentNotFoundException.class, () -> userServiceImp.createUser(dto));
    }

    @Test
    void updateUser_UserNotFound() {
        UserDTO dto = UserDTO.builder()
                .id(1L)
                .username("johnUpdated")
                .password("newpass")
                .departmentId(1L)
                .authorities(List.of("Admin"))
                .build();
        when(userEntityRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserEntityNotFoundException.class, () -> userServiceImp.updateUser(1L, dto));
    }

    @Test
    void updateUser_DepartmentNotFound() {
        UserDTO dto = UserDTO.builder()
                .id(1L)
                .username("johnUpdated")
                .password("newpass")
                .departmentId(2L)
                .authorities(List.of("Admin"))
                .build();
        when(userEntityRepository.findById(1L)).thenReturn(Optional.of(user));
        when(departmentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(DepartmentNotFoundException.class, () -> userServiceImp.updateUser(1L, dto));
    }

    @Test
    void updateUser_AuthorityNotFound() {
        UserDTO dto = UserDTO.builder()
                .id(1L)
                .username("johnUpdated")
                .password("newpass")
                .departmentId(1L)
                .authorities(List.of("User"))
                .build();
        when(userEntityRepository.findById(1L)).thenReturn(Optional.of(user));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(passwordEncoder.encode("newpass")).thenReturn("encodedNewPass");
        when(authorityRepository.findByAuthority("User")).thenReturn(Optional.empty());
        assertThrows(AuthorityNotFoundException.class, () -> userServiceImp.updateUser(1L, dto));
    }

    @Test
    void deleteUser_NotFound() {
        when(userEntityRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserEntityNotFoundException.class, () -> userServiceImp.deleteUser(1L));
    }
}