package faang.school.projectservice.validator.project;

import faang.school.projectservice.exception.customexception.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProjectValidator {

    public void nonvalizesLime(long fileSize, long maxImageSize) {
        if(fileSize > maxImageSize) {
            log.warn("The size of the image exceeds {}", maxImageSize);
            throw new DataValidationException("The size of the image exceeds " + maxImageSize);
        }
    }

    public void gallerySizeValidate(int size , int maxImage) {
        if(size >= maxImage) {
            log.warn("You have reached the gallery limit");
            throw new DataValidationException("You have reached the gallery limit");
        }
    }
}
