package com.cityhospital.model;

import java.io.Serializable;

public class EmailConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private String smtpHost;
    private int smtpPort;
    private String username;
    private String password;
    private String fromAddress;
    private boolean useTls;

    public EmailConfig() {
        this.smtpHost = "smtp.gmail.com";
        this.smtpPort = 587;
        this.username = "";
        this.password = "";
        this.fromAddress = "";
        this.useTls = true;
    }

    public EmailConfig(String smtpHost, int smtpPort, String username, String password, String fromAddress, boolean useTls) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.username = username;
        this.password = password;
        this.fromAddress = fromAddress;
        this.useTls = useTls;
    }

    public String getSmtpHost() { return smtpHost; }
    public void setSmtpHost(String smtpHost) { this.smtpHost = smtpHost; }
    public int getSmtpPort() { return smtpPort; }
    public void setSmtpPort(int smtpPort) { this.smtpPort = smtpPort; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFromAddress() { return fromAddress; }
    public void setFromAddress(String fromAddress) { this.fromAddress = fromAddress; }
    public boolean isUseTls() { return useTls; }
    public void setUseTls(boolean useTls) { this.useTls = useTls; }
}
