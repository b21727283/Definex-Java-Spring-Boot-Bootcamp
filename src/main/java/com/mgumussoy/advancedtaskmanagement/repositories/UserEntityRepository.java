package com.mgumussoy.advancedtaskmanagement.repositories;

import com.mgumussoy.advancedtaskmanagement.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
}
