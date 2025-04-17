package faang.school.projectservice.service.project;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.mapper.project.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.amazonclient.AmazonClientService;
import faang.school.projectservice.service.resource.ResourceService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import faang.school.projectservice.validator.resource.ResourceValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectFilesServiceTest {

    @Mock
    private AmazonClientService amazonClientService;

    @Mock
    private ResourceService resourceService;

    @Mock
    private ResourceValidator resourceValidator;

    @Mock
    private ProjectService projectService;

    @Spy
    private ProjectMapper projectMapper = new ProjectMapperImpl();

    @Mock
    private TeamMemberService teamMemberService;

    @InjectMocks
    ProjectFilesService projectFilesService;

    @Test
    public void uploadFileTest() {
        long teamMemberId = 1;
        long projectId = 1;
        String folder = "1name";
        String key = "key";
        BigInteger maxStorageSize = new BigInteger("1000");
        BigInteger currentStorageSize = new BigInteger("0");
        MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                "text/plain", "This is a test".getBytes());
        Project project = Project.builder()
                .id(projectId)
                .name("name")
                .storageSize(null)
                .maxStorageSize(maxStorageSize)
                .description("description")
                .build();
        Project savingProject = Project.builder()
                .id(projectId)
                .name("name")
                .storageSize(currentStorageSize.add(BigInteger.valueOf(file.getSize())))
                .maxStorageSize(maxStorageSize)
                .description("description")
                .build();
        TeamMember teamMember = TeamMember.builder()
                .roles(new ArrayList<>())
                .build();
        Resource updatedResource = Resource.builder()
                .name(file.getOriginalFilename())
                .key(key)
                .size(BigInteger.valueOf(file.getSize()))
                .allowedRoles(new ArrayList<>())
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .project(savingProject)
                .build();

        doNothing().when(resourceValidator).validateFileSizeNotBigger2Gb(file.getSize());
        when(projectService.getProjectById(projectId)).thenReturn(project);
        doNothing().when(resourceValidator).
                validateMaxStorageSizeIsNotNull(maxStorageSize);
        doNothing().when(resourceValidator).validateStorageSizeNotExceeded(maxStorageSize,
                currentStorageSize.add(BigInteger.valueOf(file.getSize())));
        when(teamMemberService.findById(teamMemberId)).thenReturn(teamMember);
        when(amazonClientService.uploadFile(file, folder)).thenReturn(key);

        projectFilesService.uploadFile(projectId, teamMemberId, file);

        verify(resourceValidator).validateFileSizeNotBigger2Gb(file.getSize());
        verify(projectService, times(1)).getProjectById(projectId);
        verify(resourceValidator, times(1)).
                validateMaxStorageSizeIsNotNull(maxStorageSize);
        verify(resourceValidator, times(1)).validateStorageSizeNotExceeded(
                maxStorageSize, currentStorageSize.add(BigInteger.valueOf(file.getSize())));
        verify(teamMemberService, times(1)).findById(teamMemberId);
        verify(amazonClientService, times(1)).uploadFile(file, folder);
        verify(projectService, times(1)).
                updateProject(projectMapper.toDto(savingProject));
        verify(resourceService, times(1)).save(updatedResource);
    }

    @Test
    public void downloadFileTest() throws IOException {
        long resourceId = 1L;
        String key = "key";
        String mockFileContent = "This is a test";
        Resource resource = Resource.builder()
                .key(key)
                .id(resourceId)
                .build();
        ByteArrayInputStream mockInputStream = new ByteArrayInputStream(mockFileContent.getBytes());
        S3ObjectInputStream s3ObjectInputStream = new S3ObjectInputStream(mockInputStream, null);

        when(resourceService.getResource(resourceId)).thenReturn(resource);
        when(amazonClientService.downloadFile(key)).thenReturn(s3ObjectInputStream);

        InputStream result = projectFilesService.downloadFile(resourceId);

        verify(resourceService, times(1)).getResource(resourceId);
        verify(amazonClientService, times(1)).downloadFile(key);

        String resultContent = new String(result.readAllBytes());
        assertNotNull(result);
        assertEquals(mockFileContent, resultContent);
    }

    @Test
    public void downloadAllFilesTest() throws IOException {
        long projectId = 1L;
        long resourceId1 = 1L;
        long resourceId2 = 1L;
        long resourceId3 = 1L;
        String name1 = "file1.txt";
        String name2 = "file2.txt";
        String name3 = "file3.txt";
        String key1 = "key1";
        String key2 = "key2";
        String key3 = "key3";
        String content1 = "content1";
        String content3 = "content3";

        Resource resource1 = Resource.builder()
                .id(resourceId1)
                .name(name1)
                .key(key1)
                .status(ResourceStatus.ACTIVE)
                .build();
        Resource resource2 = Resource.builder()
                .id(resourceId2)
                .name(name2)
                .key(key2)
                .status(ResourceStatus.INACTIVE)
                .build();
        Resource resource3 = Resource.builder()
                .id(resourceId3)
                .name(name3)
                .key(key3)
                .status(ResourceStatus.ACTIVE)
                .build();
        Project project = Project.builder()
                .id(projectId)
                .resources(List.of(resource1, resource2, resource3))
                .build();

        Map<String, String> filesNamesWithKeys = new HashMap<>();
        filesNamesWithKeys.put(resourceId1 + name1, key1);
        filesNamesWithKeys.put(resourceId3 + name3, key3);

        ByteArrayInputStream mockInputStream1 = new ByteArrayInputStream(content1.getBytes());
        S3ObjectInputStream s3ObjectInputStream1 = new S3ObjectInputStream(mockInputStream1, null);
        ByteArrayInputStream mockInputStream3 = new ByteArrayInputStream(content3.getBytes());
        S3ObjectInputStream s3ObjectInputStream3 = new S3ObjectInputStream(mockInputStream3, null);

        Map<String, S3ObjectInputStream> namesWithS3ObjectInputStreams = new HashMap<>();
        namesWithS3ObjectInputStreams.put(name1, s3ObjectInputStream1);
        namesWithS3ObjectInputStreams.put(name3, s3ObjectInputStream3);

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(amazonClientService.downloadAllFiles(filesNamesWithKeys)).thenReturn(namesWithS3ObjectInputStreams);

        Map<String, InputStream> result = projectFilesService.downloadAllFiles(projectId);

        String resultContent1 = new String(result.get(name1).readAllBytes());
        String resultContent3 = new String(result.get(name3).readAllBytes());

        verify(projectService, times(1)).getProjectById(projectId);
        verify(amazonClientService, times(1)).downloadAllFiles(filesNamesWithKeys);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey(name1));
        assertTrue(result.containsKey(name3));
        assertEquals(content1, resultContent1);
        assertEquals(content3, resultContent3);
    }

    @Test
    public void deleteFileTest() {
        long resourceId = 1L;
        long teamMemberId = 1L;
        long projectId = 1L;
        String key = "key";
        BigInteger storageSize = new BigInteger("100");
        BigInteger resourceSize = new BigInteger("10");
        TeamMember teamMember = TeamMember.builder()
                .id(teamMemberId)
                .build();
        Project project = Project.builder()
                .id(projectId)
                .storageSize(storageSize)
                .build();
        Project updatedProject = Project.builder()
                .id(projectId)
                .storageSize(storageSize.subtract(resourceSize))
                .build();
        Resource resource = Resource.builder()
                .id(resourceId)
                .size(resourceSize)
                .key(key)
                .status(ResourceStatus.ACTIVE)
                .project(project)
                .build();
        Resource updatedResource = Resource.builder()
                .id(resourceId)
                .key(null)
                .size(null)
                .status(ResourceStatus.DELETED)
                .updatedBy(teamMember)
                .project(project)
                .build();

        when(resourceService.getResource(resourceId)).thenReturn(resource);
        when(teamMemberService.findById(teamMemberId)).thenReturn(teamMember);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        doNothing().when(resourceValidator).validateAllowedToDeleteFile(resource, teamMember);

        projectFilesService.deleteFile(resourceId, teamMemberId);

        verify(amazonClientService, times(1)).deleteFile(key);
        verify(projectService, times(1)).updateProject(projectMapper.toDto(updatedProject));
        verify(resourceService, times(1)).save(updatedResource);
    }
}