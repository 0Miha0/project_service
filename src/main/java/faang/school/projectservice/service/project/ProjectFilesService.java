package faang.school.projectservice.service.project;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.TeamMemberService;
import faang.school.projectservice.service.amazon_client.AmazonClientService;
import faang.school.projectservice.service.file_streaming.FileStreamingService;
import faang.school.projectservice.service.resource.ResourceService;
import faang.school.projectservice.validator.project.ProjectValidator;
import faang.school.projectservice.validator.resource.ResourceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectFilesService {

    @Value("${project-files.max-project-image-size}")
    private long maxProjectImageSize;

    private static final int MAX_IMAGE_GALLERY = 50;

    private final AmazonClientService amazonClient;
    private final ResourceService resourceService;
    private final ResourceValidator resourceValidator;
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final TeamMemberService teamMemberService;
    private final ProjectValidator projectValidator;
    private final FileStreamingService fileStreamingService;

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

    public void updateProjectCover(Long projectId, MultipartFile file) {
        projectValidator.nonvalizesLime(file.getSize(), maxProjectImageSize);
        Project project = projectService.findById(projectId);
        String folder = projectId + project.getName() + "/cover";
        String key = amazonClient.uploadFile(file, folder);
        project.setCoverImageId(key);
        projectService.save(project);
    }

    public StreamingResponseBody createProjectPresentation(Long projectId) {
        log.info("Creating project presentation for project ID: {}", projectId);
        Project project = projectService.findById(projectId);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PDDocument document = new PDDocument();
        PDPageContentStream contentStream = null;
        InputStream imageStream = null;

        try {
            PDPage page = new PDPage();
            document.addPage(page);
            contentStream = new PDPageContentStream(document, page);

            float margin = 50;
            float yStart = 750;
            float width = page.getMediaBox().getWidth() - 2 * margin;
            float currentY = yStart;

            log.info("Downloading cover image for project ID: {}", projectId);
            imageStream = amazonClient.downloadFile(project.getCoverImageId());
            byte[] imageBytes = imageStream.readAllBytes();
            PDImageXObject image = PDImageXObject.createFromByteArray(document, imageBytes, "project-cover-image");
            float imageWidth = 150;
            float imageHeight = 150;
            contentStream.drawImage(image, margin, currentY - imageHeight, imageWidth, imageHeight);

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(margin + imageWidth + 20, currentY - 20);
            contentStream.showText("Title: " + project.getName());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("The date of creation: " + project.getCreatedAt());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Status: " + project.getStatus());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Owner: " + teamMemberService.findById(project.getOwnerId()).getNickname());
            contentStream.endText();

            currentY -= imageHeight + 30;

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(margin, currentY);
            contentStream.showText("Description: " + project.getDescription());
            contentStream.endText();
            currentY -= 40;

            currentY = drawTable(contentStream, document, currentY, "Completed tasks", new String[]{"Task", "Data"},
                    project.getTasks().stream()
                            .filter(t -> t.getStatus().equals(TaskStatus.DONE))
                            .map(t -> new String[]{t.getName(), t.getUpdatedAt().toString()}).toList());

            currentY -= 50;
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(margin, currentY);
            contentStream.showText("Project statistics:");
            contentStream.endText();
            currentY -= 20;

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(margin, currentY);
            contentStream.showText("The number of tasks performed: " + project.getTasks().size());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("The number of participants: " + project.getTeams().size());
            contentStream.endText();

            contentStream.close();
            document.save(byteArrayOutputStream);
            log.info("Project presentation created successfully for project ID: {}", projectId);
        } catch (IOException e) {
            log.error("Error creating project presentation for project ID: {}", projectId, e);
            throw new RuntimeException("Failed to create project presentation", e);
        } finally {
            try {
                if (contentStream != null) {
                    contentStream.close();
                }
                if (document != null) {
                    document.close();
                }
                if (imageStream != null) {
                    imageStream.close();
                }
            } catch (IOException e) {
                log.error("Error closing resources for project ID: {}", projectId, e);
            }
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return fileStreamingService.getStreamingResponseBody(inputStream);
    }

    public void addImageToProjectGallery(Long projectId, MultipartFile file) {
        log.info("Adding image to project gallery for project ID: {}", projectId);
        projectValidator.nonvalizesLime(file.getSize(), maxProjectImageSize);
        Project project = projectService.findById(projectId);
        projectValidator.gallerySizeValidate(project.getGalleryFileKeys().size(), MAX_IMAGE_GALLERY);
        String folder = projectId + project.getName() + "/gallery";
        String key = amazonClient.uploadFile(file, folder);
        List<String> galleryFileKeys = project.getGalleryFileKeys();
        galleryFileKeys.add(key);
        project.setGalleryFileKeys(galleryFileKeys);
        projectService.save(project);
        log.info("Image added to project gallery for project ID: {}", projectId);
    }

    public void removeImageFromProjectGallery(Long projectId, String fileKey) {
        log.info("Removing image from project gallery for project ID: {}", projectId);
        Project project = projectService.findById(projectId);
        List<String> galleryFileKeys = project.getGalleryFileKeys();
        galleryFileKeys.removeIf(key -> key.equals(fileKey));
        amazonClient.deleteFile(fileKey);
        project.setGalleryFileKeys(galleryFileKeys);
        projectService.save(project);
        log.info("Image removed from project gallery for project ID: {}", projectId);
    }

    private float drawTable(PDPageContentStream contentStream, PDDocument document, float yStart, String title, String[] headers, List<String[]> rows) throws IOException {
        log.info("Drawing table for project ID: {}", title);
        float margin = 50;
        float tableWidth = document.getPage(0).getMediaBox().getWidth() - 2 * margin;
        float rowHeight = 20;
        float yPosition = yStart - 30;

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText(title);
        contentStream.endText();
        yPosition -= 20;

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        float[] columnWidths = {tableWidth / headers.length, tableWidth / headers.length, tableWidth / headers.length};

        for (int i = 0; i < headers.length; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(margin + (i * columnWidths[i]), yPosition);
            contentStream.showText(headers[i]);
            contentStream.endText();
        }
        yPosition -= rowHeight;

        contentStream.setFont(PDType1Font.HELVETICA, 12);
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + (i * columnWidths[i]), yPosition);
                contentStream.showText(row[i]);
                contentStream.endText();
            }
            yPosition -= rowHeight;
        }
        log.info("Table drawn successfully for project ID: {}", title);
        return yPosition;
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
