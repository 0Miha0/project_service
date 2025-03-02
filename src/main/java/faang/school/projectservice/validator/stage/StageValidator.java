package faang.school.projectservice.validator.stage;

import faang.school.projectservice.exception.customexception.DataValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Component
public class StageValidator {

    public <T> void validationOnNull(T value, String message) {
        if (value == null) {
            throw new DataValidationException(message);
        }
    }

    public <T extends Number> void validationNumOnNullAndLessThanZero(T value, String message) {
        if (value == null || value.doubleValue() <= 0) {
            throw new DataValidationException(message);
        }
    }

    public void validationOnEmptyString(String value, String message) {
        if (value != null && value.trim().isEmpty()) {
            log.error(message);
            throw new DataValidationException(message);
        }
    }

    public  <T> void validationOnNullOrEmptyList(List<T> list, String message) {
        if (list == null || list.isEmpty()) {
            log.warn(message);
            throw new DataValidationException(message);
        }
    }
}
