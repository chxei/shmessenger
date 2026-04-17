package org.chxei.shmessenger.service;

import com.tinify.Source;
import com.tinify.Tinify;
import org.chxei.shmessenger.entity.File;
import org.chxei.shmessenger.repository.FileRepository;
import org.chxei.shmessenger.utils.Misc;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FileService {
    private final String fileDirectory = System.getProperty("user.dir") + Misc.dotenv.get("UPLOAD_DIRECTORY");
    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    //todo: handle case when file save succeeds on filesystem but fails on database
    public Long saveFile(MultipartFile multipartFile) {
        var file = new File();

        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            originalFilename = "unnamed_" + System.currentTimeMillis();
        }
        // Sanitize filename to prevent path traversal
        String sanitizedFilename = new java.io.File(originalFilename).getName();
        file.setName(sanitizedFilename);

        try {
            file.setFileObject(multipartFile.getBytes());
            var directory = new java.io.File(fileDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            var dest = new java.io.File(directory, sanitizedFilename);
            multipartFile.transferTo(dest);
        } catch (Exception e) {
            Misc.logger.error("Could not save file to filesystem: {}", e.getMessage());
        }

        try {
            Source source = Tinify.fromBuffer(multipartFile.getBytes());
            file.setFileCompressed(source.toBuffer());
        } catch (Exception _) {
            Misc.logger.info("cant compress image");
        }
        file.setStoredLocally(false);

        return fileRepository.save(file).getId();
    }

    public Resource downloadImage(Long fileId) {
        byte[] file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getFileCompressed();

        return new ByteArrayResource(file);
    }
}
