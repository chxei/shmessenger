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


    public Long saveFile(MultipartFile multipartFile) {
        File file = new File();

        file.setName(multipartFile.getOriginalFilename());
        try {
            file.setFileObject(multipartFile.getBytes());
            java.io.File dest = new java.io.File(fileDirectory + file.getName());
            multipartFile.getInputStream().close();
            multipartFile.transferTo(dest);
        } catch (Exception e) {
            Misc.logger.info("cant save file");
        }


        try {
            Source source = Tinify.fromBuffer(multipartFile.getBytes());
            file.setFileCompressed(source.toBuffer());
        } catch (Exception e) {
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
