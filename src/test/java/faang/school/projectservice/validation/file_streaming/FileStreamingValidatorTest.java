package faang.school.projectservice.validation.file_streaming;

import faang.school.projectservice.exception.customexception.DataValidationException;
import faang.school.projectservice.validator.file_streaming.FileStreamingValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class FileStreamingValidatorTest {

    @InjectMocks
    private FileStreamingValidator fileStreamingValidator;

    @Test
    void validateFileStreamingTest() throws IOException {
        Path imagePath = Paths.get("src/test/resources/files/test-image.png");
        byte[] fileData = Files.readAllBytes(imagePath);
        InputStream fileStream = new ByteArrayInputStream(fileData);

        fileStreamingValidator.validateFileStreaming(fileStream, "message");
    }

    @Test
    void validateFileStreamingThrowsExceptionTest() {
        assertThrows(DataValidationException.class,
                () -> fileStreamingValidator.validateFileStreaming(null, "message"));
    }

    @Test
    void validateFilesStreamingTest() throws IOException {
        Path imagePath = Paths.get("src/test/resources/files/test-image.png");
        byte[] fileData = Files.readAllBytes(imagePath);
        InputStream fileStream = new ByteArrayInputStream(fileData);

        fileStreamingValidator.validateFilesStreaming(Map.of("test-image", fileStream), "message");
    }

    @Test
    void validateFilesStreamingThrowsExceptionTest() {
        assertThrows(DataValidationException.class,
                () -> fileStreamingValidator.validateFileStreaming(null, "message"));
    }
}
