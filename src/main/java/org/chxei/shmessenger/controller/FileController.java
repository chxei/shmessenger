package org.chxei.shmessenger.controller;

import org.chxei.shmessenger.entity.File;
import org.chxei.shmessenger.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin
public class FileController {
    @Autowired
    private FileRepository fileRepository;

    @PostMapping(value = "/upload")
    Long uploadImage(@RequestParam MultipartFile multipartFile) throws Exception {
        File file = new File();
        file.setName(multipartFile.getName());
        file.setFile(multipartFile.getBytes());

        return fileRepository.save(file).getId();
    }

    @GetMapping(value = "/download/{fileId}", produces = MediaType.IMAGE_JPEG_VALUE)
    Resource downloadImage(@PathVariable Long fileId) {
        byte[] file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getFile();

        return new ByteArrayResource(file);
    }
}
