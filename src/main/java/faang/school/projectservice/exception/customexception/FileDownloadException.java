package faang.school.projectservice.exception.customexception;

public class FileDownloadException extends RuntimeException{
    public FileDownloadException(String message) {
        super(message);
    }
}
