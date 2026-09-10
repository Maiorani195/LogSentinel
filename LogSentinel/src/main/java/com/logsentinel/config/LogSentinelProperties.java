package com.logsentinel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "logsentinel")
public class LogSentinelProperties {

    private List<String> keywords;
    private BruteForce bruteForce;
    private Cascade cascade;
    private Slack slack;

    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }

    public BruteForce getBruteForce() { return bruteForce; }
    public void setBruteForce(BruteForce bruteForce) { this.bruteForce = bruteForce; }

    public Cascade getCascade() { return cascade; }
    public void setCascade(Cascade cascade) { this.cascade = cascade; }

    public Slack getSlack() { return slack; }
    public void setSlack(Slack slack) { this.slack = slack; }

    public static class BruteForce {
        private int attempts;
        private int windowSeconds;
        public int getAttempts() { return attempts; }
        public void setAttempts(int attempts) { this.attempts = attempts; }
        public int getWindowSeconds() { return windowSeconds; }
        public void setWindowSeconds(int windowSeconds) { this.windowSeconds = windowSeconds; }
    }

    public static class Cascade {
        private int errors;
        private int windowSeconds;
        public int getErrors() { return errors; }
        public void setErrors(int errors) { this.errors = errors; }
        public int getWindowSeconds() { return windowSeconds; }
        public void setWindowSeconds(int windowSeconds) { this.windowSeconds = windowSeconds; }
    }

    public static class Slack {
        private String webhookUrl;
        public String getWebhookUrl() { return webhookUrl; }
        public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }
    }
}