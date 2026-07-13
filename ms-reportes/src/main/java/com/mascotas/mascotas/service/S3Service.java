package com.mascotas.mascotas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region:us-east-1}")
    private String region;

    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("No se puede subir un archivo vacío");
        }

        // Generar nombre único
        String nombreArchivo = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(nombreArchivo)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Retornar la URL pública del archivo
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, nombreArchivo);

        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo para subir a S3", e);
        } catch (Exception e) {
            throw new RuntimeException("Error al subir archivo a AWS S3", e);
        }
    }
}
