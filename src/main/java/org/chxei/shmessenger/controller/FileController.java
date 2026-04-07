package org.chxei.shmessenger.controller;

import org.chxei.shmessenger.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/files")
public class FileController {
    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    Long uploadImage(@RequestParam MultipartFile multipartFile) {
        return fileService.saveFile(multipartFile);
    }

    @GetMapping(value = "/{fileId}", produces = MediaType.IMAGE_JPEG_VALUE)
    Resource downloadImage(@PathVariable Long fileId) {
        return fileService.downloadImage(fileId);
    }
}
