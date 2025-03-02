package faang.school.projectservice.event_drive.redis.publisher;

import faang.school.projectservice.event_drive.redis.event.ProjectCreateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectCreateEventPublisher implements EventPublisher<ProjectCreateEvent>{

    @Value("${spring.data.redis.channels.project_create}")
    private String channel;

    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(ProjectCreateEvent event) {
        redisTemplate.convertAndSend(channel, event);
    }
}
