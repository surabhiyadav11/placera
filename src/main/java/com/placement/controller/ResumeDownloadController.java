package com.placement.controller;

import com.placement.entity.UserAccount;
import com.placement.exception.ResourceNotFoundException;
import com.placement.exception.UnauthorizedAccessException;
import com.placement.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ResumeDownloadController - Serves student PDF resume documents securely.
 *
 * Demonstrates:
 * - Authentication Verification: Prevents anonymous access to student personal resumes.
 * - Directory Traversal Prevention: Validates path normalization against root upload directory.
 * - In-Browser PDF Preview: Emits application/pdf with inline content disposition.
 */
@Controller
public class ResumeDownloadController {

    private final UserService userService;

    @Value("${placera.upload.dir:uploads/resumes}")
    private String uploadDir;

    public ResumeDownloadController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/resumes/{filename:.+}")
    public ResponseEntity<Resource> downloadResume(@PathVariable("filename") String filename) {
        UserAccount currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            throw new UnauthorizedAccessException("RESUME_ACCESS", "You must be authenticated to view resumes.");
        }

        try {
            Path targetDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path filePath = targetDirectory.resolve(filename).normalize();

            // Directory traversal guard
            if (!filePath.startsWith(targetDirectory) || !Files.exists(filePath)) {
                throw new ResourceNotFoundException("Resume Document", "filename", filename);
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("Resume Document", "filename", filename);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Resume Document", "filename", filename);
        }
    }
}
