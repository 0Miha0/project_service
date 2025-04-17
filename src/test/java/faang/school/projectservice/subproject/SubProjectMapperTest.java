package faang.school.projectservice.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.mapper.subproject.SubProjectMapper;
import faang.school.projectservice.mapper.subproject.SubProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubProjectMapperTest {

    private final SubProjectMapper mapper = new SubProjectMapperImpl();

    @Test
    @DisplayName("Test creation dto to entity mapping")
    public void toEntityTest() {
        CreateSubProjectDto dto = CreateSubProjectDto.builder()
                .name("cool name")
                .description("cool description")
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        Project result = mapper.toEntity(dto);

        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getVisibility(), result.getVisibility());
    }

    @Test
    @DisplayName("Test entity to dto mapping")
    public void toDtoTest() {
        Project project = Project.builder()
                .id(1L)
                .ownerId(2L)
                .parentProject(Project.builder().id(2L).build())
                .name("cool name")
                .description("cool description")
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        ProjectDto result = mapper.toDto(project);

        assertEquals(project.getId(), result.getId());
        assertEquals(project.getOwnerId(), result.getOwnerId());
        assertEquals(project.getParentProject().getId(), result.getParentId());
        assertEquals(project.getName(), result.getName());
        assertEquals(project.getDescription(), result.getDescription());
        assertEquals(project.getStatus(), result.getStatus());
        assertEquals(project.getVisibility(), result.getVisibility());
    }

    @Test
    @DisplayName("Test partial update")
    public void partialUpdateTest() {
        Project project = Project.builder()
                .id(1L)
                .ownerId(2L)
                .parentProject(Project.builder().id(2L).build())
                .name("cool name")
                .description("cool description")
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        ProjectDto dto = ProjectDto.builder()
                .id(20L)
                .ownerId(150L)
                .name("new cool name")
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        mapper.partialUpdate(project, dto);

        assertNotEquals(project.getId(), dto.getId());
        assertNotEquals(project.getOwnerId(), dto.getOwnerId());
        assertNotNull(project.getParentProject());
        assertEquals(project.getName(), dto.getName());
        assertNotNull(project.getDescription());
        assertEquals(project.getStatus(), dto.getStatus());
        assertNotNull(project.getVisibility());
    }

    @Test
    @DisplayName("Test to dto list mapping")
    public void toDtoListTest() {
        Project project = Project.builder()
                .id(1L)
                .ownerId(2L)
                .parentProject(Project.builder().id(2L).build())
                .name("cool name")
                .description("cool description")
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        List<Project> projects = List.of(project);

        List<ProjectDto> result = mapper.toDtoList(projects);

        assertEquals(projects.size(), result.size());
        assertEquals(projects.get(0).getId(), result.get(0).getId());
    }
}
