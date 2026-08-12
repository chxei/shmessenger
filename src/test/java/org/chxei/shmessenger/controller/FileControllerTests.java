package org.chxei.shmessenger.controller;

import org.chxei.shmessenger.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileController.class)
@WithMockUser
class FileControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileService fileService;

    @Test
    void uploadReturnsFileId() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("multipartFile", "photo.jpg", "image/jpeg", "image".getBytes());
        when(fileService.saveFile(multipartFile)).thenReturn(42L);

        mockMvc.perform(multipart("/files").file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(content().string("42"));

        verify(fileService).saveFile(multipartFile);
    }

    @Test
    void downloadReturnsImageResource() throws Exception {
        when(fileService.downloadImage(42L)).thenReturn(new ByteArrayResource("image".getBytes()));

        mockMvc.perform(get("/files/42"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes("image".getBytes()));

        verify(fileService).downloadImage(42L);
    }
}
