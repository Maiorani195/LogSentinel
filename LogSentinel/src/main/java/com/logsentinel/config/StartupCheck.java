package com.logsentinel.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.logsentinel.history.HistoryRepository;
import com.logsentinel.history.PositionStore;
import java.util.List;
import java.util.Map;
import com.logsentinel.Watcher.LogWatcher;

@Component
public class StartupCheck implements CommandLineRunner {

    private final LogSentinelProperties properties;
    private final HistoryRepository historyRepository;
    private  final PositionStore positionStore;
    private final LogWatcher logWatcher;

    public StartupCheck(LogSentinelProperties properties, HistoryRepository historyRepository, PositionStore positionStore , LogWatcher logWatcher) {
        this.properties = properties;
        this.historyRepository = historyRepository;
        this.positionStore = positionStore;
        this.logWatcher = logWatcher;
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


        logWatcher.lerNovasLinhas("teste.log");
    }
}