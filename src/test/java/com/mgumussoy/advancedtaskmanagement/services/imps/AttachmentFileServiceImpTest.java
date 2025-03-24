package com.mgumussoy.advancedtaskmanagement.services.imps;

import com.mgumussoy.advancedtaskmanagement.entities.AttachmentFile;
import com.mgumussoy.advancedtaskmanagement.entities.TaskEntity;
import com.mgumussoy.advancedtaskmanagement.exceptions.AttachmentFileNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.TaskNotFoundException;
import com.mgumussoy.advancedtaskmanagement.repositories.AttachmentFileRepository;
import com.mgumussoy.advancedtaskmanagement.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AttachmentFileServiceImpTest {

    @InjectMocks
    private AttachmentFileServiceImp attachmentFileServiceImp;

    @Mock
    private AttachmentFileRepository attachmentFileRepository;

    @Mock
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private TaskEntity createValidTask(Long taskId) {
        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setDeleted(false);
        if (task.getAttachments() == null) {
            task.setAttachments(new ArrayList<>());
        }
        return task;
    }

    private AttachmentFile createValidAttachmentFile(Long fileId, TaskEntity task) {
        AttachmentFile file = new AttachmentFile();
        file.setId(fileId);
        file.setFileName("test.txt");
        file.setFileType("text/plain");
        file.setFileData("dummy content".getBytes());
        file.setDeleted(false);
        file.setTaskEntity(task);
        return file;
    }

    // ------------------ saveFiles Tests ------------------
    @Test
    void saveFiles_successful() throws Exception {
        Long taskId = 1L;
        String description = "File description";
        MockMultipartFile multipartFile = new MockMultipartFile("files", "test.txt", "text/plain", "file content".getBytes());
        List<MultipartFile> files = List.of(multipartFile);

        TaskEntity task = createValidTask(taskId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        attachmentFileServiceImp.saveFiles(taskId, description, files);

        assertEquals(1, task.getAttachments().size());
        AttachmentFile savedFile = task.getAttachments().getFirst();
        assertEquals("test.txt", savedFile.getFileName());
        assertEquals("text/plain", savedFile.getFileType());
        assertEquals("file content", new String(savedFile.getFileData()));
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void saveFiles_TaskNotFound() {
        Long taskId = 1L;
        String description = "File description";
        List<MultipartFile> files = new ArrayList<>();
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> {
            attachmentFileServiceImp.saveFiles(taskId, description, files);
        });
    }

    @Test
    void saveFiles_IOException() throws Exception {
        Long taskId = 1L;
        String description = "File description";
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("error.txt");
        when(file.getContentType()).thenReturn("text/plain");
        when(file.getBytes()).thenThrow(new IOException("IO error"));
        List<MultipartFile> files = List.of(file);

        TaskEntity task = createValidTask(taskId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThrows(IOException.class, () -> {
            attachmentFileServiceImp.saveFiles(taskId, description, files);
        });

        verify(taskRepository, never()).save(any());
    }

    // ------------------ deleteFile Tests ------------------
    @Test
    void deleteFile_successful() throws Exception {
        Long fileId = 1L;
        TaskEntity task = createValidTask(10L);
        AttachmentFile file = createValidAttachmentFile(fileId, task);
        task.getAttachments().add(file);

        when(attachmentFileRepository.findById(fileId)).thenReturn(Optional.of(file));

        attachmentFileServiceImp.deleteFile(fileId);

        ArgumentCaptor<AttachmentFile> captor = ArgumentCaptor.forClass(AttachmentFile.class);
        verify(attachmentFileRepository).save(captor.capture());
        assertTrue(captor.getValue().isDeleted());

        assertFalse(task.getAttachments().contains(file));
    }

    @Test
    void deleteFile_AttachmentFileNotFound() {
        Long fileId = 1L;
        when(attachmentFileRepository.findById(fileId)).thenReturn(Optional.empty());
        assertThrows(AttachmentFileNotFoundException.class, () -> {
            attachmentFileServiceImp.deleteFile(fileId);
        });
    }

    @Test
    void deleteFile_AlreadyDeleted() {
        Long fileId = 1L;
        TaskEntity task = createValidTask(10L);
        AttachmentFile file = createValidAttachmentFile(fileId, task);
        file.setDeleted(true);
        when(attachmentFileRepository.findById(fileId)).thenReturn(Optional.of(file));
        assertThrows(AttachmentFileNotFoundException.class, () -> {
            attachmentFileServiceImp.deleteFile(fileId);
        });
    }

    // ------------------ getFile Tests ------------------
    @Test
    void getFile_successful() throws Exception {
        Long fileId = 1L;
        TaskEntity task = createValidTask(10L);
        AttachmentFile file = createValidAttachmentFile(fileId, task);
        when(attachmentFileRepository.findById(fileId)).thenReturn(Optional.of(file));

        byte[] data = attachmentFileServiceImp.getFile(fileId);
        assertNotNull(data);
        assertEquals("dummy content", new String(data));
    }

    @Test
    void getFile_AttachmentFileNotFound() {
        Long fileId = 1L;
        when(attachmentFileRepository.findById(fileId)).thenReturn(Optional.empty());
        assertThrows(AttachmentFileNotFoundException.class, () -> {
            attachmentFileServiceImp.getFile(fileId);
        });
    }

    // ------------------ updateFile Tests ------------------
    @Test
    void updateFile_successful() throws Exception {
        Long fileId = 1L;
        Long newTaskId = 2L;
        String newDescription = "Updated description";
        MockMultipartFile multipartFile = new MockMultipartFile("file", "update.txt", "text/plain", "new content".getBytes());

        TaskEntity oldTask = createValidTask(10L);
        AttachmentFile file = createValidAttachmentFile(fileId, oldTask);
        oldTask.getAttachments().add(file);
        when(attachmentFileRepository.findById(fileId)).thenReturn(Optional.of(file));

        TaskEntity newTask = createValidTask(newTaskId);
        when(taskRepository.findById(newTaskId)).thenReturn(Optional.of(newTask));

        attachmentFileServiceImp.updateFile(fileId, newTaskId, newDescription, multipartFile);

        assertFalse(oldTask.getAttachments().contains(file));
        assertTrue(newTask.getAttachments().contains(file));
        assertEquals(newDescription, file.getDescription());
        assertEquals("update.txt", file.getFileName());
        assertEquals("text/plain", file.getFileType());
        assertEquals("new content", new String(file.getFileData()));
    }

    @Test
    void updateFile_IOException() throws Exception {
        Long fileId = 1L;
        Long newTaskId = 2L;
        String newDescription = "Updated description";
        MultipartFile fileMock = mock(MultipartFile.class);
        when(fileMock.getOriginalFilename()).thenReturn("update.txt");
        when(fileMock.getContentType()).thenReturn("text/plain");
        when(fileMock.getBytes()).thenThrow(new IOException("IO error"));

        TaskEntity oldTask = createValidTask(10L);
        AttachmentFile file = createValidAttachmentFile(fileId, oldTask);
        oldTask.getAttachments().add(file);
        when(attachmentFileRepository.findById(fileId)).thenReturn(Optional.of(file));

        TaskEntity newTask = createValidTask(newTaskId);
        when(taskRepository.findById(newTaskId)).thenReturn(Optional.of(newTask));

        assertThrows(IOException.class, () -> {
            attachmentFileServiceImp.updateFile(fileId, newTaskId, newDescription, fileMock);
        });
    }

    @Test
    void updateFile_AttachmentFileNotFound() {
        Long fileId = 1L;
        when(attachmentFileRepository.findById(fileId)).thenReturn(Optional.empty());
        assertThrows(AttachmentFileNotFoundException.class, () -> {
            attachmentFileServiceImp.updateFile(fileId, 2L, "desc", new MockMultipartFile("file", "update.txt", "text/plain", "content".getBytes()));
        });
    }

    @Test
    void updateFile_TaskNotFound() {
        Long fileId = 1L;
        Long newTaskId = 2L;
        String newDescription = "Updated description";
        MockMultipartFile multipartFile = new MockMultipartFile("file", "update.txt", "text/plain", "new content".getBytes());

        TaskEntity oldTask = createValidTask(10L);
        AttachmentFile file = createValidAttachmentFile(fileId, oldTask);
        oldTask.getAttachments().add(file);
        when(attachmentFileRepository.findById(fileId)).thenReturn(Optional.of(file));

        when(taskRepository.findById(newTaskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> {
            attachmentFileServiceImp.updateFile(fileId, newTaskId, newDescription, multipartFile);
        });
    }
}