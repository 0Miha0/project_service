package faang.school.projectservice.exception;

import faang.school.projectservice.exception.customexception.DataValidationException;
import faang.school.projectservice.exception.customexception.FileDownloadException;
import faang.school.projectservice.exception.customexception.FileUploadException;
import faang.school.projectservice.exception.customexception.StorageExceededException;
import faang.school.projectservice.exception.customexception.StreamingFileError;
import faang.school.projectservice.exception.customexception.ZippingFileError;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(Exception ex) {
        return buildResponse(ex);
    }

    @ExceptionHandler({DataValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(Exception ex) {
        return buildResponse(ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex) {
        return buildResponse(ex);
    }

    @ExceptionHandler({FileDownloadException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleDiceBearException(FileDownloadException ex) {
        return buildResponse(ex);
    }

    @ExceptionHandler({FileUploadException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleMinioException(FileUploadException ex) {
        return buildResponse(ex);
    }

    @ExceptionHandler({StorageExceededException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleAvatarNotFoundException(StorageExceededException ex) {
        return buildResponse(ex);
    }

    @ExceptionHandler({ZippingFileError.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleFileDownloadException(ZippingFileError ex) {
        return buildResponse(ex);
    }

    @ExceptionHandler({StreamingFileError.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleStreamingFileError(StreamingFileError ex) {
        return buildResponse(ex);
    }

    private ErrorResponse buildResponse(Exception ex) {
        log.error(ex.getClass().getName(), ex);
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(ex.getClass().getName())
                .message(Objects.requireNonNullElse(ex.getMessage(), "No message available"))
                .build();
    }
}
