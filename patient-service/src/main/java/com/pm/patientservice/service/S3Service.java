package com.pm.patientservice.service;

import com.pm.patientservice.dto.PresignedPutResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3;
    private final S3Presigner presigner;
    private final String bucket;


    public S3Service(S3Client s3, S3Presigner presigner, @Value("${aws.s3.bucket}") String bucket) {
        this.s3 = s3;
        this.presigner = presigner;
        this.bucket = bucket;
    }

    public String generateKeyForPatientId(String patientId, String originalFilename) {
        String ext = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) ext = originalFilename.substring(i);
        return String.format("patients/%s/id-proof/%s%s", patientId, UUID.randomUUID(), ext);
    }

    public PresignedPutResponse getPresignedPutUrl(String key , String contentType, Duration expiry,String fileSize){
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(expiry)
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presigned = presigner.presignPutObject(putObjectPresignRequest);

        return new PresignedPutResponse(presigned.url().toString() , presigned.signedHeaders());
    }


    public boolean exists(String key) {
        try {
            HeadObjectRequest h = HeadObjectRequest.builder().bucket(bucket).key(key).build();
            s3.headObject(h);
            return true;
        } catch (S3Exception ex) {
            return false;
        }
    }

    }
