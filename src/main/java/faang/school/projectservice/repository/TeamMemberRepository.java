package faang.school.projectservice.repository;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    @Query(
            "SELECT tm FROM TeamMember tm JOIN tm.team t " +
                    "WHERE tm.userId = :userId " +
                    "AND t.project.id = :projectId"
    )
    TeamMember findByUserIdAndProjectId(long userId, long projectId);

    List<TeamMember> findByUserId(long userId);

    TeamMember findSingleByUserId(long userId);

    @Query("SELECT tm.roles FROM TeamMember tm WHERE tm.id = :userId")
    List<TeamRole> findUserRolesByUserId(@Param("userId") Long userId);
}
