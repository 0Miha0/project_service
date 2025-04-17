package faang.school.projectservice.service.image;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    private static final int MAX_FIRST_SIZE_IMAGE = 1080;
    private static final int MAX_SECOND_SIZE_IMAGE = 566;

    @InjectMocks
    private ImageService imageService;

    @Test
    void squareImageResizeTest() {
        BufferedImage inputImage = createImage(500, 500);
        BufferedImage resizedImage = imageService.resizeImage(inputImage);

        assertEquals(MAX_FIRST_SIZE_IMAGE, resizedImage.getWidth());
        assertEquals(MAX_FIRST_SIZE_IMAGE, resizedImage.getHeight());
    }

    @Test
    void tallImageResizeTest() {
        BufferedImage inputImage = createImage(400, 1200);
        BufferedImage resizedImage = imageService.resizeImage(inputImage);

        assertEquals(MAX_FIRST_SIZE_IMAGE, resizedImage.getHeight());
        assertTrue(resizedImage.getWidth() <= MAX_SECOND_SIZE_IMAGE);
    }

    @Test
    void wideImageResizeTest() {
        BufferedImage inputImage = createImage(1600, 900);
        BufferedImage resizedImage = imageService.resizeImage(inputImage);

        assertEquals(MAX_SECOND_SIZE_IMAGE, resizedImage.getHeight());
        assertTrue(resizedImage.getWidth() <= MAX_FIRST_SIZE_IMAGE);
    }

    @Test
    void outputImageTypeTest() {
        BufferedImage inputImage = createImage(800, 600);
        BufferedImage resizedImage = imageService.resizeImage(inputImage);

        assertEquals(BufferedImage.TYPE_INT_RGB, resizedImage.getType());
    }

    @Test
    void nullInputTest() {
        assertThrows(NullPointerException.class, () -> imageService.resizeImage(null));
    }

    private BufferedImage createImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, 0x000000);
            }
        }
        return image;
    }
}
