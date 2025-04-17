package faang.school.projectservice.service.teammember;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.dto.teammember.TeamMemberFilterDto;
import faang.school.projectservice.dto.teammember.TeamMemberUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.teammember.TeamMemberFilter;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.team_member.TeamMemberMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.team.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceTest {

    private static final String TEAM_MEMBER = "TeamMember";

    @Mock
    private TeamMemberJpaRepository teamMemberRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private TeamService teamService;

    @Spy
    private TeamMemberMapperImpl teamMemberMapper;

    @Mock
    private TeamMemberFilter teamMemberFilter;

    @Mock
    private List<TeamMemberFilter> teamMemberFilters;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private TeamMemberService teamMemberService;

    List<TeamMember> memberList1;
    List<TeamMember> memberList2;

    @BeforeEach
    public void setUp() {

        memberList1 = List.of(
                TeamMember.builder().id(2L).userId(2L).build(),
                TeamMember.builder().id(3L).userId(3L).build(),
                TeamMember.builder().id(4L).userId(4L).build()
        );

        memberList2 = List.of(
                TeamMember.builder().id(4L).userId(5L).build(),
                TeamMember.builder().id(5L).userId(6L).build(),
                TeamMember.builder().id(6L).userId(7L).build()
        );
    }

    @Test
    @DisplayName("Successfully add a new team member")
    public void successfullyAddNewMemberToTheTeamTest() {
        Long newUserId = 10L;

        Project project = Project.builder()
                .id(1L)
                .ownerId(1L)
                .build();

        List<Team> teams = List.of(
                Team.builder()
                        .id(1L)
                        .teamMembers(memberList1)
                        .project(project)
                        .build(),
                Team.builder()
                        .id(2L)
                        .teamMembers(memberList2)
                        .project(project)
                        .build()
        );

        project.setTeams(teams);

        TeamMember currentUser = TeamMember.builder()
                .id(1L)
                .userId(1L)
                .build();

        TeamMember savedMember = TeamMember.builder()
                .userId(newUserId)
                .build();

        TeamMemberDto teamMemberDto = TeamMemberDto.builder()
                .userId(newUserId)
                .team(1L)
                .role(List.of("MANAGER"))
                .build();

        when(projectService.getProjectById(project.getId()))
                .thenReturn(project);
        when(userContext.getUserId())
                .thenReturn(currentUser.getUserId());
        when(teamMemberRepository.findSingleByUserId(currentUser.getUserId()))
                .thenReturn(Optional.of(currentUser));
        when(teamService.findById(teams.get(0).getId()))
                .thenReturn(teams.get(0));
        when(teamMemberRepository.save(any(TeamMember.class)))
                .thenReturn(savedMember);

        TeamMemberDto result = teamMemberService.addMemberToTheTeam(project.getId(), teamMemberDto);

        assertNotNull(result);

        verify(teamMemberRepository, times(1)).save(any(TeamMember.class));
    }

    @Test
    @DisplayName("Checking for lack of access to add a member to a group")
    public void lackOfAccessToAddMemberToGroupTest() {
        Long userId = 1L;

        Project project = Project.builder()
                .id(1L)
                .ownerId(2L)
                .build();

        List<Team> teams = List.of(
                Team.builder()
                        .id(1L)
                        .teamMembers(memberList1)
                        .project(project)
                        .build(),
                Team.builder()
                        .id(2L)
                        .teamMembers(memberList2)
                        .project(project)
                        .build()
        );

        project.setTeams(teams);

        TeamMember currentUser = TeamMember.builder()
                .id(1L)
                .userId(1L)
                .roles(List.of(TeamRole.INTERN))
                .build();

        TeamMemberDto teamMemberDto = TeamMemberDto.builder()
                .userId(userId)
                .team(1L)
                .role(List.of("MANAGER"))
                .build();

        when(projectService.getProjectById(project.getId()))
                .thenReturn(project);
        when(userContext.getUserId())
                .thenReturn(currentUser.getUserId());
        when(teamMemberRepository.findSingleByUserId(currentUser.getUserId()))
                .thenReturn(Optional.of(currentUser));

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                teamMemberService.addMemberToTheTeam(project.getId(), teamMemberDto)
        );
        assertEquals("You are not authorized to add team members", exception.getMessage());
    }

    @Test
    @DisplayName("Successfully update existing member to the team")
    public void successfullyUpdateMemberInTheTeamTest() {
        Long newUserId = 2L;

        Project project = Project.builder()
                .id(1L)
                .ownerId(1L)
                .build();

        List<Team> teams = List.of(
                Team.builder()
                        .id(1L)
                        .teamMembers(memberList1)
                        .project(project)
                        .build(),
                Team.builder()
                        .id(2L)
                        .teamMembers(memberList2)
                        .project(project)
                        .build()
        );

        project.setTeams(teams);

        TeamMember currentUser = TeamMember.builder()
                .id(1L)
                .userId(1L)
                .build();

        TeamMember updatedUser = TeamMember.builder()
                .userId(newUserId)
                .build();

        TeamMemberDto teamMemberDto = TeamMemberDto.builder()
                .userId(newUserId)
                .team(1L)
                .role(List.of("MANAGER"))
                .build();

        when(projectService.getProjectById(project.getId()))
                .thenReturn(project);
        when(userContext.getUserId())
                .thenReturn(currentUser.getUserId());
        when(teamMemberRepository.findSingleByUserId(currentUser.getUserId()))
                .thenReturn(Optional.of(currentUser));
        when(teamMemberRepository.save(any(TeamMember.class)))
                .thenReturn(updatedUser);

        TeamMemberDto result = teamMemberService.addMemberToTheTeam(project.getId(), teamMemberDto);

        assertNotNull(result);

        verify(teamMemberRepository, times(1)).save(any(TeamMember.class));
    }

    @Test
    @DisplayName("Successfully update member as Team Lead")
    public void successfullyUpdateMemberAsTeamLeadTest() {
        Long currentUserId = 1L;
        Long updateUserId = 2L;
        Long teamId = 1L;
        Long projectId = 1L;

        TeamMember currentUser = TeamMember.builder()
                .id(currentUserId)
                .roles(List.of(TeamRole.TEAMLEAD))
                .build();

        TeamMember updatedUser = TeamMember.builder()
                .userId(updateUserId)
                .roles(Collections.emptyList())
                .build();

        Project project = Project.builder()
                .id(projectId)
                .ownerId(1L)
                .build();

        List<Team> teams = List.of(
                Team.builder()
                        .id(1L)
                        .teamMembers(memberList1)
                        .project(project)
                        .build(),
                Team.builder()
                        .id(2L)
                        .teamMembers(memberList2)
                        .project(project)
                        .build()
        );

        project.setTeams(teams);

        TeamMemberUpdateDto teamMemberUpdateDto = TeamMemberUpdateDto.builder()
                .updateUserId(updateUserId)
                .roles(List.of("INTERN"))
                .teamId(teamId)
                .build();

        UserDto userDto = UserDto.builder()
                .id(updateUserId)
                .email("newuser@example.com")
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectService.getProjectById(project.getId()))
                .thenReturn(project);
        when(teamMemberRepository.findById(updateUserId))
                .thenReturn(Optional.of(updatedUser));
        when(userContext.getUserId())
                .thenReturn(currentUserId);
        when(teamMemberRepository.findSingleByUserId(currentUserId))
                .thenReturn(Optional.of(currentUser));
        when(userServiceClient.getUser(updateUserId))
                .thenReturn(userDto);
        when(teamMemberRepository.save(any(TeamMember.class)))
                .thenReturn(updatedUser);
        when(userServiceClient.saveUser(any(UserDto.class)))
                .thenReturn(userDto);

        TeamMemberDto result = teamMemberService.updateMemberInTheTeam(projectId, teamMemberUpdateDto);

        assertNotNull(result);

        verify(projectService, times(1)).getProjectById(projectId);
        verify(teamMemberRepository, times(1)).findById(updateUserId);
        verify(teamMemberRepository, times(1)).findSingleByUserId(currentUserId);
        verify(userServiceClient, times(1)).getUser(updateUserId);
        verify(teamMemberRepository, times(1)).save(any(TeamMember.class));
        verify(userServiceClient, times(1)).saveUser(any(UserDto.class));
    }

    @Test
    @DisplayName("Successfully update member as User")
    public void successfullyUpdateMemberAsUserTest() {
        Long currentUserId = 1L;
        Long updateUserId = 1L;
        String updateUsername = "New User";
        Long teamId = 1L;
        Long projectId = 1L;

        TeamMember currentUser = TeamMember.builder()
                .id(currentUserId)
                .userId(updateUserId)
                .roles(List.of(TeamRole.DEVELOPER))
                .build();

        TeamMember updatedUser = TeamMember.builder()
                .userId(updateUserId)
                .roles(Collections.emptyList())
                .build();

        Project project = Project.builder()
                .id(projectId)
                .ownerId(1L)
                .build();

        List<Team> teams = List.of(
                Team.builder()
                        .id(1L)
                        .teamMembers(memberList1)
                        .project(project)
                        .build(),
                Team.builder()
                        .id(2L)
                        .teamMembers(memberList2)
                        .project(project)
                        .build()
        );

        project.setTeams(teams);

        TeamMemberUpdateDto teamMemberUpdateDto = TeamMemberUpdateDto.builder()
                .updateUserId(updateUserId)
                .username(updateUsername)
                .roles(List.of("INTERN"))
                .teamId(teamId)
                .build();

        UserDto userDto = UserDto.builder()
                .id(updateUserId)
                .username(updateUsername)
                .email("newuser@example.com")
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectService.getProjectById(project.getId()))
                .thenReturn(project);
        when(teamMemberRepository.findById(updateUserId))
                .thenReturn(Optional.of(updatedUser));
        when(userContext.getUserId())
                .thenReturn(currentUserId);
        when(teamMemberRepository.findSingleByUserId(currentUserId))
                .thenReturn(Optional.of(currentUser));
        when(userServiceClient.getUser(updateUserId))
                .thenReturn(userDto);
        when(userServiceClient.saveUser(any(UserDto.class)))
                .thenReturn(userDto);

        TeamMemberDto result = teamMemberService.updateMemberInTheTeam(projectId, teamMemberUpdateDto);

        assertNotNull(result);

        verify(projectService, times(1)).getProjectById(projectId);
        verify(teamMemberRepository, times(1)).findById(updateUserId);
        verify(userServiceClient, times(1)).getUser(userDto.getId());
        verify(userServiceClient, times(1)).saveUser(any(UserDto.class));
    }

    @Test
    @DisplayName("Successfully delete member from the team")
    public void successfullyDeleteMemberFromTheTeamTest() {
        Long projectId = 1L;
        Long currentUserId = 1L;
        Long deleteUserId = 2L;

        Project project = Project.builder()
                .id(1L)
                .ownerId(1L)
                .build();

        List<Team> teams = List.of(
                Team.builder()
                        .id(1L)
                        .teamMembers(memberList1)
                        .project(project)
                        .build(),
                Team.builder()
                        .id(2L)
                        .teamMembers(memberList2)
                        .project(project)
                        .build()
        );

        project.setTeams(teams);

        TeamMember currentUser = TeamMember.builder()
                .id(currentUserId)
                .userId(deleteUserId)
                .roles(List.of(TeamRole.DEVELOPER))
                .team(teams.get(0))
                .build();

        TeamMember deleteUser = TeamMember.builder()
                .id(deleteUserId)
                .userId(deleteUserId)
                .build();

        when(userContext.getUserId())
                .thenReturn(currentUserId);
        when(teamMemberRepository.findSingleByUserId(currentUserId))
                .thenReturn(Optional.of(currentUser));
        when(teamMemberRepository.findById(deleteUserId))
                .thenReturn(Optional.ofNullable(deleteUser));
        when(projectService.getProjectById(projectId))
                .thenReturn(project);
        doNothing().when(teamMemberRepository).deleteById(deleteUserId);

        teamMemberService.deleteMemberFromTheTeam(projectId, deleteUserId);

        verify(projectService, times(1)).getProjectById(projectId);
        verify(teamMemberRepository, times(1)).deleteById(deleteUserId);
    }

    @Test
    @DisplayName("Verifying successful retrieval of participants with filtering")
    public void checkGetTeamMembersByFilterSuccessTest() {
        Long projectId = 1L;

        TeamMemberFilterDto filterDto = TeamMemberFilterDto.builder()
                .teamMemberRolePattern("INTERN")
                .build();

        List<TeamMember> memberList = List.of(
                TeamMember.builder().id(2L).userId(2L).roles(List.of(TeamRole.INTERN)).build(),
                TeamMember.builder().id(3L).userId(3L).roles(List.of(TeamRole.INTERN)).build(),
                TeamMember.builder().id(4L).userId(4L).roles(List.of(TeamRole.INTERN)).build(),
                TeamMember.builder().id(4L).userId(5L).roles(List.of(TeamRole.INTERN)).build(),
                TeamMember.builder().id(5L).userId(6L).roles(List.of(TeamRole.OWNER)).build(),
                TeamMember.builder().id(6L).userId(7L).roles(List.of(TeamRole.MANAGER)).build()
        );

        Project project = Project.builder()
                .id(projectId)
                .ownerId(1L)
                .build();

        List<Team> teams = List.of(
                Team.builder()
                        .id(1L)
                        .teamMembers(memberList)
                        .project(project)
                        .build()
        );

        project.setTeams(teams);

        when(projectService.getProjectById(projectId))
                .thenReturn(project);

        List<TeamMemberDto> result = teamMemberService.getAllMembersWithFilter(projectId, filterDto);

        assertNotNull(result);

        verify(projectService, times(1)).getProjectById(projectId);
    }

    @Test
    @DisplayName("Successfully get all members from the project")
    public void getAllMembersFromProjectTest() {
        Long projectId = 1L;

        List<TeamMember> memberList1 = List.of(
                TeamMember.builder().id(2L).userId(2L).build(),
                TeamMember.builder().id(3L).userId(3L).build(),
                TeamMember.builder().id(4L).userId(4L).build()
        );

        List<TeamMember> memberList2 = List.of(
                TeamMember.builder().id(4L).userId(5L).build(),
                TeamMember.builder().id(5L).userId(6L).build(),
                TeamMember.builder().id(6L).userId(7L).build()
        );

        List<Team> teams = List.of(
                Team.builder()
                        .id(1L)
                        .teamMembers(memberList1)
                        .build(),
                Team.builder()
                        .id(2L)
                        .teamMembers(memberList2)
                        .build()
        );

        Project project = Project.builder()
                .id(projectId)
                .ownerId(1L)
                .teams(teams)
                .build();

        when(projectService.getProjectById(projectId))
                .thenReturn(project);

        List<TeamMemberDto> result = teamMemberService.getAllMembersFromTheProject(projectId);

        assertNotNull(result);

        verify(projectService, times(1)).getProjectById(projectId);
    }

    @Test
    @DisplayName("Get member by id")
    public void getMemberByIdTest() {
        Long memberId = 3L;
        Long projectId = 1L;

        TeamMember teamMember = TeamMember.builder()
                .id(memberId)
                .build();

        List<TeamMember> memberList = List.of(
                TeamMember.builder().id(2L).userId(2L).roles(List.of(TeamRole.INTERN)).build(),
                TeamMember.builder().id(3L).userId(3L).roles(List.of(TeamRole.INTERN)).build(),
                TeamMember.builder().id(4L).userId(4L).roles(List.of(TeamRole.INTERN)).build(),
                TeamMember.builder().id(4L).userId(5L).roles(List.of(TeamRole.INTERN)).build(),
                TeamMember.builder().id(5L).userId(6L).roles(List.of(TeamRole.OWNER)).build(),
                TeamMember.builder().id(6L).userId(7L).roles(List.of(TeamRole.MANAGER)).build()
        );

        Project project = Project.builder()
                .id(projectId)
                .ownerId(1L)
                .build();

        List<Team> teams = List.of(
                Team.builder()
                        .id(1L)
                        .teamMembers(memberList)
                        .project(project)
                        .build()
        );

        project.setTeams(teams);

        when(projectService.getProjectById(projectId))
                .thenReturn(project);

        TeamMemberDto result = teamMemberService.getMemberById(projectId, memberId);

        assertNotNull(result);

        verify(projectService, times(1)).getProjectById(projectId);
    }

    @Test
    public void findByIdNotFoundTest() {
        Long userId = 1L;
        when(teamMemberRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> teamMemberService.findById(userId));

        assertEquals("Entity %s with ID %s not found".formatted(TEAM_MEMBER, userId), exception.getMessage());
        verify(teamMemberRepository, times(1)).findById(userId);
    }

    @Test
    public void findByIdFoundTest() {
        Long userId = 1L;
        TeamMember teamMember = new TeamMember();
        teamMember.setId(userId);
        teamMember.setUserId(userId);
        when(teamMemberRepository.findById(userId)).thenReturn(Optional.of(teamMember));

        TeamMember findMember = teamMemberService.findById(userId);

        assertEquals(teamMember.getId(), findMember.getId());
        assertEquals(teamMember.getUserId(), findMember.getUserId());
    }

    @Test
    void saveTeamMemberTest() {
        TeamMember teamMember = new TeamMember();
        when(teamMemberRepository.save(teamMember)).thenReturn(teamMember);

        TeamMember savedTeamMember = assertDoesNotThrow(() -> teamMemberService.save(teamMember));
        verify(teamMemberRepository, times(1)).save(teamMember);
        assertEquals(teamMember, savedTeamMember);
    }

    @Test
    void saveTeamMembersTest() {
        TeamMember teamMember = new TeamMember();
        List<TeamMember> membersToSave = List.of(teamMember);
        when(teamMemberRepository.saveAll(membersToSave)).thenReturn(membersToSave);

        List<TeamMember> savedTeamMembers = assertDoesNotThrow(() -> teamMemberService.saveAll(membersToSave));
        assertEquals(1, savedTeamMembers.size());
        verify(teamMemberRepository, times(1)).saveAll(membersToSave);
    }

    @Test
    void findByUserIdAndProjectIdFoundTest() {
        long userId = 1L;
        long projectId = 2L;
        TeamMember teamMember = new TeamMember();
        when(teamMemberRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(Optional.of(teamMember));

        TeamMember result = teamMemberService.findByUserIdAndProjectId(userId, projectId);

        assertEquals(teamMember, result);
        verify(teamMemberRepository, times(1)).findByUserIdAndProjectId(userId, projectId);
    }

    @Test
    void findByUserIdAndProjectIdNotFoundTest() {
        long userId = 1L;
        long projectId = 2L;
        when(teamMemberRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class, () -> teamMemberService.findByUserIdAndProjectId(userId, projectId));

        assertEquals("Entity %s with ID %s not found".formatted(TEAM_MEMBER, userId), exception.getMessage());
        verify(teamMemberRepository, times(1)).findByUserIdAndProjectId(userId, projectId);
    }

    @Test
    void deleteTeamMemberTest() {
        TeamMember teamMember = new TeamMember();
        assertDoesNotThrow(() -> teamMemberService.delete(teamMember));
        verify(teamMemberRepository, times(1)).delete(teamMember);
    }

    @Test
    void deleteAllTeamMembersTest() {
        List<TeamMember> teamMembersToDelete = List.of(new TeamMember());
        assertDoesNotThrow(() -> teamMemberService.deleteAll(teamMembersToDelete));
        verify(teamMemberRepository, times(1)).deleteAll(teamMembersToDelete);
    }

    @Test
    public void teamMemberNotExistsTest() {
        long id = 1L;

        assertThrows(EntityNotFoundException.class,
                () -> teamMemberService.getTeamMemberEntity(id));
    }

    @Test
    public void teamMemberExistsTest() {
        long id = 1L;
        TeamMember teamMember = new TeamMember();
        when(teamMemberRepository.findById(id)).thenReturn(Optional.of(teamMember));

        assertDoesNotThrow(() -> teamMemberService.getTeamMemberEntity(id));
    }
}