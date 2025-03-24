package com.mgumussoy.advancedtaskmanagement.controllers;

import com.mgumussoy.advancedtaskmanagement.services.AttachmentFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/attachments")
@PreAuthorize("hasAuthority('Team_Member')")
public class AttachmentFileController {
    private final AttachmentFileService attachmentFileService;

    public AttachmentFileController(AttachmentFileService attachmentFileService) {
        this.attachmentFileService = attachmentFileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFiles(
            @RequestParam("taskId") Long taskId,
            @RequestParam("description") String description,
            @RequestPart("files") List<MultipartFile> files) throws IOException {
        attachmentFileService.saveFiles(taskId, description, files);
        return ResponseEntity.ok("Files uploaded successfully!");
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable Long fileId) {
        attachmentFileService.deleteFile(fileId);
        return ResponseEntity.ok("File deleted successfully!");
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long fileId) {
        byte[] fileData = attachmentFileService.getFile(fileId);
        return ResponseEntity.ok(fileData);
    }

    @PostMapping("/{fileId}")
    public ResponseEntity<String> updateFile(
            @PathVariable Long fileId,
            @RequestParam("taskId") Long taskId,
            @RequestParam("description") String description,
            @RequestPart("file") MultipartFile file) throws IOException {
        attachmentFileService.updateFile(fileId, taskId, description, file);
        return ResponseEntity.ok("File updated successfully!");
    }
}