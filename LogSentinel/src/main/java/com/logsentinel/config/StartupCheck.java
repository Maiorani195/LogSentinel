package com.logsentinel.config;

import com.logsentinel.alert.AlertConsolidator;
import com.logsentinel.detector.BruteForceDetector;
import com.logsentinel.detector.CascadeDetector;
import com.logsentinel.detector.KeywordDetector;
import com.logsentinel.watcher.FileWatcherService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.logsentinel.history.HistoryRepository;
import com.logsentinel.history.PositionStore;
import java.util.List;
import java.util.Map;
import com.logsentinel.watcher.LogWatcher;

@Component
@Order(2)
public class StartupCheck implements CommandLineRunner {

    private final LogSentinelProperties properties;
    private final HistoryRepository historyRepository;
    private  final PositionStore positionStore;
    private final LogWatcher logWatcher;
    private final FileWatcherService fileWatcherService;
    private  final KeywordDetector keywordDetector;
    private final BruteForceDetector bruteForceDetector;
    private  final CascadeDetector cascadeDetector;
    private final AlertConsolidator alertConsolidator;

    public StartupCheck(LogSentinelProperties properties, HistoryRepository historyRepository, PositionStore positionStore , LogWatcher logWatcher , FileWatcherService fileWatcherService , KeywordDetector keywordDetector , BruteForceDetector bruteForceDetector , CascadeDetector cascadeDetector , AlertConsolidator alertConsolidator) {

        this.properties = properties;
        this.historyRepository = historyRepository;
        this.positionStore = positionStore;
        this.logWatcher = logWatcher;
        this.fileWatcherService =fileWatcherService;
        this.keywordDetector = keywordDetector;
        this.bruteForceDetector = bruteForceDetector;
        this.cascadeDetector = cascadeDetector;
        this.alertConsolidator =alertConsolidator;
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


        System.out.println(keywordDetector.contemPalavraChave("2026-09-14 10:00:05 ERROR Falha ao conectar"));
        System.out.println(keywordDetector.contemPalavraChave("2026-09-14 10:00:10 INFO Requisicao processada"));


        for( int i = 1; i<=5; i++){
            boolean resultado = bruteForceDetector.detectarForcaBruta("Tentativa" + i +  "  unauthorized access");
            System.out.println("Tentativa: " + i +" " +  resultado);
        }

        System.out.println("Divisão entre ⬆ ️ Forca Bruta e ⬇ Erros em Cascata ️");



        for (int i = 1; i<=7; i++){
            boolean resultado = cascadeDetector.detectarErrosEmCascata("Tentativa " + i + "error");
            System.out.println("Tentativa: " + i +" " +  resultado);
        }

        String resultado = alertConsolidator.consolidarAvisos("2026-09-14 10:00:05 unauthorized ERROR access denied");
        System.out.println(resultado);

        fileWatcherService.iniciarMonitoramento();





    }
}