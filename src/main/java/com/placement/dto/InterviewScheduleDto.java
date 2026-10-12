package com.placement.dto;

import com.placement.entity.InterviewSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * InterviewScheduleDto - Data Transfer Object encapsulating interview scheduling requests.
 *
 * Demonstrates:
 * - OOP Encapsulation: Validates round scheduling requirements and date formatting.
 */
public class InterviewScheduleDto {

    private Long id;

    @NotNull(message = "Application reference is required")
    private Long jobApplicationId;

    @NotBlank(message = "Interview round name is required")
    private String roundName;

    @NotNull(message = "Interview date and time is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime scheduledDateTime;

    @NotBlank(message = "Meeting venue or video call link is required")
    private String venueOrLink;

    private String interviewerName;

    private String feedback;

    private InterviewSchedule.InterviewStatus status = InterviewSchedule.InterviewStatus.SCHEDULED;

    public InterviewScheduleDto() {
    }

    public InterviewScheduleDto(Long jobApplicationId, String roundName,
                                LocalDateTime scheduledDateTime, String venueOrLink) {
        this.jobApplicationId = jobApplicationId;
        this.roundName = roundName;
        this.scheduledDateTime = scheduledDateTime;
        this.venueOrLink = venueOrLink;
        this.status = InterviewSchedule.InterviewStatus.SCHEDULED;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(Long jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }

    public String getRoundName() {
        return roundName;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    public LocalDateTime getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(LocalDateTime scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    public String getVenueOrLink() {
        return venueOrLink;
    }

    public void setVenueOrLink(String venueOrLink) {
        this.venueOrLink = venueOrLink;
    }

    public String getInterviewerName() {
        return interviewerName;
    }

    public void setInterviewerName(String interviewerName) {
        this.interviewerName = interviewerName;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public InterviewSchedule.InterviewStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewSchedule.InterviewStatus status) {
        this.status = status;
    }
}
