package com.cityhospital.service;

import com.cityhospital.model.EmailConfig;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {

    public static boolean sendEmail(String to, String subject, String body) {
        EmailConfig config = FileManager.loadEmailConfig();
        if (config.getSmtpHost() == null || config.getSmtpHost().isEmpty()) {
            System.err.println("Email not sent: SMTP not configured.");
            return false;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", config.getSmtpHost());
        props.put("mail.smtp.port", String.valueOf(config.getSmtpPort()));
        props.put("mail.smtp.auth", "true");
        if (config.isUseTls()) {
            props.put("mail.smtp.starttls.enable", "true");
        }

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.getUsername(), config.getPassword());
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(config.getFromAddress()));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject);
            msg.setText(body);
            Transport.send(msg);
            System.out.println("Email sent to " + to + " via " + config.getSmtpHost());
            return true;
        } catch (MessagingException e) {
            System.err.println("Email send failed: " + e.getMessage());
            return false;
        }
    }
}
