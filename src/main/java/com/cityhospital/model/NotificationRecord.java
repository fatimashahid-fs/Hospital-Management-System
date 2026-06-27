package com.cityhospital.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class NotificationRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_EMAIL = "EMAIL";
    public static final String TYPE_SMS = "SMS";
    public static final String TYPE_ALERT = "ALERT";

    public static final String STATUS_SENT = "Sent";
    public static final String STATUS_FAILED = "Failed";

    private String id;
    private String type;
    private String recipient;
    private String subject;
    private String message;
    private String status;
    private LocalDateTime sentAt;

    public NotificationRecord() {}

    public NotificationRecord(String id, String type, String recipient, String subject, String message, String status, LocalDateTime sentAt) {
        this.id = id;
        this.type = type;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
        this.status = status;
        this.sentAt = sentAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}
