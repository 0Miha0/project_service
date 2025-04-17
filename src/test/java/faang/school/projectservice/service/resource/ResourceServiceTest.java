package faang.school.projectservice.service.resource;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    ResourceRepository resourceRepository;

    @InjectMocks
    ResourceService resourceService;

    @Test
    public void getResourceTest() {
        long resourceId = 1L;
        Resource resource = Resource.builder().build();

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.ofNullable(resource));

        assertDoesNotThrow(() -> resourceService.getResource(resourceId));
    }

    @Test
    public void getResourceThrowsExceptionTest() {
        long resourceId = 1L;

        doThrow(EntityNotFoundException.class).when(resourceRepository).findById(resourceId);

        assertThrows(EntityNotFoundException.class,
                () -> resourceService.getResource(resourceId));
    }

    @Test
    public void saveResourceTest() {
        Resource resource = Resource.builder().build();

        resourceService.save(resource);

        verify(resourceRepository, times(1)).save(resource);
    }
}