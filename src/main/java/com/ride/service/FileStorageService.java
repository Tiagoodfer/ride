package com.ride.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    public String uploadFile(MultipartFile file) {

        // TODO: Implementar integração com AWS S3
        // AmazonS3 s3Client = ...
        // s3Client.putObject(...)

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        log.info("Simulating upload to S3 for file: {}", fileName);

        // Retorna uma URL fictícia por enquanto
        return "https://s3.amazonaws.com/ride-bucket/" + fileName;
    }
}
