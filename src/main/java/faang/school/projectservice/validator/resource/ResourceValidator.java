package faang.school.projectservice.validator.resource;

import faang.school.projectservice.exception.customexception.DataValidationException;
import faang.school.projectservice.exception.customexception.StorageExceededException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Slf4j
@Component
public class ResourceValidator {

    @Value("${project-files.max-project-file-size}")
    public long maxProjectFileSize;

    public void validateAllowedToDeleteFile(Resource resource, TeamMember teamMember) {
        if (!resource.getCreatedBy().getId().equals(teamMember.getId())
                && teamMember.getRoles().stream()
                .noneMatch(teamRole -> teamRole.equals(TeamRole.MANAGER))) {
            log.warn("Delete file allowed only creator or manager");
            throw new DataValidationException("Delete file allowed only creator or manager");
        }
    }

    public void validateMaxStorageSizeIsNotNull(BigInteger maxStorageSize) {
        if (maxStorageSize == null) {
            log.warn("Max storage size not set for the project.");
            throw new IllegalStateException("Max storage size not set for the project.");
        }
    }

    public void validateStorageSizeNotExceeded(BigInteger maxStorageSize,
                                               BigInteger currentStorageSize) {
        if (maxStorageSize.compareTo(currentStorageSize) < 0) {
            log.warn("Storage exceeded by {} bytes", maxStorageSize.subtract(currentStorageSize));
            throw new StorageExceededException("Storage can't exceed 2 Gb ");
        }
    }

    public void validateFileSizeNotBigger2Gb(Long fileSize) {
        if (fileSize > maxProjectFileSize) {
            log.warn("File size is {} bytes, which is too big", fileSize);
            throw new DataValidationException("Max uploading file size can't be more than 2GB");
        }
    }
}