package com.mgumussoy.advancedtaskmanagement.services.imps;

import com.mgumussoy.advancedtaskmanagement.dtos.CommentDTO;
import com.mgumussoy.advancedtaskmanagement.entities.Comment;
import com.mgumussoy.advancedtaskmanagement.entities.TaskEntity;
import com.mgumussoy.advancedtaskmanagement.entities.UserEntity;
import com.mgumussoy.advancedtaskmanagement.exceptions.CommentNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.TaskNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.UserEntityNotFoundException;
import com.mgumussoy.advancedtaskmanagement.repositories.CommentRepository;
import com.mgumussoy.advancedtaskmanagement.repositories.TaskRepository;
import com.mgumussoy.advancedtaskmanagement.repositories.UserEntityRepository;
import com.mgumussoy.advancedtaskmanagement.services.CommentService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImp implements CommentService {
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;
    private final TaskRepository taskRepository;
    private final UserEntityRepository userEntityRepository;

    @Autowired
    public CommentServiceImp(CommentRepository commentRepository, ModelMapper modelMapper, TaskRepository taskRepository, UserEntityRepository userEntityRepository) {
        this.commentRepository = commentRepository;
        this.modelMapper = modelMapper;
        this.taskRepository = taskRepository;
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    @Transactional
    public CommentDTO createComment(CommentDTO comment) throws TaskNotFoundException, UserEntityNotFoundException {
        Comment commentEntity = new Comment();
        commentEntity.setText(comment.getText());

        TaskEntity task = findTaskById(comment.getTaskId());
        commentEntity.setTaskEntity(task);

        UserEntity author = findUserById(comment.getAuthorId());
        commentEntity.setAuthor(author);

        commentEntity = commentRepository.save(commentEntity);

        task.getComments().add(commentEntity);
        author.getComments().add(commentEntity);

        return convertEntityToDTO(commentEntity);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) throws CommentNotFoundException {
        Comment comment = findCommentById(commentId);
        comment.setDeleted(true);
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void updateComment(Long commentId, CommentDTO newComment) throws CommentNotFoundException, TaskNotFoundException, UserEntityNotFoundException {
        Comment existingComment = findCommentById(commentId);

        TaskEntity oldTask = existingComment.getTaskEntity();
        UserEntity oldAuthor = existingComment.getAuthor();

        TaskEntity newTask = findTaskById(newComment.getTaskId());
        UserEntity newAuthor = findUserById(newComment.getAuthorId());

        existingComment.setText(newComment.getText());
        existingComment.setTaskEntity(newTask);
        existingComment.setAuthor(newAuthor);

        commentRepository.save(existingComment);

        oldTask.getComments().remove(existingComment);
        oldAuthor.getComments().remove(existingComment);

        newTask.getComments().add(existingComment);
        newAuthor.getComments().add(existingComment);
    }

    @Override
    public CommentDTO getComment(Long commentId) throws CommentNotFoundException {
        return convertEntityToDTO(findCommentById(commentId));
    }

    private Comment findCommentById(Long commentId) throws CommentNotFoundException {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        if (comment.isDeleted()) throw new CommentNotFoundException();
        return comment;
    }

    private CommentDTO convertEntityToDTO(Comment comment) {
        CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);
        commentDTO.setTaskId(comment.getTaskEntity().getId());
        commentDTO.setAuthorId(comment.getAuthor().getId());
        return commentDTO;
    }

    private TaskEntity findTaskById(Long taskId) throws TaskNotFoundException {
        TaskEntity task = taskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        if (task.isDeleted()) throw new TaskNotFoundException();
        return task;
    }

    private UserEntity findUserById(Long userId) throws UserEntityNotFoundException {
        UserEntity user = userEntityRepository.findById(userId).orElseThrow(UserEntityNotFoundException::new);
        if (user.isDeleted()) throw new UserEntityNotFoundException();
        return user;
    }
}
