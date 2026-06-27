package com.cityhospital.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Feedback implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_RESOLVED = "Resolved";
    public static final String STATUS_UNRESOLVED = "Unresolved";

    private String feedbackId;
    private String patientId;
    private String patientName;
    private int rating;
    private String comment;
    private LocalDateTime submittedAt;
    private String status;

    public Feedback(String feedbackId, String patientId, String patientName,
                    int rating, String comment) {
        this.feedbackId = feedbackId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.rating = rating;
        this.comment = comment;
        this.submittedAt = LocalDateTime.now();
        this.status = STATUS_UNRESOLVED;
    }

    public Feedback() {}

    public String getFeedbackId() { return feedbackId; }
    public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
