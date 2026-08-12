package org.chxei.shmessenger.service;

import com.tinify.Source;
import com.tinify.Tinify;
import org.chxei.shmessenger.entity.File;
import org.chxei.shmessenger.repository.FileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTests {

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileService fileService;

    @TempDir
    private Path tempDir;

    @Test
    void saveFileSanitizesFilenameAndStoresCompressedBytes() throws Exception {
        ReflectionTestUtils.setField(fileService, "fileDirectory", tempDir.toString());
        MockMultipartFile multipartFile = new MockMultipartFile("multipartFile", "../photo.jpg", "image/jpeg", "original".getBytes());
        Source source = mock(Source.class);
        when(source.toBuffer()).thenReturn("compressed".getBytes());
        when(fileRepository.save(any(File.class))).thenAnswer(invocation -> {
            File file = invocation.getArgument(0);
            file.setId(9L);
            return file;
        });

        try (MockedStatic<Tinify> tinify = mockStatic(Tinify.class)) {
            tinify.when(() -> Tinify.fromBuffer("original".getBytes())).thenReturn(source);

            Long fileId = fileService.saveFile(multipartFile);

            assertEquals(9L, fileId);
        }

        ArgumentCaptor<File> fileCaptor = ArgumentCaptor.captor();
        verify(fileRepository).save(fileCaptor.capture());
        File savedFile = fileCaptor.getValue();
        assertEquals("photo.jpg", savedFile.getName());
        assertArrayEquals("original".getBytes(), savedFile.getFileObject());
        assertArrayEquals("compressed".getBytes(), savedFile.getFileCompressed());
        assertFalse(savedFile.isStoredLocally());
    }

    @Test
    void saveFileUsesDefaultFilenameAndIgnoresTinifyFailure() {
        ReflectionTestUtils.setField(fileService, "fileDirectory", tempDir.toString());
        MockMultipartFile multipartFile = new MockMultipartFile("multipartFile", "", "image/jpeg", "original".getBytes());
        when(fileRepository.save(any(File.class))).thenAnswer(invocation -> {
            File file = invocation.getArgument(0);
            file.setId(10L);
            return file;
        });

        try (MockedStatic<Tinify> tinify = mockStatic(Tinify.class)) {
            tinify.when(() -> Tinify.fromBuffer("original".getBytes())).thenThrow(new RuntimeException("tinify failed"));

            Long fileId = fileService.saveFile(multipartFile);

            assertEquals(10L, fileId);
        }

        ArgumentCaptor<File> fileCaptor = ArgumentCaptor.captor();
        verify(fileRepository).save(fileCaptor.capture());
        assertTrue(fileCaptor.getValue().getName().startsWith("unnamed_"));
        assertArrayEquals("original".getBytes(), fileCaptor.getValue().getFileObject());
        assertNull(fileCaptor.getValue().getFileCompressed());
    }

    @Test
    void downloadImageReturnsCompressedBytes() throws Exception {
        File file = new File();
        file.setId(3L);
        file.setFileCompressed("compressed".getBytes());
        when(fileRepository.findById(3L)).thenReturn(Optional.of(file));

        Resource resource = fileService.downloadImage(3L);

        assertArrayEquals("compressed".getBytes(), resource.getContentAsByteArray());
    }

    @Test
    void downloadImageThrowsNotFoundWhenMissing() {
        when(fileRepository.findById(404L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> fileService.downloadImage(404L));

        assertEquals(404, exception.getStatusCode().value());
    }
}
