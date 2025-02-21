package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.team_member.TeamMemberDto;
import faang.school.projectservice.dto.team_member.TeamMemberFilterDto;
import faang.school.projectservice.dto.team_member.TeamMemberUpdateDto;
import faang.school.projectservice.exception.customexception.DataValidationException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.team_member.TeamMemberMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.team_member.TeamMemberValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberMapper teamMemberMapper;
    private final TeamMemberValidator teamMemberValidator;
    private final ProjectService projectService;
    private final TeamService teamService;
    private final List<Filter<TeamMember, TeamMemberFilterDto>> teamMemberFilters;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    @Transactional
    public TeamMemberDto addMemberToTheTeam(Long projectId, TeamMemberDto teamMemberDto) {
        log.info("Adding team member to the team: {}", teamMemberDto.getTeam());

        Project project = projectService.findById(projectId);
        TeamMember currentUser = findSingleByUserId(userContext.getUserId());

        if (!hasPermissionToAddMember(project, currentUser)) {
            log.warn("User {} attempted to add a team member without proper authorization", currentUser.getUserId());
            throw new DataValidationException("You are not authorized to add team members");
        }

        Optional<TeamMember> existingMember = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(member -> member.getUserId().equals(teamMemberDto.getUserId()))
                .findFirst();

        TeamMemberDto result;
        if (existingMember.isPresent()) {
            result = updateExistingMember(existingMember.get(), teamMemberDto);
            log.info("Updated existing team member: {}", result.getUserId());
        } else {
            result = addNewMember(teamMemberDto);
            log.info("Added new team member: {}", result.getUserId());
        }

        return result;
    }

    @Transactional
    public TeamMemberDto updateMemberInTheTeam(Long projectId, TeamMemberUpdateDto teamMemberUpdateDto) {
        Team team = projectService.findById(projectId).getTeams().stream()
                .filter(t -> t.getId().equals(teamMemberUpdateDto.getTeamId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Team not found"));

        TeamMember updateUser = findById(teamMemberUpdateDto.getUpdateUserId());
        TeamMember currentUser = findSingleByUserId(userContext.getUserId());

        if (currentUser.getRoles().contains(TeamRole.TEAMLEAD)) {
            return updateMemberAsTeamLead(teamMemberUpdateDto, team, updateUser);
        } else if (currentUser.getUserId().equals(teamMemberUpdateDto.getUpdateUserId())) {
            return updateMemberAsUser(teamMemberUpdateDto, updateUser);
        } else {
            throw new DataValidationException("You do not have permission to update this team member");
        }
    }

    @Transactional
    public void deleteMemberFromTheTeam(Long projectId, Long userId) {
        log.info("Deleting team members from the project: {}", userId);

        TeamMember currentUser = findSingleByUserId(userContext.getUserId());
        TeamMember deletedMember = findById(userId);

        Project project = projectService.findById(projectId);

        if (!project.getOwnerId().equals(currentUser.getId())) {
            throw new DataValidationException("You are not authorized to delete team members");
        }

        deleteById(deletedMember.getId());
    }

    public List<TeamMemberDto> getAllMembersWithFilter(Long projectId, TeamMemberFilterDto teamMemberFilterDto) {
        Stream<TeamMember> teamMemberStream = projectService.findById(projectId).getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream());

        return teamMemberFilters.stream()
                .filter(filter -> filter.isApplicable(teamMemberFilterDto))
                .reduce(teamMemberStream, ((stream, filter) -> filter.apply(stream, teamMemberFilterDto)), (e1, e2) -> e1)
                .map(teamMemberMapper::toDto)
                .toList();
    }

    public List<TeamMemberDto> getAllMembersFromTheProject(Long projectId) {
        log.info("Retrieving all team members for project: {}", projectId);
        Project project = projectService.findById(projectId);
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(teamMemberMapper::toDto)
                .toList();
    }

    public TeamMemberDto getMemberById(Long projectId, Long userId) {
        log.debug("Retrieving team member by ID: {} for project: {}", userId, projectId);

        TeamMember teamMember = projectService.findById(projectId).getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(member -> member.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Team member not found"));

        log.info("Team member successfully retrieved, ID: {}", userId);
        return teamMemberMapper.toDto(teamMember);
    }

    private TeamMemberDto updateMemberAsTeamLead(TeamMemberUpdateDto dto, Team team, TeamMember updateUser) {
        UserDto user = userServiceClient.getUser(dto.getUpdateUserId());

        List<TeamRole> newRoles = dto.getRoles().stream()
                .map(TeamRole::valueOf)
                .toList();

        updateUser.setTeam(team);

        List<TeamRole> updatedRoles = new ArrayList<>(updateUser.getRoles());
        updatedRoles.addAll(newRoles);
        updateUser.setRoles(updatedRoles);

        TeamMember savedMember = save(updateUser);

        user.setUsername(dto.getUsername());
        user.setUpdatedAt(LocalDateTime.now());
        userServiceClient.saveUser(user);

        return teamMemberMapper.toDto(savedMember);
    }

    private TeamMemberDto updateMemberAsUser(TeamMemberUpdateDto dto, TeamMember updateUser) {
        UserDto user = userServiceClient.getUser(dto.getUpdateUserId());
        user.setUsername(dto.getUsername());
        user.setUpdatedAt(LocalDateTime.now());
        userServiceClient.saveUser(user);

        return teamMemberMapper.toDto(updateUser);
    }

    private boolean hasPermissionToAddMember(Project project, TeamMember currentUser) {
        return project.getOwnerId().equals(currentUser.getId()) ||
                currentUser.getRoles().stream().anyMatch(role -> role == TeamRole.TEAMLEAD);
    }

    private TeamMemberDto updateExistingMember(TeamMember existingMember, TeamMemberDto teamMemberDto) {
        List<TeamRole> newRoles = teamMemberDto.getRole().stream()
                .map(TeamRole::valueOf)
                .collect(Collectors.toList());

        existingMember.setRoles(newRoles);
        TeamMember updatedMember = teamMemberRepository.save(existingMember);

        return teamMemberMapper.toDto(updatedMember);
    }

    private TeamMemberDto addNewMember(TeamMemberDto teamMemberDto) {
        Team team = teamService.findById(teamMemberDto.getTeam());
        List<TeamRole> teamRoles = teamMemberDto.getRole().stream()
                .map(TeamRole::valueOf)
                .toList();
        TeamMember teamMember = TeamMember.builder()
                .userId(teamMemberDto.getUserId())
                .roles(teamRoles)
                .team(team)
                .build();

        TeamMember savedMember = save(teamMember);
        log.info("New team member added successfully: {}", savedMember.getUserId());

        return teamMemberMapper.toDto(savedMember);
    }

    public List<TeamRole> getTeamMemberRole(Long teamMemberId) {
        log.info("Retrieving team member roles for ID: {}", teamMemberId);
        return teamMemberRepository.findUserRolesByUserId(teamMemberId);
    }

    public TeamMember findById(long id) {
        return teamMemberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team member not found"));
    }

    public List<TeamMember> findAllById(List<Long> id) {
        return teamMemberRepository.findAllById(id);
    }

    public TeamMember save(TeamMember teamMember) {
        return teamMemberRepository.save(teamMember);
    }

    public List<TeamMember> saveAll(List<TeamMember> teamMembers) {
        return teamMemberRepository.saveAll(teamMembers);
    }

    public TeamMember findByUserIdAndProjectId(long userId, long projectId) {
        return teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
    }

    public TeamMember findSingleByUserId(long userId) {
        return teamMemberRepository.findSingleByUserId(userId);
    }

    public List<TeamMember> findAll() {
        return teamMemberRepository.findAll();
    }

    public void deleteById(long id) {
        teamMemberRepository.deleteById(id);
    }

    public void delete(TeamMember teamMember) {
        teamMemberRepository.delete(teamMember);
    }

    public void deleteAll(List<TeamMember> teamMembers) {
        teamMemberRepository.deleteAll(teamMembers);
    }

    public TeamMember getTeamMemberEntity(long teamMemberId) {
        return teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new EntityNotFoundException("Team member not found"));
    }
}
