package com.logsentinel.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.logsentinel.history.HistoryRepository;

import java.util.List;
import java.util.Map;

@Component
public class StartupCheck implements CommandLineRunner {

    private final LogSentinelProperties properties;
    private final HistoryRepository historyRepository;

    public StartupCheck(LogSentinelProperties properties, HistoryRepository historyRepository) {
        this.properties = properties;
        this.historyRepository = historyRepository;
    }

    @Override
    public void run(String... args) {
        System.out.println("Keywords: " + properties.getKeywords());
        System.out.println("Brute force: " + properties.getBruteForce().getAttempts() + " tentativas em " + properties.getBruteForce().getWindowSeconds() + "s");
        System.out.println("Cascade: " + properties.getCascade().getErrors() + " erros em " + properties.getCascade().getWindowSeconds() + "s");
        System.out.println("Slack webhook: " + properties.getSlack().getWebhookUrl());


        historyRepository.salvarAnomalia("keyword", "2026-09-10T17:40:00", "ERROR: falha ao conectar", "sent");

        List<Map<String, Object>> anomalias = historyRepository.listar_todas();
        System.out.println("Anomalias no banco: " + anomalias);


    }
}