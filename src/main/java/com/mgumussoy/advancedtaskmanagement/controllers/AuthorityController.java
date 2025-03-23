package com.mgumussoy.advancedtaskmanagement.controllers;

import com.mgumussoy.advancedtaskmanagement.dtos.AuthorityDTO;
import com.mgumussoy.advancedtaskmanagement.services.AuthorityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authorities")
@PreAuthorize("hasAuthority('Admin')")
public class AuthorityController {
    private final AuthorityService authorityService;

    public AuthorityController(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    @PostMapping
    public ResponseEntity<AuthorityDTO> createAuthority(@RequestBody @Valid AuthorityDTO authorityDTO) {
        AuthorityDTO createdAuthority = authorityService.createAuthority(authorityDTO);
        return ResponseEntity.ok(createdAuthority);
    }

    @DeleteMapping("/{authorityId}")
    public ResponseEntity<String> deleteAuthority(@PathVariable Long authorityId) {
        authorityService.deleteAuthority(authorityId);
        return ResponseEntity.ok("Authority deleted successfully!");
    }

    @PutMapping("/{authorityId}")
    public ResponseEntity<AuthorityDTO> updateAuthority(@PathVariable Long authorityId, @RequestBody @Valid AuthorityDTO authorityDTO) {
        authorityService.updateAuthority(authorityId, authorityDTO);
        return ResponseEntity.ok(authorityDTO);
    }

    @GetMapping("/{authorityId}")
    public ResponseEntity<AuthorityDTO> getAuthority(@PathVariable Long authorityId) {
        AuthorityDTO authorityDTO = authorityService.getAuthority(authorityId);
        return ResponseEntity.ok(authorityDTO);
    }
}