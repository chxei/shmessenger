package org.chxei.shmessenger.controller;

import com.tinify.Source;
import com.tinify.Tinify;
import org.chxei.shmessenger.entity.File;
import org.chxei.shmessenger.repository.FileRepository;
import org.chxei.shmessenger.utils.Misc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping(value = "/file")
public class FileController {
    private final FileRepository fileRepository;
    private final String fileDirectory = System.getProperty("user.dir") + "/src/main/resources/static/uploads/images/original/";

    @Autowired
    public FileController(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @PostMapping
    Long uploadImage(@RequestParam MultipartFile multipartFile) throws IOException {
        File file = new File();

        file.setName(multipartFile.getOriginalFilename());
        file.setFileObject(multipartFile.getBytes());

        java.io.File dest = new java.io.File(fileDirectory + file.getName());
        multipartFile.getInputStream().close();
        multipartFile.transferTo(dest);
        try {
            Source source = Tinify.fromBuffer(multipartFile.getBytes());
            file.setFileCompressed(source.toBuffer());
        } catch (Exception e) {
            Misc.logger.info("cant compress image");
        }
        file.setStoredLocally(false);

        return fileRepository.save(file).getId();
    }

    @GetMapping(produces = MediaType.IMAGE_JPEG_VALUE)
    Resource downloadImage(@RequestParam Long fileId) {
        byte[] file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getFileCompressed();

        return new ByteArrayResource(file);
    }
}
