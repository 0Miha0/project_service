package faang.school.projectservice.mapper.team_member;

import faang.school.projectservice.dto.team_member.TeamMemberDto;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMemberMapper {

    @Mapping(target = "team", source = "team", qualifiedByName = "mapTeamToLong")
    TeamMemberDto toDto(TeamMember teamMember);

    @Mapping(target = "team", source = "team", qualifiedByName = "mapLongToTeam")
    TeamMember toEntity(TeamMemberDto teamMemberDto);

    @Named("mapTeamToLong")
    default Long mapTeamToLong(Team team) {
        return team != null ? team.getId() : null;
    }

    @Named("mapLongToTeam")
    default Team mapLongToTeam(Long teamId) {
        if (teamId == null) {
            return null;
        }
        Team team = new Team();
        team.setId(teamId);
        return team;
    }
}
