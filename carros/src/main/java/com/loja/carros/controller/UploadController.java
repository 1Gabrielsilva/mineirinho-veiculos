package com.loja.carros.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("mensagem", "Arquivo vazio"));
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String newFilename = UUID.randomUUID().toString() + extension;
            Path path = Paths.get(UPLOAD_DIR + newFilename);
            
            // Ensure directory exists
            Files.createDirectories(path.getParent());
            
            Files.write(path, file.getBytes());

            return ResponseEntity.ok(Map.of("path", "uploads/" + newFilename));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("mensagem", "Erro ao salvar arquivo: " + e.getMessage()));
        }
    }
}
