package faang.school.projectservice.filter.team_member;

import faang.school.projectservice.dto.team_member.TeamMemberFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.TeamMember;
import io.micrometer.common.util.StringUtils;

import java.util.stream.Stream;

public class TeamMemberRoleFilter implements Filter<TeamMember, TeamMemberFilterDto> {

    @Override
    public boolean isApplicable(TeamMemberFilterDto dto) {
        return dto != null && StringUtils.isNotBlank(dto.getTeamMemberRolePattern());
    }

    @Override
    public Stream<TeamMember> apply(Stream<TeamMember> teamMember, TeamMemberFilterDto dto) {
        if (dto == null || StringUtils.isBlank(dto.getTeamMemberRolePattern())) {
            return teamMember;
        }

        String teamRolePattern = dto.getTeamMemberRolePattern().trim().toUpperCase();
        return teamMember.filter(teamMember1 ->
                teamMember1.getRoles().stream()
                        .anyMatch(teamMemberRoles ->
                                teamMemberRoles.name().equals(teamRolePattern)
                        )
        );
    }
}

