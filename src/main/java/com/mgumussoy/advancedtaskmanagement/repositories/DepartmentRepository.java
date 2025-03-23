package com.mgumussoy.advancedtaskmanagement.repositories;

import com.mgumussoy.advancedtaskmanagement.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
