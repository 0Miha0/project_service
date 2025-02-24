package faang.school.projectservice.service;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.mapper.meet.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.validator.meet.MeetValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetService {

    private final MeetRepository meetRepository;
    private final MeetMapper meetMapper;
    private final MeetValidator meetValidator;

    public MeetDto createMeet(MeetDto dto, long creatorId) {
        log.info("Create meet: {}", dto.getTitle());
        Meet meet = meetMapper.toEntity(dto);
        meet.setStatus(MeetStatus.PENDING);
        meet.setCreatorId(creatorId);
        meet.setCreatedAt(LocalDateTime.now());
        log.info("Creating meet: {}", dto.getTitle());
        return meetMapper.toDto(save(meet));
    }

    public MeetDto updateMeet(long meetId, MeetDto dto, long memberId) {
        log.info("Update meet with id: {}", meetId);
        Meet meet = findById(meetId);
        meetValidator.memberIsTheCreatorValidate(meet.getCreatorId(), memberId);
        meetMapper.updateMeetFromDto(meet, dto);
        meet.setUpdatedAt(LocalDateTime.now());
        log.info("Updated meet with id: {}", meet);
        return meetMapper.toDto(save(meet));
    }

    public void deleteMeet(long meetId, long memberId) {
        log.info("Delete meet with id: {}", meetId);
        meetValidator.memberIsTheCreatorValidate(findById(meetId).getCreatorId(), memberId);
        deleteById(meetId);
        log.info("Deleted meet with id: {}", meetId);
    }

    public List<MeetDto> getAllMeetsFromProject(long projectId) {
        log.info("Get all meets from project with id: {}", projectId);
        return meetMapper.toDtoList(findAllByProjectId(projectId));
    }

    public MeetDto getMeetById(long meetId) {
        log.info("Get meet with id: {}", meetId);
        return meetMapper.toDto(findById(meetId));
    }

    public List<Meet> findAllByProjectId(long projectId) {
        log.info("Find all meet from project with id: {}", projectId);
        return meetRepository.findAllByProjectId(projectId)
                .orElseThrow(() -> new EntityNotFoundException("This project has no planned meetings"));
    }

    public Meet findById(long meetId) {
        log.info("Find meet with id: {}", meetId);
        return meetRepository.findById(meetId)
                .orElseThrow(() -> new EntityNotFoundException("Meet with id " + meetId + " not found"));
    }

    public void deleteById(long id) {
        log.info("Delete by id meet with id: {}", id);
        meetRepository.deleteById(id);
        log.info("Deleted by id meet with id: {}", id);
    }

    public Meet save(Meet meet) {
        log.info("Save meet: {}", meet.getTitle());
        return meetRepository.save(meet);
    }
}
