package faang.school.projectservice.service.file_streaming;

import faang.school.projectservice.exception.customexception.DataValidationException;
import faang.school.projectservice.exception.customexception.StreamingFileError;
import faang.school.projectservice.validator.file_streaming.FileStreamingValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileStreamingServiceTest {

    @Mock
    private FileStreamingValidator fileStreamingValidator;

    @InjectMocks
    private FileStreamingService fileStreamingService;

    @Test
    void getStreamingResponseBodyTest() throws Exception {
        Path imagePath = Paths.get("src/test/resources/files/test-image.png");
        byte[] fileData = Files.readAllBytes(imagePath);
        InputStream fileStream = new ByteArrayInputStream(fileData);

        Mockito.doNothing().when(fileStreamingValidator).validateFileStreaming(Mockito.any(), Mockito.anyString());

        StreamingResponseBody response =
                fileStreamingService.getStreamingResponseBody(fileStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        assertNotNull(response);
        response.writeTo(outputStream);

        assertArrayEquals(fileData, outputStream.toByteArray());
    }

    @Test
    void getStreamingResponseBodyThrowsIOExceptionTest() throws IOException {
        InputStream faultyStream = mock(InputStream.class);
        when(faultyStream.read(any(byte[].class))).thenThrow(new IOException("Simulated stream error"));

        StreamingResponseBody responseBody = fileStreamingService.getStreamingResponseBody(faultyStream);

        assertThrows(StreamingFileError.class, () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            responseBody.writeTo(outputStream);
        });
    }

    @Test
    void getStreamingResponseBodyInZipTest() throws Exception {
        Map<String, InputStream> files = new HashMap<>();
        files.put("file1.txt", new ByteArrayInputStream("Content of file 1".getBytes()));
        files.put("file2.txt", new ByteArrayInputStream("Content of file 2".getBytes()));

        StreamingResponseBody response =
                fileStreamingService.getStreamingResponseBodyInZip(files);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        assertNotNull(response);
        response.writeTo(outputStream);

        try (ZipInputStream zipInputStream = new ZipInputStream(
                new ByteArrayInputStream(outputStream.toByteArray()))) {
            int fileCount = 0;
            while (zipInputStream.getNextEntry() != null) {
                fileCount++;
            }

            assertEquals(files.size(), fileCount);
        }
    }

    @Test
    void getStreamingResponseBodyInZipThrowsIOExceptionTest() throws IOException {
        InputStream faultyStream = mock(InputStream.class);
        when(faultyStream.read(any(byte[].class))).thenThrow(new IOException("Simulated stream error"));

        Map<String, InputStream> files = new HashMap<>();
        files.put("faulty.txt", faultyStream);

        StreamingResponseBody responseBody = fileStreamingService.getStreamingResponseBodyInZip(files);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        responseBody.writeTo(outputStream);

        try (ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(outputStream.toByteArray()))) {
            assertNull(zipIn.getNextEntry(), "Faulty file should not be included in the ZIP");
        }
    }
}
