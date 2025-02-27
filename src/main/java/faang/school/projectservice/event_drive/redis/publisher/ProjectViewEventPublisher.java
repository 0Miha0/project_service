package faang.school.projectservice.event_drive.redis.publisher;

import faang.school.projectservice.event_drive.redis.event.ProjectViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectViewEventPublisher implements EventPublisher<ProjectViewEvent>{

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.project_views}")
    private String postViewEventChannel;

    @Override
    public void publish(ProjectViewEvent event) {
        redisTemplate.convertAndSend(postViewEventChannel, event);
    }
}
