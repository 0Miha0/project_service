package faang.school.projectservice.service.resource;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public Resource findById(Long id) {
        log.info("Finding resource with id: {}", id);
        return resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with id: " + id));
    }

    public void save(Resource resource) {
        log.info("Saving resource: {}", resource.getName());
        resourceRepository.save(resource);
        log.info("Resource saved successfully: {}", resource.getName());
    }
}
