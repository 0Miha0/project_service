package faang.school.projectservice.service;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class ImageService {

    public byte[] optimizeImage(MultipartFile file) {
        log.info("Optimizing image {}", file.getOriginalFilename());
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());

            if (image == null) {
                log.warn("Failed to read image data");
                return new byte[0];
            }

            int width = image.getWidth();
            int height = image.getHeight();

            if (width > 1080 || height > 1080) {
                int targetWidth = 1080;
                int targetHeight;

                if (width > height) {
                    targetHeight = (int) (height * (1080.0 / width));
                    if (targetHeight > 566) {
                        targetHeight = 566;
                    }
                } else {
                    targetHeight = 1080;
                }

                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    Thumbnails.of(image)
                            .size(targetWidth, targetHeight)
                            .outputFormat("png")
                            .toOutputStream(outputStream);
                    return outputStream.toByteArray();
                }
            }
            log.info("Image {} is already optimized", file.getOriginalFilename());
            return file.getBytes();
        } catch (IOException e) {
            log.warn("Error optimizing image: {}", file.getOriginalFilename(), e);
            return new byte[0];
        }
    }
}
