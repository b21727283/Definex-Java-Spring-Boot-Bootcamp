package com.mgumussoy.advancedtaskmanagement.repositories;

import com.mgumussoy.advancedtaskmanagement.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
