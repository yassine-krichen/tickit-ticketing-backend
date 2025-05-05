package com.sofrecom.tickit.impl;

import com.sofrecom.tickit.config.StorageProperties;
import com.sofrecom.tickit.exception.FileStorageException;
import com.sofrecom.tickit.exception.ResourceNotFoundException;
import com.sofrecom.tickit.model.Attachment;
import com.sofrecom.tickit.model.TicketActivity;
import com.sofrecom.tickit.repository.AttachmentRepository;
import com.sofrecom.tickit.repository.TicketActivityRepository;
import com.sofrecom.tickit.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttachmentServiceImplTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private TicketActivityRepository activityRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private StorageProperties storageProperties;

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    private TicketActivity activity;
    private Attachment attachment;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        // Setup test data
        activity = new TicketActivity();
        activity.setId(1L);

        attachment = new Attachment();
        attachment.setId(1L);
        attachment.setActivity(activity);
        attachment.setFilename("test.pdf");
        attachment.setContentType("application/pdf");
        attachment.setSize(1024L);
        attachment.setStoragePath("2025/05/02/activity_1/uuid_test.pdf");

        mockFile = new MockMultipartFile(
            "test.pdf",
            "test.pdf",
            "application/pdf",
            "test content".getBytes()
        );

        // Only set up the stubs that will actually be used in tests
        when(storageProperties.getAllowedTypes()).thenReturn(new String[]{"application/pdf"});
        when(storageProperties.getMaxFileSize()).thenReturn(10 * 1024 * 1024L);
    }

    @Test
    void createAttachment_ValidFile_ReturnsAttachment() throws IOException {
        // Arrange
        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));
        when(fileStorageService.store(any(MultipartFile.class), eq(1L))).thenReturn("2025/05/02/activity_1/uuid_test.pdf");
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(attachment);

        // Act
        Attachment result = attachmentService.createAttachment(1L, mockFile);

        // Assert
        assertNotNull(result);
        assertEquals(attachment.getId(), result.getId());
        verify(fileStorageService).store(mockFile, 1L);
        verify(attachmentRepository).save(any(Attachment.class));
    }

    @Test
    void createAttachment_InvalidFileType_ThrowsException() throws IOException {
        // Arrange
        MockMultipartFile invalidFile = new MockMultipartFile(
            "test.exe",
            "test.exe",
            "application/octet-stream",
            "test content".getBytes()
        );
        
        // Only set up the mocks that will be used
        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));

        // Act & Assert
        assertThrows(FileStorageException.class, () -> {
            attachmentService.createAttachment(1L, invalidFile);
        });
        
        // Verify no interactions with the storage service
        verify(fileStorageService, never()).store(any(), anyLong());
    }

    @Test
    void getAttachmentsByActivityId_ActivityExists_ReturnsAttachments() {
        // Arrange
        when(activityRepository.existsById(1L)).thenReturn(true);
        when(attachmentRepository.findByActivity_Id(1L)).thenReturn(Arrays.asList(attachment));

        // Act
        List<Attachment> results = attachmentService.getAttachmentsByActivityId(1L);

        // Assert
        assertEquals(1, results.size());
        assertEquals(attachment.getId(), results.get(0).getId());
    }

    @Test
    void getAttachmentById_AttachmentExists_ReturnsAttachment() {
        // Arrange
        when(attachmentRepository.findByIdAndActivity_Id(1L, 1L)).thenReturn(Optional.of(attachment));

        // Act
        Attachment result = attachmentService.getAttachmentById(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(attachment.getId(), result.getId());
    }

    @Test
    void getAttachmentById_AttachmentNotFound_ThrowsException() {
        // Arrange
        when(attachmentRepository.findByIdAndActivity_Id(1L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            attachmentService.getAttachmentById(1L, 1L);
        });
    }

    @Test
    void deleteAttachment_AttachmentExists_PerformsSoftDelete() {
        // Arrange
        Attachment attachmentToDelete = Mockito.spy(attachment);
        when(attachmentRepository.findByIdAndActivity_Id(1L, 1L)).thenReturn(Optional.of(attachmentToDelete));

        // Act
        attachmentService.deleteAttachment(1L, 1L);

        // Assert
        verify(attachmentToDelete).setDeleted(true);
        verify(attachmentToDelete).setDeletedAt(any(LocalDateTime.class));
        verify(attachmentRepository).save(attachmentToDelete);
    }
}