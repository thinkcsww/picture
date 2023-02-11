package com.applory.pictureserver.domain.file;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @ResponseBody
    @GetMapping("/images/{filename}")
    public byte[] downloadImage(@PathVariable String filename) throws IOException {
        return fileService.getFile(filename);
    }
}
