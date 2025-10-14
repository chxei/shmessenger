package org.chxei.shmessenger.controller;

import org.chxei.shmessenger.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "/file")
public class FileController {
    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    Long uploadImage(@RequestParam MultipartFile multipartFile) throws IOException {
        return fileService.saveFile(multipartFile);
    }

    @GetMapping(produces = MediaType.IMAGE_JPEG_VALUE)
    Resource downloadImage(@RequestParam Long fileId) {
        return fileService.downloadImage(fileId);
    }
}
