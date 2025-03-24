package com.mgumussoy.advancedtaskmanagement.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgumussoy.advancedtaskmanagement.configs.GlobalExceptionHandler;
import com.mgumussoy.advancedtaskmanagement.dtos.LoginRequest;
import com.mgumussoy.advancedtaskmanagement.dtos.UserDTO;
import com.mgumussoy.advancedtaskmanagement.entities.UserEntity;
import com.mgumussoy.advancedtaskmanagement.exceptions.AuthorityNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.DepartmentNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.UserEntityNotFoundException;
import com.mgumussoy.advancedtaskmanagement.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {

    private static final String API_BASE_PATH = "/users";
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ------------------ Token Endpoint Tests ------------------
    @Test
    void token_ShouldReturnOk() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user", "pass");
        UserEntity userEntity = UserEntity.builder()
                .username("user")
                .password("pass")
                .build();
        userEntity.setId(1L);
        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userEntity);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        Jwt jwt = new Jwt("dummyToken",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Collections.singletonMap("alg", "RS256"),
                Collections.singletonMap("sub", "user"));
        when(jwtEncoder.encode(any())).thenReturn(jwt);

        mockMvc.perform(post(API_BASE_PATH + "/token")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Map.of("Authorization", "dummyToken"))));
    }

    // ------------------ Create User Tests ------------------
    @Test
    void createUser_ShouldReturnOk() throws Exception {
        UserDTO requestDto = UserDTO.builder()
                .id(1L)
                .username("john.doe")
                .password("secret")
                .departmentId(100L)
                .build();

        UserDTO responseDto = UserDTO.builder()
                .id(1L)
                .username("john.doe")
                .password("secret")
                .departmentId(100L)
                .build();

        when(userService.createUser(any(UserDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post(API_BASE_PATH)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    void createUser_ShouldThrowAuthorityNotFoundException() throws Exception {
        UserDTO requestDto = UserDTO.builder()
                .id(1L)
                .username("john.doe")
                .password("secret")
                .departmentId(100L)
                .build();

        when(userService.createUser(any(UserDTO.class))).thenThrow(new AuthorityNotFoundException());

        mockMvc.perform(post(API_BASE_PATH)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_ShouldThrowDepartmentNotFoundException() throws Exception {
        UserDTO requestDto = UserDTO.builder()
                .id(1L)
                .username("john.doe")
                .password("secret")
                .departmentId(100L)
                .build();

        when(userService.createUser(any(UserDTO.class))).thenThrow(new DepartmentNotFoundException());

        mockMvc.perform(post(API_BASE_PATH)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ------------------ Update User Tests ------------------
    @Test
    @WithMockUser(authorities = {"Admin"})
    void updateUser_ShouldReturnOk() throws Exception {
        Long userId = 1L;
        UserDTO requestDto = UserDTO.builder()
                .id(userId)
                .username("john.updated")
                .password("secret")
                .departmentId(100L)
                .build();

        mockMvc.perform(put(API_BASE_PATH + "/" + userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)));
    }

    @Test
    @WithMockUser(authorities = {"Admin"})
    void updateUser_ShouldThrowUserEntityNotFoundException() throws Exception {
        Long userId = 1L;
        UserDTO requestDto = UserDTO.builder()
                .id(userId)
                .username("john.updated")
                .password("secret")
                .departmentId(100L)
                .build();

        doThrow(new UserEntityNotFoundException())
                .when(userService).updateUser(anyLong(), any(UserDTO.class));

        mockMvc.perform(put(API_BASE_PATH + "/" + userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = {"Admin"})
    void updateUser_ShouldThrowDepartmentNotFoundException() throws Exception {
        Long userId = 1L;
        UserDTO requestDto = UserDTO.builder()
                .id(userId)
                .username("john.updated")
                .password("secret")
                .departmentId(100L)
                .build();

        doThrow(new DepartmentNotFoundException())
                .when(userService).updateUser(anyLong(), any(UserDTO.class));

        mockMvc.perform(put(API_BASE_PATH + "/" + userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = {"Admin"})
    void updateUser_ShouldThrowAuthorityNotFoundException() throws Exception {
        Long userId = 1L;
        UserDTO requestDto = UserDTO.builder()
                .id(userId)
                .username("john.updated")
                .password("secret")
                .departmentId(100L)
                .build();

        doThrow(new AuthorityNotFoundException())
                .when(userService).updateUser(anyLong(), any(UserDTO.class));

        mockMvc.perform(put(API_BASE_PATH + "/" + userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ------------------ Delete User Tests ------------------
    @Test
    @WithMockUser(authorities = {"Admin"})
    void deleteUser_ShouldReturnOk() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete(API_BASE_PATH + "/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully!"));
    }

    @Test
    @WithMockUser(authorities = {"Admin"})
    void deleteUser_ShouldThrowUserEntityNotFoundException() throws Exception {
        Long userId = 1L;

        doThrow(new UserEntityNotFoundException())
                .when(userService).deleteUser(anyLong());

        mockMvc.perform(delete(API_BASE_PATH + "/" + userId))
                .andExpect(status().isNotFound());
    }

    // ------------------ Get User Tests ------------------
    @Test
    @WithMockUser(authorities = {"Admin"})
    void getUser_ShouldReturnOk() throws Exception {
        Long userId = 1L;
        UserDTO responseDto = UserDTO.builder()
                .id(userId)
                .username("john.doe")
                .password("secret")
                .departmentId(100L)
                .build();

        when(userService.getUserById(anyLong())).thenReturn(responseDto);

        mockMvc.perform(get(API_BASE_PATH + "/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    @WithMockUser(authorities = {"Admin"})
    void getUser_ShouldThrowUserEntityNotFoundException() throws Exception {
        Long userId = 1L;

        doThrow(new UserEntityNotFoundException())
                .when(userService).getUserById(anyLong());

        mockMvc.perform(get(API_BASE_PATH + "/" + userId))
                .andExpect(status().isNotFound());
    }

    // ------------------ Get Users Tests ------------------
    @Test
    @WithMockUser(authorities = {"Admin"})
    void getUsers_ShouldReturnOk() throws Exception {
        List<UserDTO> users = List.of(
                UserDTO.builder().id(1L).username("john.doe").password("secret").departmentId(100L).build(),
                UserDTO.builder().id(2L).username("jane.doe").password("secret").departmentId(101L).build()
        );

        when(userService.getUsers()).thenReturn(users);

        mockMvc.perform(get(API_BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }
}
