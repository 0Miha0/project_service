package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.filter.moment.MomentDateFilter;
import faang.school.projectservice.filter.moment.MomentProjectsFilter;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.mapper.moment.MomentMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {

    @InjectMocks
    private MomentService momentService;

    @Mock
    private MomentRepository momentRepository;

    @Spy
    private MomentMapper momentMapper = new MomentMapperImpl();

    @Mock
    private ProjectService projectService;

    @Test
    void createMomentNameNullTest() {
        MomentDto momentDto = new MomentDto();
        Moment moment = new Moment();
        when(momentMapper.toEntity(momentDto)).thenReturn(moment);
        assertThrows(IllegalArgumentException.class, () -> momentService.createMoment(momentDto));
    }

    @Test
    void createMomentProjectStatusDoesNotMatchTest() {
        MomentDto momentDto = new MomentDto();
        Moment moment = new Moment();

        Project project = new Project();
        project.setStatus(ProjectStatus.CANCELLED);
        List<Project> projects = List.of(project);
        moment.setProjects(projects);

        when(momentMapper.toEntity(momentDto)).thenReturn(moment);
        assertThrows(IllegalArgumentException.class, () -> momentService.createMoment(momentDto));
    }

    @Test
    void createMomentTest() {
        Moment moment = new Moment();
        moment.setName("Test");

        MomentDto momentDto = new MomentDto();
        momentDto.setName("Test");
        momentDto.setProjectIds(List.of(1L));

        Project project = new Project();
        project.setId(1L);
        project.setStatus(ProjectStatus.IN_PROGRESS);
        moment.setProjects(List.of(project));

        when(projectService.findProjectsByIds(momentDto.getProjectIds())).thenReturn(List.of(project));
        when(momentRepository.save(moment)).thenReturn(moment);

        MomentDto result = momentService.createMoment(momentDto);

        verify(momentMapper, times(1)).toEntity(momentDto);
        verify(momentMapper, times(1)).toDto(moment);
        verify(momentRepository, times(1)).save(moment);

        assertEquals(momentDto.getName(), result.getName());
        assertEquals(momentDto.getProjectIds(), result.getProjectIds());
        assertTrue(result.getProjectIds().containsAll(List.of(1L)));
    }

    @Test
    void updateMomentEntityNotFoundTest() {
        Long momentId = 1L;
        when(momentRepository.findById(momentId)).thenReturn(Optional.empty());
        MomentDto momentDto = new MomentDto();
        assertThrows(EntityNotFoundException.class, () -> momentService.updateMoment(momentDto, 1L));
    }

    @Test
    void updateMomentTest() {
        Long momentId = 1L;

        MomentDto updatedMomentDto = new MomentDto();
        updatedMomentDto.setName("Updated Name");
        updatedMomentDto.setProjectIds(List.of(1L, 2L));

        Moment momentToUpdate = new Moment();
        momentToUpdate.setId(momentId);
        momentToUpdate.setName("Old Name");

        Project activeProject = new Project();
        activeProject.setStatus(ProjectStatus.IN_PROGRESS);

        momentToUpdate.setProjects(List.of(activeProject));

        when(momentRepository.findById(momentId)).thenReturn(Optional.of(momentToUpdate));
        when(projectService.findProjectsByIds(updatedMomentDto.getProjectIds()))
                .thenReturn(List.of(activeProject, new Project()));

        when(momentRepository.save(momentToUpdate)).thenReturn(momentToUpdate);
        momentService.updateMoment(updatedMomentDto, momentId);

        Moment savedMoment = momentRepository.findById(momentId).orElse(null);
        assertNotNull(savedMoment, "Saved moment should not be null");
        assertEquals(momentId, savedMoment.getId());

        verify(momentMapper, times(1)).updateEntity(savedMoment, updatedMomentDto);
        verify(momentRepository, times(1)).save(savedMoment);
    }

    @Test
    void getMomentsByFilter() {
        Moment moment1 = new Moment();
        Moment moment2 = new Moment();
        moment1.setProjects(List.of(new Project()));
        moment2.setProjects(List.of(new Project()));
        List<Moment> moments = Arrays.asList(moment1, moment2);
        when(momentRepository.findAll()).thenReturn(moments);

        Filter<Moment, MomentFilterDto> dateFilter = Mockito.mock(MomentDateFilter.class);
        Filter<Moment, MomentFilterDto> projectsFilter = Mockito.mock(MomentProjectsFilter.class);
        List<Filter<Moment, MomentFilterDto>> filters = List.of(dateFilter, projectsFilter);

        momentService = new MomentService(momentRepository, momentMapper, projectService, filters);

        MomentFilterDto momentFilterDto = new MomentFilterDto();
        when(filters.get(0).isApplicable(eq(momentFilterDto))).thenReturn(true);
        when(filters.get(0).apply(any(), eq(momentFilterDto))).thenReturn(moments.stream());

        momentService.getMomentsByFilter(momentFilterDto);
        verify(momentMapper, times(moments.size())).toDto(any(Moment.class));
    }

    @Test
    void getAllMomentsTest() {
        Moment moment = new Moment();
        moment.setProjects(List.of(new Project()));
        List<Moment> moments = List.of(moment);
        when(momentRepository.findAll()).thenReturn(moments);
        momentService.getAllMoments();
        verify(momentRepository, times(1)).findAll();
        verify(momentMapper, times(1)).toDto(any(Moment.class));
    }

    @Test
    void getMomentByIdNotFoundTest() {
        Long momentId = 1L;
        when(momentRepository.findById(momentId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> momentService.getMomentById(1L));
    }

    @Test
    void getMomentByIdTest() {
        Long momentId = 1L;
        Moment moment = new Moment();
        moment.setProjects(List.of(new Project()));
        when(momentRepository.findById(momentId)).thenReturn(Optional.of(moment));

        momentService.getMomentById(momentId);

        verify(momentRepository, times(1)).findById(momentId);
        verify(momentMapper, times(1)).toDto(moment);
    }

}
