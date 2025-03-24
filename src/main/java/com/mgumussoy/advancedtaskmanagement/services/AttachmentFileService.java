package com.mgumussoy.advancedtaskmanagement.services;

import com.mgumussoy.advancedtaskmanagement.exceptions.AttachmentFileNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.TaskNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AttachmentFileService {
    void saveFiles(Long taskId, String description, List<MultipartFile> files) throws IOException, TaskNotFoundException;

    void deleteFile(Long fileId) throws AttachmentFileNotFoundException;

    byte[] getFile(Long fileId) throws AttachmentFileNotFoundException;

    void updateFile(Long fileId, Long taskId, String description, MultipartFile file) throws IOException, AttachmentFileNotFoundException, TaskNotFoundException;
}
