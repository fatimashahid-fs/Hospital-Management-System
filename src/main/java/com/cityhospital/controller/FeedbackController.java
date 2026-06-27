package com.cityhospital.controller;

import com.cityhospital.model.Feedback;
import com.cityhospital.service.FileManager;
import com.cityhospital.util.IDGenerator;
import java.util.List;
import java.util.stream.Collectors;

public class FeedbackController {

    public List<Feedback> getAllFeedback() {
        return FileManager.loadFeedback();
    }

    public List<Feedback> getFeedbackByPatient(String patientId) {
        return FileManager.loadFeedback().stream()
                .filter(f -> f.getPatientId().equals(patientId))
                .sorted((a, b) -> b.getSubmittedAt().compareTo(a.getSubmittedAt()))
                .collect(Collectors.toList());
    }

    public List<Feedback> getUnresolvedFeedback() {
        return FileManager.loadFeedback().stream()
                .filter(f -> Feedback.STATUS_UNRESOLVED.equals(f.getStatus()))
                .sorted((a, b) -> b.getSubmittedAt().compareTo(a.getSubmittedAt()))
                .collect(Collectors.toList());
    }

    public void submitFeedback(String patientId, String patientName, int rating, String comment) {
        List<Feedback> list = FileManager.loadFeedback();
        String id = IDGenerator.generateFeedbackId();
        list.add(new Feedback(id, patientId, patientName, Math.max(1, Math.min(5, rating)), comment));
        FileManager.saveFeedback(list);
    }

    public void markResolved(String feedbackId) {
        List<Feedback> list = FileManager.loadFeedback();
        list.stream()
                .filter(f -> f.getFeedbackId().equals(feedbackId))
                .findFirst()
                .ifPresent(f -> f.setStatus(Feedback.STATUS_RESOLVED));
        FileManager.saveFeedback(list);
    }
}
