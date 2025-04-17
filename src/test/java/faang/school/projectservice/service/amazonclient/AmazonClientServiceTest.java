package faang.school.projectservice.service.amazonclient;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.exception.FileDeleteException;
import faang.school.projectservice.exception.FileDownloadException;
import faang.school.projectservice.exception.FileUploadException;
import faang.school.projectservice.exception.InvalidFormatFile;
import faang.school.projectservice.service.image.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmazonClientServiceTest {

    @Mock
    private AmazonS3 s3client;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private ImageService imageService;

    @Mock
    private S3Object s3Object;

    @InjectMocks
    private AmazonClientService amazonClientService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(amazonClientService, "bucketName", "projectbucket");
    }

    @Test
    void updateProjectCoverInvalidFormatTest() {
        when(multipartFile.getOriginalFilename()).thenReturn("file.txt");

        assertThrows(InvalidFormatFile.class, () -> amazonClientService.updateProjectCover(multipartFile));
    }

    @Test
    void updateProjectCoverThrowsIOExceptionTest() throws IOException {
        when(multipartFile.getOriginalFilename()).thenReturn("first.png");
        when(multipartFile.getInputStream()).thenThrow(IOException.class);

        assertThrows(RuntimeException.class, () -> amazonClientService.updateProjectCover(multipartFile));
    }

    @Test
    void updateProjectCoverTest() throws IOException {

        String fileName = "image.png";
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());

        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(imageService.resizeImage(any(BufferedImage.class))).thenReturn(image);

        String result = amazonClientService.updateProjectCover(multipartFile);

        assertTrue(result.contains("-" + fileName));
    }

    @Test
    void getProjectCoverHandlesIOExceptionTest() throws IOException {

        S3ObjectInputStream inputStream = mock(S3ObjectInputStream.class);
        when(s3client.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(inputStream);
        when(inputStream.readAllBytes()).thenThrow(new IOException("Test exception"));

        assertThrows(RuntimeException.class, () -> amazonClientService.getProjectCover("test.png"));
    }

    @Test
    void getProjectCoverTest() {
        String fileName = "first.png";
        InputStream inputStream = new ByteArrayInputStream("test image".getBytes());
        S3Object s3Object = new S3Object();
        s3Object.setObjectContent(inputStream);

        when(s3client.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);

        byte[] result = amazonClientService.getProjectCover(fileName);

        assertEquals("test image", new String(result));
    }

    @Test
    public void uploadFileTest() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                "text/plain", "This is a test".getBytes());
        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);

        String folder = "test-folder";
        String key = amazonClientService.uploadFile(file, folder);

        verify(s3client, times(1)).putObject(captor.capture());
        PutObjectRequest capturedObjectRequest = captor.getValue();

        assertEquals(key, capturedObjectRequest.getKey());
    }

    @Test
    public void uploadFileThrowsExceptionTest() throws IOException {
        String folder = "test-folder";
        String contentType = "text/plain";
        String fileName = "test.txt";
        MockMultipartFile file = new MockMultipartFile(
                "file", fileName, contentType,
                new ByteArrayInputStream("This is a test".getBytes())
        );

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn(fileName);
        when(mockFile.getContentType()).thenReturn(contentType);
        when(mockFile.getSize()).thenReturn(file.getSize());
        when(mockFile.getInputStream()).thenThrow(new IOException("Stream error"));

        FileUploadException exception = assertThrows(FileUploadException.class,
                () -> amazonClientService.uploadFile(mockFile, folder));
        assertTrue(exception.getMessage().contains("Error uploading file with key"));

        verify(s3client, never()).putObject(any(PutObjectRequest.class));
    }

    @Test
    public void downloadFileTest() throws IOException {
        String bucketName = "projectbucket";
        String key = "Test-key";
        String fileContent = "File content";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());
        S3Object s3Object = new S3Object();
        s3Object.setObjectContent(inputStream);

        when(s3client.getObject(bucketName, key)).thenReturn(s3Object);

        S3ObjectInputStream resultStream = amazonClientService.downloadFile(key);

        String resultContent = new String(resultStream.readAllBytes());
        verify(s3client, times(1)).getObject(bucketName, key);
        assertNotNull(resultStream);
        assertEquals(fileContent, resultContent);
    }

    @Test
    public void downloadAllFilesTest() throws IOException {
        String bucketName = "projectbucket";
        String key1 = "Test-key1";
        String key2 = "Test-key2";
        String fileName1 = "File1.txt";
        String fileName2 = "File2.txt";
        String fileContent1 = "File content 1";
        String fileContent2 = "File content 2";
        Map<String, String> filesNamesWithKeys = new HashMap<>(Map.of(fileName1, key1, fileName2, key2));

        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(fileContent1.getBytes());
        ByteArrayInputStream inputStream2 = new ByteArrayInputStream(fileContent2.getBytes());
        S3Object s3Object1 = new S3Object();
        S3Object s3Object2 = new S3Object();
        s3Object1.setObjectContent(inputStream1);
        s3Object2.setObjectContent(inputStream2);

        when(s3client.getObject(bucketName, key1)).thenReturn(s3Object1);
        when(s3client.getObject(bucketName, key2)).thenReturn(s3Object2);

        Map<String, S3ObjectInputStream> result = amazonClientService.downloadAllFiles(filesNamesWithKeys);

        String resultContent1 = new String(result.get(fileName1).readAllBytes());
        String resultContent2 = new String(result.get(fileName2).readAllBytes());

        verify(s3client, times(1)).getObject(bucketName, key1);
        verify(s3client, times(1)).getObject(bucketName, key2);

        assertEquals(2, result.size());
        assertTrue(result.containsKey(fileName1));
        assertTrue(result.containsKey(fileName2));
        assertEquals(fileContent1, resultContent1);
        assertEquals(fileContent2, resultContent2);
    }

    @Test
    public void downloadFileThrowExceptionTest() {
        String bucketName = "projectbucket";
        String key = "Test-key";

        when(s3client.getObject(bucketName, key)).thenThrow(AmazonS3Exception.class);

        assertThrows(FileDownloadException.class,
                () -> amazonClientService.downloadFile(key));

        verify(s3client, times(1)).getObject(bucketName, key);
    }

    @Test
    public void deleteFileTest() {
        String bucketName = "projectbucket";
        String key = "test-key";

        assertDoesNotThrow(() -> amazonClientService.deleteFile(key));

        verify(s3client).deleteObject(bucketName, key);
    }

    @Test
    public void deleteFileThrowExceptionTest() {
        String bucketName = "projectbucket";
        String key = "test-key";
        doThrow(new AmazonS3Exception("S3 error")).when(s3client).deleteObject(bucketName, key);

        FileDeleteException exception = assertThrows(FileDeleteException.class,
                () -> amazonClientService.deleteFile(key));
        assertEquals("Error deleting file with key: " + key, exception.getMessage());

        verify(s3client, times(1)).deleteObject(bucketName, key);
    }
}