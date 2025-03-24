package com.mgumussoy.advancedtaskmanagement.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgumussoy.advancedtaskmanagement.configs.GlobalExceptionHandler;
import com.mgumussoy.advancedtaskmanagement.dtos.AuthorityDTO;
import com.mgumussoy.advancedtaskmanagement.exceptions.AuthorityNotFoundException;
import com.mgumussoy.advancedtaskmanagement.services.AuthorityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthorityControllerTest {

    private static final String API_BASE_PATH = "/authorities";
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthorityController authorityController;

    @Mock
    private AuthorityService authorityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authorityController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ------------------ Create Authority Tests ------------------
    @Test
    void createAuthority_ShouldReturnOk() throws Exception {
        AuthorityDTO requestDto = new AuthorityDTO();
        requestDto.setAuthority("Team_Member");

        AuthorityDTO responseDto = new AuthorityDTO();
        requestDto.setAuthority("Team_Member");

        when(authorityService.createAuthority(any(AuthorityDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post(API_BASE_PATH)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    // ------------------ Update Authority Tests ------------------
    @Test
    void updateAuthority_ShouldReturnOk() throws Exception {
        Long authorityId = 1L;
        AuthorityDTO requestDto = new AuthorityDTO();
        requestDto.setAuthority("Team_Member");

        mockMvc.perform(put(API_BASE_PATH + "/" + authorityId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)));
    }

    @Test
    void updateAuthority_ThrowsAuthorityNotFoundException() throws Exception {
        long authorityId = 1L;
        AuthorityDTO requestDto = new AuthorityDTO();
        requestDto.setAuthority("Team_Member");

        doThrow(new AuthorityNotFoundException())
                .when(authorityService).updateAuthority(anyLong(), any(AuthorityDTO.class));

        mockMvc.perform(put(API_BASE_PATH + "/" + authorityId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ------------------ Get Authority Tests ------------------
    @Test
    void getAuthority_ShouldReturnOk() throws Exception {
        long authorityId = 1L;
        AuthorityDTO responseDto = new AuthorityDTO();
        responseDto.setAuthority("Team_Member");

        when(authorityService.getAuthority(anyLong())).thenReturn(responseDto);

        mockMvc.perform(get(API_BASE_PATH + "/" + authorityId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    void getAuthority_ThrowsAuthorityNotFoundException() throws Exception {
        long authorityId = 1L;

        doThrow(new AuthorityNotFoundException())
                .when(authorityService).getAuthority(anyLong());

        mockMvc.perform(get(API_BASE_PATH + "/" + authorityId))
                .andExpect(status().isNotFound());
    }

    // ------------------ Delete Authority Tests ------------------
    @Test
    void deleteAuthority_ShouldReturnOk() throws Exception {
        long authorityId = 1L;

        mockMvc.perform(delete(API_BASE_PATH + "/" + authorityId))
                .andExpect(status().isOk())
                .andExpect(content().string("Authority deleted successfully!"));
    }

    @Test
    void deleteAuthority_ThrowsAuthorityNotFoundException() throws Exception {
        long authorityId = 1L;

        doThrow(new AuthorityNotFoundException())
                .when(authorityService).deleteAuthority(anyLong());

        mockMvc.perform(delete(API_BASE_PATH + "/" + authorityId))
                .andExpect(status().isNotFound());
    }
}