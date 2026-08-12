package org.chxei.shmessenger.service;

import com.tinify.Source;
import com.tinify.Tinify;
import org.chxei.shmessenger.entity.File;
import org.chxei.shmessenger.repository.FileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {"server.ssl.enabled=false", "spring.main.lazy-initialization=false"})
@Transactional
class FileServiceIntegrationTest {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileRepository fileRepository;

    @TempDir
    private Path tempDir;

    @Test
    void saveAndDownloadImageUseRealRepository() throws Exception {
        ReflectionTestUtils.setField(fileService, "fileDirectory", tempDir.toString());
        Source source = mock(Source.class);
        when(source.toBuffer()).thenReturn("compressed".getBytes());

        Long fileId;
        try (MockedStatic<Tinify> tinify = mockStatic(Tinify.class)) {
            tinify.when(() -> Tinify.fromBuffer("original".getBytes())).thenReturn(source);

            fileId = fileService.saveFile(new MockMultipartFile("multipartFile", "../photo.jpg", "image/jpeg", "original".getBytes()));
        }

        File savedFile = fileRepository.findById(fileId).orElseThrow();
        assertEquals("photo.jpg", savedFile.getName());
        assertArrayEquals("original".getBytes(), savedFile.getFileObject());
        assertArrayEquals("compressed".getBytes(), savedFile.getFileCompressed());
        assertTrue(Files.exists(tempDir.resolve("photo.jpg")));

        Resource resource = fileService.downloadImage(fileId);
        assertArrayEquals("compressed".getBytes(), resource.getContentAsByteArray());
    }

    @Test
    void downloadImageThrowsNotFoundForMissingFile() {
        assertThrows(ResponseStatusException.class, () -> fileService.downloadImage(999_999L));
    }
}
