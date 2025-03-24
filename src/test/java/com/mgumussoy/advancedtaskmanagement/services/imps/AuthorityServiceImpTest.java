package com.mgumussoy.advancedtaskmanagement.services.imps;

import com.mgumussoy.advancedtaskmanagement.dtos.AuthorityDTO;
import com.mgumussoy.advancedtaskmanagement.entities.Authority;
import com.mgumussoy.advancedtaskmanagement.exceptions.AuthorityNotFoundException;
import com.mgumussoy.advancedtaskmanagement.repositories.AuthorityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthorityServiceImpTest {

    @InjectMocks
    private AuthorityServiceImp authorityServiceImp;

    @Mock
    private AuthorityRepository authorityRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAuthority_success() {
        AuthorityDTO dto = AuthorityDTO.builder().authority("ADMIN").build();
        Authority savedAuthority = Authority.builder().id(1L).authority("ADMIN").build();
        when(authorityRepository.save(any(Authority.class))).thenReturn(savedAuthority);

        AuthorityDTO result = authorityServiceImp.createAuthority(dto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ADMIN", result.getAuthority());
        verify(authorityRepository, times(1)).save(any(Authority.class));
    }

    @Test
    void getAuthority_success() {
        Authority authority = Authority.builder().id(1L).authority("ADMIN").build();
        when(authorityRepository.findById(1L)).thenReturn(Optional.of(authority));

        AuthorityDTO result = authorityServiceImp.getAuthority(1L);
        assertEquals(1L, result.getId());
        assertEquals("ADMIN", result.getAuthority());
    }

    @Test
    void getAuthority_NotFound() {
        when(authorityRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(AuthorityNotFoundException.class, () -> authorityServiceImp.getAuthority(1L));
    }

    @Test
    void deleteAuthority_success() {
        Authority authority = Authority.builder().id(1L).authority("ADMIN").build();
        when(authorityRepository.findById(1L)).thenReturn(Optional.of(authority));

        authorityServiceImp.deleteAuthority(1L);
        ArgumentCaptor<Authority> captor = ArgumentCaptor.forClass(Authority.class);
        verify(authorityRepository).save(captor.capture());
        assertTrue(captor.getValue().isDeleted());
    }

    @Test
    void deleteAuthority_ThrowNotFoundForDeleted() {
        Authority authority = Authority.builder().id(1L).authority("ADMIN").build();
        authority.setDeleted(true);
        when(authorityRepository.findById(1L)).thenReturn(Optional.of(authority));
        assertThrows(AuthorityNotFoundException.class, () -> authorityServiceImp.deleteAuthority(1L));
    }

    @Test
    void updateAuthority_success() {
        Authority authority = Authority.builder().id(1L).authority("ADMIN").build();
        AuthorityDTO updateDTO = AuthorityDTO.builder().authority("USER").build();
        when(authorityRepository.findById(1L)).thenReturn(Optional.of(authority));
        when(authorityRepository.save(any(Authority.class))).thenReturn(authority);

        authorityServiceImp.updateAuthority(1L, updateDTO);
        ArgumentCaptor<Authority> captor = ArgumentCaptor.forClass(Authority.class);
        verify(authorityRepository).save(captor.capture());
        assertEquals("USER", captor.getValue().getAuthority());
    }
}