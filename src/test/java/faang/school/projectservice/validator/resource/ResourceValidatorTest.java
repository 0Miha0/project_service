package faang.school.projectservice.validator.resource;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.StorageExceededException;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResourceValidatorTest {
    private final ResourceValidator resourceValidator = new ResourceValidator();

    @Test
    public void allowedToDeleteFileThrowsExceptionTest() {
        TeamMember emptyTeamMember = new TeamMember();
        emptyTeamMember.setId(1L);
        Resource resource = Resource.builder()
                .createdBy(emptyTeamMember)
                .build();
        TeamMember teamMember = TeamMember.builder()
                .id(2L)
                .roles(new ArrayList<>())
                .build();

        assertThrows(DataValidationException.class,
                () -> resourceValidator.validateAllowedToDeleteFile(resource, teamMember));
    }

    @Test
    public void allowedToDeleteFileCorrectTeamMemberIdTest() {
        TeamMember teamMember = TeamMember.builder()
                .id(2L)
                .roles(new ArrayList<>())
                .build();
        Resource resource = Resource.builder()
                .createdBy(teamMember)
                .build();

        assertDoesNotThrow(
                () -> resourceValidator.validateAllowedToDeleteFile(resource, teamMember));
    }

    @Test
    public void allowedToDeleteFileCorrectTeamMemberRoleManagerTest() {
        TeamMember anotherTeamMember = new TeamMember();
        anotherTeamMember.setId(1L);
        TeamMember teamMember = TeamMember.builder()
                .id(2L)
                .roles(new ArrayList<>(List.of(TeamRole.MANAGER)))
                .build();
        Resource resource = Resource.builder()
                .createdBy(anotherTeamMember)
                .build();

        assertDoesNotThrow(
                () -> resourceValidator.validateAllowedToDeleteFile(resource, teamMember));
    }

    @Test
    public void checkMaxStorageSizeIsNotNullThrowExceptionTest() {
        BigInteger maxStorageSize = null;

        assertThrows(IllegalStateException.class,
                () -> resourceValidator.validateMaxStorageSizeIsNotNull(maxStorageSize));
    }

    @Test
    public void checkMaxStorageSizeIsNotNullTest() {
        BigInteger maxStorageSize = new BigInteger("1");

        assertDoesNotThrow(
                () -> resourceValidator.validateMaxStorageSizeIsNotNull(maxStorageSize));
    }

    @Test
    public void checkStorageSizeNotExceededThrowsExceptionTest() {
        BigInteger currentSize = new BigInteger("11");
        BigInteger maxSize = new BigInteger("10");

        assertThrows(StorageExceededException.class,
                () -> resourceValidator.validateStorageSizeNotExceeded(maxSize, currentSize));
    }

    @Test
    public void checkStorageSizeNotExceededTest() {
        BigInteger currentSize = new BigInteger("1");
        BigInteger maxSize = new BigInteger("10");

        assertDoesNotThrow(
                () -> resourceValidator.validateStorageSizeNotExceeded(maxSize, currentSize));
    }

    @Test
    public void fileSizeNotBigger2GbThrowsExceptionTest() {
        resourceValidator.maxProjectFileSize = 2147483648L;
        long fileSize = 2147483650L;

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> resourceValidator.validateFileSizeNotBigger2Gb(fileSize));

        assertEquals("Max uploading file size can't be more than 2GB", exception.getMessage());
    }

    @Test
    public void fileSizeNotBigger2GbTest() {
        resourceValidator.maxProjectFileSize = 2147483648L;
        long fileSize = 100L;

        assertDoesNotThrow(() -> resourceValidator.validateFileSizeNotBigger2Gb(fileSize));
    }
}