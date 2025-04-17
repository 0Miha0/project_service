package faang.school.projectservice.validator.task;

import faang.school.projectservice.exception.DataValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskValidatorTest {

    private TaskValidator taskValidator = new TaskValidator();

    @Test
    void validateTaskIdIsNotNullThrowsExceptionTest() {
        Long taskId = null;

        assertThrows(DataValidationException.class,
                () -> taskValidator.validateTaskIdIsNotNull(taskId));
    }

    @Test
    void validateTaskIdIsNotNullTest() {
        Long taskId = 1L;

        assertDoesNotThrow(() -> taskValidator.validateTaskIdIsNotNull(taskId));
    }

    @Test
    void validateTaskIdIsNullThrowsExceptionTest() {
        Long taskId = 1L;

        assertThrows(DataValidationException.class,
                () -> taskValidator.validateTaskIdIsNull(taskId));
    }

    @Test
    void validateTaskIdIsNull() {
        Long taskId = null;

        assertDoesNotThrow(() -> taskValidator.validateTaskIdIsNull(taskId));
    }
}