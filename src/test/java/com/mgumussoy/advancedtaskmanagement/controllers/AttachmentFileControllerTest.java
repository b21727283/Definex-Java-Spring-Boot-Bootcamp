package com.mgumussoy.advancedtaskmanagement.controllers;

import com.mgumussoy.advancedtaskmanagement.configs.GlobalExceptionHandler;
import com.mgumussoy.advancedtaskmanagement.exceptions.AttachmentFileNotFoundException;
import com.mgumussoy.advancedtaskmanagement.exceptions.TaskNotFoundException;
import com.mgumussoy.advancedtaskmanagement.services.AttachmentFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AttachmentFileControllerTest {

    private static final String API_BASE_PATH = "/attachments";

    private MockMvc mockMvc;

    @InjectMocks
    private AttachmentFileController attachmentFileController;

    @Mock
    private AttachmentFileService attachmentFileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(attachmentFileController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ------------------ Upload Endpoint Tests ------------------
    @Test
    void upload_ShouldReturnOk() throws Exception {
        String apiPath = API_BASE_PATH + "/upload";
        MockMultipartFile file = new MockMultipartFile("files", "test.txt", "text/plain", "test content".getBytes());

        mockMvc.perform(multipart(apiPath)
                        .file(file)
                        .param("taskId", "1")
                        .param("description", "Test description")
                        .contentType("multipart/form-data"))
                .andExpect(status().isOk());
    }

    @Test
    void upload_ThrowsIOException() throws Exception {
        String apiPath = API_BASE_PATH + "/upload";
        MockMultipartFile file = new MockMultipartFile("files", "test.txt", "text/plain", "test content".getBytes());

        doThrow(new IOException()).when(attachmentFileService).saveFiles(anyLong(), any(), anyList());

        mockMvc.perform(multipart(apiPath)
                        .file(file)
                        .param("taskId", "1")
                        .param("description", "Test description")
                        .contentType("multipart/form-data"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void upload_ThrowsTaskNotFoundException() throws Exception {
        String apiPath = API_BASE_PATH + "/upload";
        MockMultipartFile file = new MockMultipartFile("files", "test.txt", "text/plain", "test content".getBytes());

        doThrow(new TaskNotFoundException()).when(attachmentFileService).saveFiles(anyLong(), any(), anyList());

        mockMvc.perform(multipart(apiPath)
                        .file(file)
                        .param("taskId", "1")
                        .param("description", "Test description")
                        .contentType("multipart/form-data"))
                .andExpect(status().isNotFound());
    }

    // ------------------ Delete Endpoint Tests ------------------
    @Test
    void delete_ShouldReturnOk() throws Exception {
        String apiPath = API_BASE_PATH + "/1";

        mockMvc.perform(delete(apiPath))
                .andExpect(status().isOk());
    }

    @Test
    void delete_ThrowsAttachmentFileNotFoundException() throws Exception {
        String apiPath = API_BASE_PATH + "/1";

        doThrow(new AttachmentFileNotFoundException()).when(attachmentFileService).deleteFile(anyLong());

        mockMvc.perform(delete(apiPath))
                .andExpect(status().isBadRequest());
    }

    // ------------------ Get File Endpoint Tests ------------------
    @Test
    void getFile_ShouldReturnOk() throws Exception {
        String apiPath = API_BASE_PATH + "/1";
        byte[] dummyData = "dummy data".getBytes();
        when(attachmentFileService.getFile(anyLong())).thenReturn(dummyData);

        mockMvc.perform(get(apiPath))
                .andExpect(status().isOk());
    }

    @Test
    void getFile_ThrowsAttachmentFileNotFoundException() throws Exception {
        String apiPath = API_BASE_PATH + "/1";

        doThrow(new AttachmentFileNotFoundException()).when(attachmentFileService).getFile(anyLong());

        mockMvc.perform(get(apiPath))
                .andExpect(status().isBadRequest());
    }

    // ------------------ Update Endpoint Tests ------------------
    @Test
    void update_ShouldReturnOk() throws Exception {
        String apiPath = API_BASE_PATH + "/1";
        MockMultipartFile file = new MockMultipartFile("file", "update.txt", "text/plain", "update content".getBytes());

        mockMvc.perform(multipart(apiPath)
                        .file(file)
                        .param("taskId", "1")
                        .param("description", "Updated description")
                        .contentType("multipart/form-data"))
                .andExpect(status().isOk());
    }

    @Test
    void update_ThrowsIOException() throws Exception {
        String apiPath = API_BASE_PATH + "/1";
        MockMultipartFile file = new MockMultipartFile("file", "update.txt", "text/plain", "update content".getBytes());

        doThrow(new IOException()).when(attachmentFileService).updateFile(anyLong(), anyLong(), any(), any());

        mockMvc.perform(multipart(apiPath)
                        .file(file)
                        .param("taskId", "1")
                        .param("description", "Updated description")
                        .contentType("multipart/form-data"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void update_ThrowsAttachmentFileNotFoundException() throws Exception {
        String apiPath = API_BASE_PATH + "/1";
        MockMultipartFile file = new MockMultipartFile("file", "update.txt", "text/plain", "update content".getBytes());

        doThrow(new AttachmentFileNotFoundException()).when(attachmentFileService).updateFile(anyLong(), anyLong(), any(), any());

        mockMvc.perform(multipart(apiPath)
                        .file(file)
                        .param("taskId", "1")
                        .param("description", "Updated description")
                        .contentType("multipart/form-data"))
                .andExpect(status().isBadRequest());
    }
}