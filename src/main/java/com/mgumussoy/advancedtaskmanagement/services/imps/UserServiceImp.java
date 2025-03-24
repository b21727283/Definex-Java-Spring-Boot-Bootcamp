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
import com.mgumussoy.advancedtaskmanagement.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService {
    private final UserEntityRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;
    private final DepartmentRepository departmentRepository;

//    private final static String ADMIN_AUTHORITY = "Admin";
//    private final static String TEAM_MEMBER = "Team Member";

    @Autowired
    public UserServiceImp(UserEntityRepository repository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository, DepartmentRepository departmentRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public UserDTO getUserById(Long userId) throws UserEntityNotFoundException {
        return entityToDto(getUserEntity(userId));
    }

    @Override
    public List<UserDTO> getUsers() {
        return repository.findAll().stream()
                .filter(userEntity -> !userEntity.isDeleted())
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) throws AuthorityNotFoundException, DepartmentNotFoundException {
        Department department = findDepartmentById(userDTO.getDepartmentId());

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        UserEntity userEntity = dtoToEntity(userDTO, department);

        userEntity = repository.save(userEntity);

        department.getUsers().add(userEntity);
        return entityToDto(userEntity);
    }

    @Override
    @Transactional
    public void updateUser(Long userId, UserDTO newUser) throws UserEntityNotFoundException, DepartmentNotFoundException, AuthorityNotFoundException {
        UserEntity oldUserEntity = getUserEntity(userId);

        Department oldDepartment = oldUserEntity.getDepartment();
        Department newDepartment = findDepartmentById(newUser.getDepartmentId());

        UserEntity newUserEntity = dtoToEntity(newUser, newDepartment);
        newUserEntity.setId(userId);
        newUserEntity.setPassword(passwordEncoder.encode(newUser.getPassword()));

        oldDepartment.getUsers().remove(oldUserEntity);

        newUserEntity = repository.save(newUserEntity);
        newDepartment.getUsers().add(newUserEntity);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) throws UserEntityNotFoundException {
        UserEntity userEntity = getUserEntity(userId);

        Department department = userEntity.getDepartment();
        department.getUsers().remove(userEntity);

        userEntity.setDeleted(true);
        repository.save(userEntity);
    }

    private UserDTO entityToDto(UserEntity userEntity) {
        UserDTO userDTO = UserDTO.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .departmentId(userEntity.getDepartment().getId())
                .build();

        userDTO.setAuthorities(userEntity.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

//        if (userEntity.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals(ADMIN_AUTHORITY))) {
//            userDTO.setAuthorities(userEntity.getAuthorities().stream()
//                    .map(GrantedAuthority::getAuthority)
//                    .collect(Collectors.toList()));
//        } else {
//            userDTO.setAuthorities(new ArrayList<>(List.of(TEAM_MEMBER)));
//        }

        return userDTO;
    }

    private UserEntity dtoToEntity(UserDTO userDTO, Department department) {

        UserEntity userEntity = UserEntity.builder()
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .department(department)
                .build();

        userEntity.setAuthorities(userDTO.getAuthorities().stream()
                .map(this::getAuthorityByName)
                .collect(Collectors.toList()));

//        if (userEntity.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals(ADMIN_AUTHORITY))) {
//            userEntity.setAuthorities(userDTO.getAuthorities().stream()
//                    .map(this::getAuthorityByName)
//                    .collect(Collectors.toList()));
//        } else {
//            userDTO.setAuthorities(new ArrayList<>(List.of(TEAM_MEMBER)));
//        }

        return userEntity;
    }

    private Authority getAuthorityByName(String authority) throws AuthorityNotFoundException {
        Authority authorityEntity = authorityRepository.findByAuthority(authority).orElseThrow(AuthorityNotFoundException::new);
        if (authorityEntity.isDeleted()) throw new AuthorityNotFoundException();
        return authorityEntity;
    }

    private Department findDepartmentById(Long departmentId) throws DepartmentNotFoundException {
        Department department = departmentRepository.findById(departmentId).orElseThrow(DepartmentNotFoundException::new);
        if (department.isDeleted()) throw new DepartmentNotFoundException();
        return department;
    }

    private UserEntity getUserEntity(Long userId) throws UserEntityNotFoundException {
        UserEntity userEntity = repository.findById(userId).orElseThrow(UserEntityNotFoundException::new);
        if (userEntity.isDeleted()) throw new UserEntityNotFoundException();
        return userEntity;
    }
}
