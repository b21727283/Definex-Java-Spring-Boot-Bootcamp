package com.mgumussoy.advancedtaskmanagement.services;

import com.mgumussoy.advancedtaskmanagement.dtos.CommentDTO;
import com.mgumussoy.advancedtaskmanagement.exceptions.CommentNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.TaskNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.UserEntityNotFoundException;

public interface CommentService {
    CommentDTO createComment(CommentDTO comment) throws TaskNotFoundException, UserEntityNotFoundException;

    void deleteComment(Long commentId) throws CommentNotFoundException;

    void updateComment(Long commentId, CommentDTO newComment) throws CommentNotFoundException, TaskNotFoundException, UserEntityNotFoundException;

    CommentDTO getComment(Long commentId) throws CommentNotFoundException;
}
