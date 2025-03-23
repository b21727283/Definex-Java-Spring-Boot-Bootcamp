package com.mgumussoy.advancedtaskmanagement.repositories;

import com.mgumussoy.advancedtaskmanagement.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
}
