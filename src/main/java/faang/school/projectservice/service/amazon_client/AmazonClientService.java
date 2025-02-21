package faang.school.projectservice.service.amazon_client;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.exception.customexception.FileDeleteException;
import faang.school.projectservice.exception.customexception.FileDownloadException;
import faang.school.projectservice.exception.customexception.FileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonClientService {

    private final AmazonS3 s3client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String folder) {
        log.info("Uploading file {} to S3 with folder: {}", file.getOriginalFilename(), folder);
        String key = (String.format("%s/%d%s", folder,
                System.currentTimeMillis(), file.getOriginalFilename()));
        log.info("Uploading file to S3 with key: {}", key);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
            s3client.putObject(request);
            log.info("File uploaded to S3 with key: {}", key);
        } catch (IOException e) {
            log.warn("Error uploading file to S3 with key: {}", key, e);
            throw new FileUploadException("Error uploading file to S3 with key: " + key);
        }
        log.info("File uploaded to S3 with key: {}", key);
        return key;
    }

    public S3ObjectInputStream downloadFile(String key) {
        return downloadFileFromS3(key);
    }

    public Map<String, S3ObjectInputStream> downloadAllFiles(Map<String, String> filesNamesWithKeys) {
        Map<String, S3ObjectInputStream> result = new HashMap<>();
        filesNamesWithKeys.forEach((name, key) -> result.put(name, downloadFile(key)));
        return result;
    }

    public void deleteFile(String key) {
        log.info("Deleting file from S3 with key: {}", key);
        try {
            s3client.deleteObject(bucketName, key);
            log.info("File deleted from S3 with key: {}", key);
        } catch (AmazonS3Exception e) {
            log.warn("Error deleting file from S3 with key: {}", key, e);
            throw new FileDeleteException("Error deleting file from S3 with key: " + key);
        }

    }

    private S3ObjectInputStream downloadFileFromS3(String key) {
        log.info("Downloading file from S3 with key: {}", key);
        try {
            log.info("File downloaded from S3 with key: {}", key);
            return s3client.getObject(bucketName, key).getObjectContent();
        } catch (AmazonS3Exception e) {
            log.warn("Error downloading file from S3 with key: {}", key, e);
            throw new FileDownloadException("Error downloading file from S3 with key: " + key);
        }
    }
}
