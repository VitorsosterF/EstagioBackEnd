package com.backendestagio.Obras.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path pastaUploads = Paths.get("uploads");

    public FileStorageService() throws IOException {
        Files.createDirectories(pastaUploads);
    }

    public String salvar(MultipartFile arquivo) throws IOException {
        String nomeArquivo = UUID.randomUUID() + "_" + arquivo.getOriginalFilename();
        Path destino = pastaUploads.resolve(nomeArquivo);
        Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/" + nomeArquivo;
    }

    public void deletar(String imagemUrl) {
        if (imagemUrl == null) return;
        try {
            String nomeArquivo = imagemUrl.replace("/uploads/", "");
            Files.deleteIfExists(pastaUploads.resolve(nomeArquivo));
        } catch (IOException e) {
            System.err.println("Erro ao deletar imagem: " + e.getMessage());
        }
    }
}