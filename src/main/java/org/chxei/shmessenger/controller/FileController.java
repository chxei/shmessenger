package org.chxei.shmessenger.controller;

import org.chxei.shmessenger.entity.File;
import org.chxei.shmessenger.repository.FileRepository;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
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
    private final FileRepository fileRepository;
    private final String fileDirectory = System.getProperty("user.dir") + "/src/main/resources/static/uploads/images/original/";

    @Autowired
    public FileController(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @PostMapping(value = "/upload")
    Long uploadImage(@RequestParam MultipartFile multipartFile) throws Exception {
        File file = new File();

        file.setName(multipartFile.getOriginalFilename());
        file.setFile(multipartFile.getBytes());

        java.io.File dest = new java.io.File(fileDirectory + file.getName());
        multipartFile.getInputStream().close();
        multipartFile.transferTo(dest);
        Mat mat = Imgcodecs.imread(dest.getAbsolutePath());

//        Imgcodecs.imencode(".webp",
//                mat,
//                MatOfByte.fromNativeAddr(mat.getNativeObjAddr())
//        );
        Imgcodecs.imwrite(dest + file.getName(), mat);
        //        try{
//            Source source = Tinify.fromBuffer(multipartFile.getBytes());
//            file.setFileCompressed(source.toBuffer());
//        }
//        catch(Exception e){
//            Misc.logger.info("cant compress image");
//        }
//        file.setStoredLocally(false);

        return 1L;//fileRepository.save(file).getId();
    }

    @GetMapping(value = "/download/{fileId}", produces = MediaType.IMAGE_JPEG_VALUE)
    Resource downloadImage(@PathVariable Long fileId) {
        byte[] file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getFile();

        return new ByteArrayResource(file);
    }
}
