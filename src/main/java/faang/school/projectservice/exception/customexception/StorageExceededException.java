package faang.school.projectservice.exception.customexception;

public class StorageExceededException extends RuntimeException {
    public StorageExceededException(String message) {
        super(message);
    }
}
