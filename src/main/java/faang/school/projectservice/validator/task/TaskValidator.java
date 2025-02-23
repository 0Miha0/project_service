package faang.school.projectservice.validator.task;

import faang.school.projectservice.exception.customexception.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskValidator {

    public void validateTaskIdIsNull(Long taskId) {
        log.info("Task id is empty: {}", taskId);
        if (taskId != null) {
            log.warn("Task id is not empty: {}", taskId);
            throw new DataValidationException("Task id should be empty");
        }
    }

    public void validateTaskIdIsNotNull(Long taskId) {
        log.info("Task id is not empty: {}", taskId);
        if (taskId == null) {
            log.warn("Task id is empty");
            throw new DataValidationException("Task id can't be empty");
        }
    }
}
