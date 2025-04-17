package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipCreationDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.dto.internship.InternshipUpdateRequestDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.filter.internship.InternshipStatusFilter;
import faang.school.projectservice.filter.internship.InternshipTeamRoleFilter;
import faang.school.projectservice.mapper.intership.InternshipMapperImpl;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.team.TeamService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import faang.school.projectservice.validator.internship.InternshipValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {

    private static final int MAX_INTERNSHIP_MONTHS_DURATION = 3;
    private static final String INTERNSHIP = "Internship";

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private InternshipValidator validator;

    @Spy
    private InternshipMapperImpl internshipMapper;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private TeamService teamService;

    @Mock
    private ProjectService projectService;

    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<Team> teamCaptor;

    private InternshipService internshipService;

    @BeforeEach
    void setUp() {
        Filter<Internship, InternshipFilterDto> statusFilter = Mockito.spy(InternshipStatusFilter.class);
        Filter<Internship, InternshipFilterDto> roleFilter = Mockito.spy(InternshipTeamRoleFilter.class);
        List<Filter<Internship, InternshipFilterDto> > filters = List.of(statusFilter, roleFilter);

        internshipService = new InternshipService(
                internshipRepository, validator, internshipMapper, teamMemberService,
                teamService, projectService, restTemplate, filters
        );
    }

    @Test
    void createInternshipValidTest() {
        List<Long> internUserIds = new ArrayList<>(List.of(1L, 2L, 3L, 4L));
        String internshipName = "Internship Spring 2025";
        String internshipDescription = "Some description";
        long mentorUserId = 8L;
        long creatorUserId = 9L;
        long projectId = 10L;
        LocalDateTime startDate = LocalDateTime.now().plusMonths(1);
        LocalDateTime endDate = LocalDateTime.now().plusMonths(1 + MAX_INTERNSHIP_MONTHS_DURATION).minusDays(1);

        InternshipCreationDto creationDto = InternshipCreationDto.builder()
                .internUserIds(internUserIds)
                .name(internshipName)
                .description(internshipDescription)
                .mentorUserId(mentorUserId)
                .creatorUserId(creatorUserId)
                .projectId(projectId)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        when(restTemplate.exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        )).thenReturn(ResponseEntity.ok(List.of()));

        TeamMember mentor = new TeamMember();
        mentor.setUserId(mentorUserId);
        mentor.setRoles(List.of(TeamRole.ANALYST, TeamRole.MANAGER));
        when(teamMemberService.findByUserIdAndProjectId(mentorUserId, projectId)).thenReturn(mentor);

        Project project = new Project();
        project.setId(10L);
        Mockito.lenient().when(projectService.getProjectById(projectId)).thenReturn(project);

        when(teamMemberService.save(any(TeamMember.class))).thenAnswer(invocation -> {
            TeamMember teamMember = invocation.getArgument(0);
            TeamMember savedTeamMember = new TeamMember();
            savedTeamMember.setUserId(teamMember.getUserId());
            savedTeamMember.setRoles(List.of(TeamRole.INTERN));
            return savedTeamMember;
        });

        when(internshipRepository.save(any(Internship.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InternshipDto savedInternshipDto = assertDoesNotThrow(() -> internshipService.createInternship(creationDto));

        assertEquals(internUserIds, savedInternshipDto.getInternUserIds());
        assertEquals(internshipName, savedInternshipDto.getName());
        assertEquals(internshipDescription, savedInternshipDto.getDescription());
        assertEquals(mentorUserId, savedInternshipDto.getMentorUserId());
        assertEquals(creatorUserId, savedInternshipDto.getCreatorUserId());
        assertEquals(projectId, savedInternshipDto.getProjectId());
        assertEquals(startDate, savedInternshipDto.getStartDate());
        assertEquals(endDate, savedInternshipDto.getEndDate());
        assertEquals(InternshipStatus.NOT_STARTED, savedInternshipDto.getStatus());
        verify(restTemplate, times(1)).exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        );
        verify(teamMemberService, times(1)).findByUserIdAndProjectId(mentorUserId, projectId);
        verify(projectService, times(1)).getProjectById(projectId);
        verify(teamService, times(1)).save(any(Team.class));
        verify(internshipRepository, times(1)).save(any(Internship.class));
    }

    @Test
    void updateInternshipInternshipInProgressTest() {
        updateInternshipValidTest(false);
    }

    @Test
    void updateInternshipInternshipCompletedTest() {
        updateInternshipValidTest(true);
    }

    @Test
    void updateNotExistingInternshipTest() {
        long internshipId = 1L;
        InternshipUpdateDto updateDto = new InternshipUpdateDto();
        when(internshipRepository.findById(internshipId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> internshipService.updateInternship(internshipId, updateDto));
        assertEquals("Entity %s with ID %s not found".formatted(INTERNSHIP, internshipId), exception.getMessage());
    }

    @Test
    void getFilteredInternshipsEmptyFilterTest() {
        InternshipFilterDto filterDto = InternshipFilterDto.builder()
                .internshipStatus(null)
                .teamRole(null)
                .build();

        List<Internship> internships = List.of(
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.ANALYST),
                createInternshipWithStatusAndMentorRole(InternshipStatus.IN_PROGRESS, TeamRole.DESIGNER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.DEVELOPER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.NOT_STARTED, TeamRole.MANAGER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.TESTER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.NOT_STARTED, TeamRole.ANALYST)
        );
        when(internshipRepository.findAll()).thenReturn(internships);

        List<InternshipDto> filteredInternshipDtos = assertDoesNotThrow(() -> internshipService.getFilteredInternships(filterDto));

        assertEquals(6, filteredInternshipDtos.size());
    }

    @Test
    void getFilteredInternshipsStatusFilterTest() {
        InternshipFilterDto filterDto = InternshipFilterDto.builder()
                .internshipStatus(InternshipStatus.COMPLETED)
                .teamRole(null)
                .build();

        List<Internship> internships = List.of(
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.ANALYST),
                createInternshipWithStatusAndMentorRole(InternshipStatus.IN_PROGRESS, TeamRole.DESIGNER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.DEVELOPER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.NOT_STARTED, TeamRole.MANAGER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.TESTER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.NOT_STARTED, TeamRole.ANALYST)
        );
        when(internshipRepository.findAll()).thenReturn(internships);

        List<InternshipDto> filteredInternshipDtos = assertDoesNotThrow(() -> internshipService.getFilteredInternships(filterDto));

        assertEquals(3, filteredInternshipDtos.size());
    }

    @Test
    void getFilteredInternshipsTeamRoleFilterTest() {
        InternshipFilterDto filterDto = InternshipFilterDto.builder()
                .internshipStatus(null)
                .teamRole(TeamRole.ANALYST)
                .build();

        List<Internship> internships = List.of(
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.ANALYST),
                createInternshipWithStatusAndMentorRole(InternshipStatus.IN_PROGRESS, TeamRole.DESIGNER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.DEVELOPER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.NOT_STARTED, TeamRole.MANAGER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.TESTER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.NOT_STARTED, TeamRole.ANALYST)
        );
        when(internshipRepository.findAll()).thenReturn(internships);

        List<InternshipDto> filteredInternshipDtos = assertDoesNotThrow(() -> internshipService.getFilteredInternships(filterDto));

        assertEquals(2, filteredInternshipDtos.size());
    }

    @Test
    void getFilteredInternshipsCombinedFilterTest() {
        InternshipFilterDto filterDto = InternshipFilterDto.builder()
                .internshipStatus(InternshipStatus.COMPLETED)
                .teamRole(TeamRole.ANALYST)
                .build();

        List<Internship> internships = List.of(
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.ANALYST),
                createInternshipWithStatusAndMentorRole(InternshipStatus.IN_PROGRESS, TeamRole.DESIGNER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.DEVELOPER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.NOT_STARTED, TeamRole.MANAGER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.TESTER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.NOT_STARTED, TeamRole.ANALYST)
        );
        when(internshipRepository.findAll()).thenReturn(internships);

        List<InternshipDto> filteredInternshipDtos = assertDoesNotThrow(() -> internshipService.getFilteredInternships(filterDto));

        assertEquals(1, filteredInternshipDtos.size());
    }

    @Test
    void getAllInternshipsValidTest() {
        List<Internship> internships = List.of(
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.ANALYST),
                createInternshipWithStatusAndMentorRole(InternshipStatus.IN_PROGRESS, TeamRole.DESIGNER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.DEVELOPER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.NOT_STARTED, TeamRole.MANAGER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.COMPLETED, TeamRole.TESTER),
                createInternshipWithStatusAndMentorRole(InternshipStatus.NOT_STARTED, TeamRole.ANALYST)
        );
        when(internshipRepository.findAll()).thenReturn(internships);

        List<InternshipDto> internshipDtos = assertDoesNotThrow(() -> internshipService.getAllInternships());

        assertEquals(internships.size(), internshipDtos.size());
        verify(internshipRepository, times(1)).findAll();
        verify(internshipMapper, times(1)).toDto(internships);
    }

    @Test
    void getInternshipByIdValidTest() {
        Long internshipId = 5L;
        InternshipStatus status = InternshipStatus.IN_PROGRESS;
        Internship internship = new Internship();
        internship.setId(internshipId);
        internship.setInterns(Collections.emptyList());
        internship.setStatus(status);

        when(internshipRepository.findById(internshipId)).thenReturn(Optional.of(internship));

        InternshipDto requestDto = assertDoesNotThrow(() -> internshipService.getInternshipById(internshipId));

        assertEquals(internshipId, requestDto.getId());
        assertEquals(status, requestDto.getStatus());
        verify(internshipRepository, times(1)).findById(internshipId);
        verify(internshipMapper, times(1)).toDto(internship);
    }

    @Test
    void getInternshipByIdNotExistingInternshipTest() {
        Long internshipId = 5L;
        when(internshipRepository.findById(internshipId)).thenReturn(Optional.empty());

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> internshipService.getInternshipById(internshipId));

        verify(internshipRepository, times(1)).findById(internshipId);
        assertEquals("Entity %s with ID %s not found".formatted(INTERNSHIP, internshipId), exception.getMessage());
    }

    @Test
    void removeInternsFromInternshipValidTest() {
        Long firstInternUserId = 1L;
        TeamMember firstIntern = new TeamMember();
        firstIntern.setUserId(firstInternUserId);

        Long secondInternUserId = 2L;
        TeamMember secondIntern = new TeamMember();
        secondIntern.setUserId(secondInternUserId);

        List<TeamMember> interns = new ArrayList<>(List.of(firstIntern, secondIntern));
        List<Long> internUserIdsToRemove = List.of(2L);
        int expectedInternsSizeAfterRemoval = 1;

        long internshipId = 10L;
        Internship internship = new Internship();
        internship.setId(internshipId);
        internship.setInterns(interns);
        when(internshipRepository.findById(internshipId)).thenReturn(Optional.of(internship));

        assertDoesNotThrow(() -> internshipService.removeInternsFromInternship(internshipId, internUserIdsToRemove));

        assertEquals(expectedInternsSizeAfterRemoval, internship.getInterns().size());
        assertTrue(internship.getInterns().contains(firstIntern));
        assertFalse(internship.getInterns().contains(secondIntern));
        verify(internshipRepository, times(1)).save(internship);
        verify(teamMemberService, times(1)).deleteAll(List.of(secondIntern));
    }

    @Test
    void removeInternsFromNotExistingInternshipTest() {
        Long internshipId = 5L;
        when(internshipRepository.findById(internshipId)).thenReturn(Optional.empty());

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> internshipService.removeInternsFromInternship(internshipId, List.of()));

        verify(internshipRepository, times(1)).findById(internshipId);
        assertEquals("Entity %s with ID %s not found".formatted(INTERNSHIP, internshipId), exception.getMessage());
    }

    private void updateInternshipValidTest(boolean isAfterEndDate) {
        long internshipId = 10L;
        TeamRole internNewRole = TeamRole.ANALYST;

        Long firstInternUserId = 1L;
        TeamMember firstIntern = new TeamMember();
        firstIntern.setUserId(firstInternUserId);
        firstIntern.setRoles(new ArrayList<>(List.of(TeamRole.INTERN)));

        Long secondInternUserId = 2L;
        TeamMember secondIntern = new TeamMember();
        secondIntern.setUserId(secondInternUserId);
        secondIntern.setRoles(new ArrayList<>(List.of(TeamRole.INTERN)));

        List<Task> tasks = List.of(
                Task.builder().performerUserId(firstInternUserId).status(TaskStatus.DONE).build(),
                Task.builder().performerUserId(firstInternUserId).status(TaskStatus.DONE).build(),
                Task.builder().performerUserId(secondInternUserId).status(TaskStatus.IN_PROGRESS).build(),
                Task.builder().performerUserId(secondInternUserId).status(TaskStatus.DONE).build()
        );

        InternshipStatus expectedInternshipStatus = isAfterEndDate ? InternshipStatus.COMPLETED : InternshipStatus.IN_PROGRESS;
        int expectedTeamSize = isAfterEndDate ? 1 : 2;
        List<Long> expectedIncompleteInternUserIds = List.of(secondInternUserId);
        List<Long> expectedCompletedInternUserIds = List.of(firstInternUserId);
        int expectedTeamMemberServiceCallTimes = isAfterEndDate ? 1 : 0;

        long projectId = 9L;
        Project project = new Project();
        project.setId(projectId);
        project.setTasks(tasks);

        List<TeamMember> interns = new ArrayList<>(List.of(firstIntern, secondIntern));

        Internship internship = new Internship();
        internship.setId(internshipId);
        internship.setProject(project);
        internship.setStatus(InternshipStatus.NOT_STARTED);
        internship.setStartDate(LocalDateTime.now().minusMonths(2));
        internship.setInterns(interns);
        internship.setEndDate(isAfterEndDate ? LocalDateTime.now().minusDays(1) : LocalDateTime.now().plusDays(4));

        InternshipUpdateDto updateDto = InternshipUpdateDto.builder()
                .internNewTeamRole(internNewRole)
                .build();

        when(internshipRepository.findById(internshipId)).thenReturn(Optional.of(internship));

        InternshipUpdateRequestDto requestDto = assertDoesNotThrow(() -> internshipService.updateInternship(internshipId, updateDto));

        assertEquals(internshipId, requestDto.getId());
        assertEquals(expectedCompletedInternUserIds, requestDto.getCompletedInternUserIds());
        assertEquals(expectedIncompleteInternUserIds, requestDto.getIncompleteInternUserIds());
        assertEquals(internNewRole, requestDto.getInternNewTeamRole());
        assertEquals(expectedInternshipStatus, requestDto.getInternshipStatus());
        assertEquals(expectedTeamSize, internship.getInterns().size());
        assertTrue(firstIntern.getRoles().contains(updateDto.getInternNewTeamRole()));
        verify(internshipRepository, times(1)).save(internship);
        verify(teamMemberService, times(1)).saveAll(anyList());
        verify(teamMemberService, times(expectedTeamMemberServiceCallTimes)).deleteAll(anyList());
    }

    private Internship createInternshipWithStatusAndMentorRole(InternshipStatus internshipStatus, TeamRole mentorTeamRole) {
        TeamMember mentor = new TeamMember();
        mentor.setRoles(List.of(mentorTeamRole));

        Internship internship = new Internship();
        internship.setMentor(mentor);
        internship.setStatus(internshipStatus);
        internship.setInterns(Collections.emptyList());
        return internship;
    }
}