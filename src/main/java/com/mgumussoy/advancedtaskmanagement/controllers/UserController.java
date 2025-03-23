package com.mgumussoy.advancedtaskmanagement.controllers;

import com.mgumussoy.advancedtaskmanagement.dtos.LoginRequest;
import com.mgumussoy.advancedtaskmanagement.dtos.UserDTO;
import com.mgumussoy.advancedtaskmanagement.entities.UserEntity;
import com.mgumussoy.advancedtaskmanagement.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtEncoder jwtEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService, JwtEncoder jwtEncoder, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtEncoder = jwtEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/token")
    public ResponseEntity<?> token(@RequestBody @Valid LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        Instant now = Instant.now();
        List<String> roles = userEntity.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(userEntity.getUsername())
                .claim("roles", roles)
                .claim("userId", userEntity.getId())
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return ResponseEntity.ok(Map.of("Authorization", token));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.ok(createdUser);
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody @Valid UserDTO userDTO) {
        userService.updateUser(userId, userDTO);
        return ResponseEntity.ok(userDTO);
    }

    @PreAuthorize("hasAuthority('Admin')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully!");
    }

    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
        UserDTO userDTO = userService.getUserById(userId);
        return ResponseEntity.ok(userDTO);
    }

    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers() {
        List<UserDTO> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }
}