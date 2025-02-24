package faang.school.projectservice.repository;

import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetRepository extends JpaRepository<Meet, Long> {

    Optional<Meet> findByProject(Project project);

    Optional<List<Meet>> findAllByProjectId(long projectId);

    Optional<Meet> findByCreatorId(long creatorId);
}
