package faang.school.projectservice.controller.project;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.file_streaming.FileStreamingService;
import faang.school.projectservice.service.resource.ResourceService;
import faang.school.projectservice.service.project.ProjectFilesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Tag(name = "Project Files", description = "Operations related to project files")
@Slf4j
@RequestMapping("/projects")
@RestController
@RequiredArgsConstructor
public class ProjectFileController {

    private final ProjectFilesService projectFilesService;
    private final FileStreamingService fileStreamingService;
    private final ResourceService resourceService;

    @Operation(summary = "Upload a file to the project",
            description = "Uploads a file to the specified project's common files.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Invalid file data or request"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred during processing")
    })
    @PostMapping("/{projectId}/resources")
    @Async("fileExecutor")
    public CompletableFuture<ResponseEntity<String>> uploadFile(@PathVariable @NotNull Long projectId,
                                                                @RequestHeader("x-team-member-id") @NotNull Long teamMemberId,
                                                                @RequestParam(value = "file", required = false) MultipartFile file) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                projectFilesService.uploadFile(projectId, teamMemberId, file);
                return ResponseEntity.ok("File uploaded successfully");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error: " + e.getMessage());
            }
        });
    }

    @Operation(summary = "Delete a file from the project",
            description = "Deletes the specified file from the project's common files.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File deleted successfully"),
            @ApiResponse(responseCode = "404", description = "File or project not found")
    })
    @DeleteMapping("/resources/{resourceId}")
    public ResponseEntity<String> deleteFile(@PathVariable @NotNull Long resourceId,
                                             @RequestHeader("x-team-member-id") @NotNull Long teamMemberId) {
        projectFilesService.deleteFile(resourceId, teamMemberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Download a file of the project",
            description = "Downloads the specified file from common project's files.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File downloaded successfully"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred during processing")
    })
    @GetMapping("/resources/{resourceId}")
    @Async("filesExecutor")
    public CompletableFuture<ResponseEntity<StreamingResponseBody>> downloadFile(@PathVariable @NotNull Long resourceId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                InputStream fileStream = projectFilesService.downloadFile(resourceId);
                StreamingResponseBody responseBody = fileStreamingService.getStreamingResponseBody(fileStream);

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(getMimeType(resourceId)))
                        .body(responseBody);
            } catch (Exception e) {
                log.warn("Error while downloading file", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(null);
            }
        });
    }

    @Operation(summary = "Download all project files",
            description = "Downloads all files for the specified project as a zip archive.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Files downloaded successfully"),
            @ApiResponse(responseCode = "404", description = "Project or files not found"),
    })
    @GetMapping("/{projectId}/resources")
    @Async("filesExecutor")
    public CompletableFuture<ResponseEntity<StreamingResponseBody>> downloadAllFiles(@PathVariable @NotNull Long projectId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, InputStream> files = projectFilesService.downloadAllFiles(projectId);

                if (files.isEmpty()) {
                    log.warn("No files found for project {}", projectId);
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
                }

                StreamingResponseBody responseBody = fileStreamingService.getStreamingResponseBodyInZip(files);

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=project_" +
                                projectId + "_resources.zip")
                        .body(responseBody);
            } catch (Exception e) {
                log.warn("Error while downloading files for project {}", projectId, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(null);
            }
        });
    }

    @Operation(summary = "Update Project Cover")
    @PutMapping("/{projectId}/cover")
    public ResponseEntity<Void> updateProjectCover(@PathVariable Long projectId,
                                                   @RequestParam(value = "file", required = false) MultipartFile file) {
        projectFilesService.updateProjectCover(projectId, file);
        return ResponseEntity.ok().build();
    }

    private String getMimeType(Long resourceId) {
        Resource resource = resourceService.findById(resourceId);
        return URLConnection.guessContentTypeFromName(resource.getName());
    }
}
