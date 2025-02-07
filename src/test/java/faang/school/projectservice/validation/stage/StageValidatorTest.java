package faang.school.projectservice.validation.stage;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.validator.stage.StageValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StageValidatorTest {

    private StageValidator stageValidator;

    @BeforeEach
    void setUp() {
        stageValidator = new StageValidator();
    }

    @Test
    @DisplayName("Validation on null value")
    public void validationOnNullTest() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> stageValidator.validationOnNull(null, "Value cannot be null"));

        assertEquals("Value cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Validation num on null or less than zero")
    public void validationOnNumLessThanZeroTest() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> stageValidator.validationNumOnNullAndLessThanZero(0, "Number cannot be less than zero"));

        assertEquals("Number cannot be less than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Validation on null or empty string")
    public void validationOnEmptyOrNullStringTest() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> stageValidator.validationOnNull(null, "Value cannot be empty"));

        assertEquals("Value cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Validation on null and empty list")
    public void validationOnNullOrEmptyListTest() {
        List<String> nullList = List.of();

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> stageValidator.validationOnNullOrEmptyList(nullList, "List cannot be null or empty"));

        assertEquals("List cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Validation different stages")
    public void validateDifferentStagesTest() {
        Stage sourceStage = Stage.builder().stageId(1L).build();
        Stage transferStage = Stage.builder().stageId(1L).build();
        String message = "Validating that source and transfer stages are different";

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> stageValidator.validateDifferentStages(sourceStage, transferStage, message));

        assertEquals("Validating that source and transfer stages are different", exception.getMessage());

    }
}
