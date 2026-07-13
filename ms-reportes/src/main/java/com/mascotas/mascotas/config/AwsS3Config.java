package com.mascotas.mascotas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsS3Config {

    @Value("${aws.s3.region:us-east-1}")
    private String region;

    @Value("${aws.s3.access-key:}")
    private String accessKey;

    @Value("${aws.s3.secret-key:}")
    private String secretKey;

    @Value("${aws.s3.session-token:}")
    private String sessionToken;

    @Bean
    public S3Client s3Client() {
        AwsCredentialsProvider credentialsProvider;

        if (accessKey != null && !accessKey.trim().isEmpty() && secretKey != null && !secretKey.trim().isEmpty()) {
            if (sessionToken != null && !sessionToken.trim().isEmpty()) {
                credentialsProvider = StaticCredentialsProvider.create(
                        AwsSessionCredentials.create(accessKey, secretKey, sessionToken)
                );
            } else {
                credentialsProvider = StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                );
            }
        } else {
            credentialsProvider = DefaultCredentialsProvider.create();
        }

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
