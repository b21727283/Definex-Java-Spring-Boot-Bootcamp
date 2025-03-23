package com.mgumussoy.advancedtaskmanagement.services.imps;

import com.mgumussoy.advancedtaskmanagement.entities.AttachmentFile;
import com.mgumussoy.advancedtaskmanagement.entities.TaskEntity;
import com.mgumussoy.advancedtaskmanagement.exceptions.AttachmentFileNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.TaskNotFoundException;
import com.mgumussoy.advancedtaskmanagement.repositories.AttachmentFileRepository;
import com.mgumussoy.advancedtaskmanagement.repositories.TaskRepository;
import com.mgumussoy.advancedtaskmanagement.services.AttachmentFileService;
import com.mgumussoy.advancedtaskmanagement.services.TaskService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class AttachmentFileServiceImp implements AttachmentFileService {
    private final AttachmentFileRepository attachmentFileRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public AttachmentFileServiceImp(AttachmentFileRepository attachmentFileRepository, TaskService taskService, TaskRepository taskRepository) {
        this.attachmentFileRepository = attachmentFileRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional
    public void saveFiles(Long taskId, String description, List<MultipartFile> files) throws IOException, TaskNotFoundException {
        TaskEntity task = findTaskById(taskId);

        for (MultipartFile file : files) {
            AttachmentFile fileEntity = new AttachmentFile();
            fileEntity.setTaskEntity(task);
            fileEntity.setDescription(description);
            fileEntity.setFileName(file.getOriginalFilename());
            fileEntity.setFileType(file.getContentType());
            fileEntity.setFileData(file.getBytes());

            task.getAttachments().add(fileEntity);
        }

        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void deleteFile(Long fileId) throws AttachmentFileNotFoundException {
        AttachmentFile attachmentFile = findAttachmentFileById(fileId);
        TaskEntity taskEntity = attachmentFile.getTaskEntity();

        if (taskEntity != null) {
            taskEntity.getAttachments().remove(attachmentFile);
        }

        attachmentFile.setDeleted(true);
        attachmentFileRepository.save(attachmentFile);
    }

    @Override
    public byte[] getFile(Long fileId) throws AttachmentFileNotFoundException {
        AttachmentFile attachmentFile = findAttachmentFileById(fileId);
        return attachmentFile.getFileData();
    }

    @Override
    @Transactional
    public void updateFile(Long fileId, Long taskId, String description, MultipartFile file) throws IOException, AttachmentFileNotFoundException, TaskNotFoundException {
        AttachmentFile fileEntity = findAttachmentFileById(fileId);
        TaskEntity oldTaskEntity = fileEntity.getTaskEntity();

        oldTaskEntity.getAttachments().remove(fileEntity);

        TaskEntity newTaskEntity = findTaskById(taskId);

        fileEntity.setDescription(description);
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setFileType(file.getContentType());
        fileEntity.setFileData(file.getBytes());

        newTaskEntity.getAttachments().add(fileEntity);
    }

    private AttachmentFile findAttachmentFileById(Long fileId) throws AttachmentFileNotFoundException {
        AttachmentFile attachmentFile = attachmentFileRepository.findById(fileId).orElseThrow(AttachmentFileNotFoundException::new);
        if (attachmentFile.isDeleted()) throw new AttachmentFileNotFoundException();
        return attachmentFile;
    }

    private TaskEntity findTaskById(Long taskId) throws TaskNotFoundException {
        TaskEntity task = taskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        if (task.isDeleted()) throw new TaskNotFoundException();
        return task;
    }
}
