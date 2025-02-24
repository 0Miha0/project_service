package faang.school.projectservice.validator.meet;

import faang.school.projectservice.exception.customexception.DataValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeetValidator {

    public void memberIsTheCreatorValidate(long creatorId, long memberId) {
        if (creatorId != memberId) {
            log.warn("This member not creator");
            throw new DataValidationException("This member not creator");
        }
    }
}
