package com.mgumussoy.advancedtaskmanagement.services;

import com.mgumussoy.advancedtaskmanagement.dtos.UserDTO;
import com.mgumussoy.advancedtaskmanagement.exceptions.AuthorityNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.DepartmentNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.UserEntityNotFoundException;

import java.util.List;

public interface UserService {
    UserDTO getUserById(Long userId) throws UserEntityNotFoundException;

    List<UserDTO> getUsers();

    UserDTO createUser(UserDTO user) throws AuthorityNotFoundException, DepartmentNotFoundException;

    void deleteUser(Long userId) throws UserEntityNotFoundException;

    void updateUser(Long userId, UserDTO newUser) throws UserEntityNotFoundException, DepartmentNotFoundException, AuthorityNotFoundException;
}
