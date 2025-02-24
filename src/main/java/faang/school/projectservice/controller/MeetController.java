package faang.school.projectservice.controller;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.service.MeetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Meet", description = "Meet related operations")
@RestController
@RequestMapping("/meets")
@RequiredArgsConstructor
public class MeetController {

    private final MeetService meetService;

    @Operation(summary = "Create a new meet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meet created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid meet data or request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized to access the project"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred during processing")
    })
    @PostMapping
    public ResponseEntity<MeetDto> createMeet(@RequestBody MeetDto dto,
                                              @RequestHeader("x-team-member-id") long creatorId) {
        return ResponseEntity.ok(meetService.createMeet(dto, creatorId));
    }

    @Operation(summary = "Update a meet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meet updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid meet data or request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized to access the project"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred during processing")
    })
    @PutMapping("/{meetId}/update")
    public ResponseEntity<MeetDto> updateMeet(@PathVariable long meetId,
                                              @RequestBody MeetDto dto,
                                              @RequestHeader("x-team-member-id") long memberId) {
        return ResponseEntity.ok(meetService.updateMeet(meetId, dto, memberId));
    }

    @Operation(summary = "Delete a meet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Meet deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized to access the project"),
            @ApiResponse(responseCode = "404", description = "Meet not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred during processing")
    })
    @DeleteMapping("/{meetId}")
    public ResponseEntity<Void> deleteMeet(@PathVariable long meetId,
                                            @RequestHeader("x-team-member-id") long memberId) {
        meetService.deleteMeet(meetId, memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all meets from a project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meets retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized to access the project"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<MeetDto>> getAllMeetsFromProject(@PathVariable long projectId) {
        return ResponseEntity.ok(meetService.getAllMeetsFromProject(projectId));
    }

    @Operation(summary = "Get a meet by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meet retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized to access the project"),
            @ApiResponse(responseCode = "404", description = "Meet not found")
    })
    @GetMapping("/{meetId}")
    public ResponseEntity<MeetDto> getMeetById(@PathVariable long meetId) {
        return ResponseEntity.ok(meetService.getMeetById(meetId));
    }
}
