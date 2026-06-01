package com.pm.patientservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    @Value("${aws.region:us-east-1}")
    private String region;

    @Value("${aws.s3.endpoint:}") // optional for MinIO or custom endpoint
    private String endpoint;

    @Bean
    public S3Client s3Client(
            @Value("${aws.accessKeyId:}") String accessKeyId,
            @Value("${aws.secretAccessKey:}") String secretAccessKey) {

        AwsCredentialsProvider creds;
        if (!accessKeyId.isBlank() && !secretAccessKey.isBlank()) {
            creds = StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey));
        } else {
            creds = DefaultCredentialsProvider.create(); // use IAM role, env, profile, etc.
        }

        S3ClientBuilder builder = S3Client.builder()
                .credentialsProvider(creds)
                .region(Region.of(region));

        if (!endpoint.isBlank()) {
            builder.endpointOverride(java.net.URI.create(endpoint));
        }

        return builder.build();
    }
    @Bean
    public S3Presigner s3Presigner( @Value("${aws.accessKeyId:}") String accessKeyId,
                                    @Value("${aws.secretAccessKey:}") String secretAccessKey) {
        AwsCredentialsProvider credentials;
        if (!accessKeyId.isBlank() && !secretAccessKey.isBlank()) {
            credentials = StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey));
        } else {
            credentials = DefaultCredentialsProvider.create(); // use IAM role, env, profile, etc.
        }

        Region awsRegion = Region.of(region);
        return S3Presigner.builder()
                .region(awsRegion)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();
    }
}