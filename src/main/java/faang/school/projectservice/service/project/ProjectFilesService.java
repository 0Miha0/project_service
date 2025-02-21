package faang.school.projectservice.service.project;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.TeamMemberService;
import faang.school.projectservice.service.amazon_client.AmazonClientService;
import faang.school.projectservice.service.resource.ResourceService;
import faang.school.projectservice.validator.resource.ResourceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectFilesService {

    private final AmazonClientService amazonClient;
    private final ResourceService resourceService;
    private final ResourceValidator resourceValidator;
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final TeamMemberService teamMemberService;

    public void uploadFile(Long projectId, Long teamMemberId, MultipartFile file) {
        log.info("Uploading file: {} to project with ID: {}", file.getOriginalFilename(), projectId);
        resourceValidator.validateFileSizeNotBigger2Gb(file.getSize());
        Project projectToCheck = projectService.findById(projectId);
        Project project = setZeroIfStorageSizeNull(projectToCheck);

        BigInteger estimatedStorageSize = project.getStorageSize().
                add(BigInteger.valueOf(file.getSize()));
        BigInteger maxStorageSize = project.getMaxStorageSize();
        resourceValidator.validateMaxStorageSizeIsNotNull(maxStorageSize);
        resourceValidator.validateStorageSizeNotExceeded(maxStorageSize, estimatedStorageSize);

        String folder = projectId + project.getName();

        String key = amazonClient.uploadFile(file, folder);

        TeamMember fileCreator = teamMemberService.findById(teamMemberId);
        List<TeamRole> allowedRoles = teamMemberService.getTeamMemberRole(fileCreator.getId());
        project.setStorageSize(estimatedStorageSize);

        Resource resource = Resource.builder()
                .name(file.getOriginalFilename())
                .key(key)
                .size(BigInteger.valueOf(file.getSize()))
                .allowedRoles(allowedRoles)
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdBy(fileCreator)
                .updatedBy(fileCreator)
                .project(project)
                .build();

        projectService.updateProject(projectMapper.toDto(project));
        resourceService.save(resource);
        log.info("Saving new Resource with key: {}, with status: {}",
                resource.getKey(), resource.getStatus());
    }

    public InputStream downloadFile(Long resourceId) {
        log.info("Downloading file with ID: {}", resourceId);
        Resource resource = resourceService.findById(resourceId);
        String key = resource.getKey();
        log.info("Downloading file with key: {} from project with ID: {}", key, resource.getProject().getId());
        return amazonClient.downloadFile(key);
    }

    public Map<String, InputStream> downloadAllFiles(Long projectId) {
        log.info("Downloading all files from project with ID: {}", projectId);
        Project project = projectService.findByIdWithResources(projectId);

        Map<String, String> filesNamesWithKeys = new HashMap<>();
        project.getResources().stream()
                .filter(resource -> resource.getStatus().equals(ResourceStatus.ACTIVE))
                .forEach(resource -> filesNamesWithKeys.put(resource.getId() + resource.getName(), resource.getKey()));

        Map<String, S3ObjectInputStream> s3ObjectInputStreams = amazonClient.downloadAllFiles(filesNamesWithKeys);

        Map<String, InputStream> files = new HashMap<>();
        s3ObjectInputStreams.forEach((key, value) -> files.put(key, value.getDelegateStream()));
        log.info("Downloaded {} files from project with ID: {}", files.size(), projectId);
        return files;
    }


    public void deleteFile(Long resourceId, Long teamMemberId) {
        log.info("Deleting file from project with ID: {}", resourceId);
        Resource resource = resourceService.findById(resourceId);
        TeamMember teamMember = teamMemberService.findById(teamMemberId);
        Project project = projectService.findById(resource.getProject().getId());

        resourceValidator.validateAllowedToDeleteFile(resource, teamMember);

        String key = resource.getKey();
        amazonClient.deleteFile(key);

        BigInteger renewStorageSize = project.getStorageSize().subtract(resource.getSize());
        project.setStorageSize(renewStorageSize);

        resource.setKey(null);
        resource.setSize(null);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(teamMember);

        projectService.updateProject(projectMapper.toDto(project));
        resourceService.save(resource);
        log.info("Saving Resource with id: {} , with status: {} ",
                resource.getId(), resource.getStatus());
    }

    private Project setZeroIfStorageSizeNull(Project project) {
        log.info("Setting storage size to zero if null for project with ID: {}", project.getId());
        if (project.getStorageSize() == null) {
            project.setStorageSize(new BigInteger("0"));
        }
        log.info("Project with ID: {} updated with storage size: {}", project.getId(), project.getStorageSize());
        return project;
    }
}
