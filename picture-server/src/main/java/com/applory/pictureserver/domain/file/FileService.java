package com.applory.pictureserver.domain.file;


import com.mchange.v1.io.InputStreamUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Component
public class FileService {

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Value("${picture.upload-path}")
    private String fileDir;

    public String getFullPath(String filename) {
        return fileDir + "/" + filename;
    }

    public List<File> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<File> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }

    public void deleteFile(String fileId) {
        File fileInDB = fileRepository.findById(fileId).orElseThrow(() -> new NoSuchElementException("Can't find file id: " + fileId));
        java.io.File realFile = new java.io.File(getFullPath(fileInDB.getStoreFileName()));
        if (realFile.exists()) {
            realFile.delete();

            fileRepository.delete(fileInDB);
        }
    }

    public File storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
        multipartFile.transferTo(new java.io.File(getFullPath(storeFileName)));

        File file = File.builder()
                .originFileName(originalFilename)
                .storeFileName(storeFileName)
                .build();

        return fileRepository.save(file);
    }


    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }


    public byte[] getFile(String filename) throws IOException {
        FileSystemResource file = new FileSystemResource(getFullPath(filename));
        return InputStreamUtils.getBytes(file.getInputStream());

    }
}
