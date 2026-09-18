package com.logsentinel.history;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class HistoryRepository {
    private final JdbcTemplate jdbcTemplate;

    public HistoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Async
    public void salvarAnomalia(String type, String timestamp, String log_snippet, String alert_status) {
        try {

            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Inserção no banco agora roda corretamente após a pausa
        jdbcTemplate.update(
                "INSERT INTO anomaly_history(type, timestamp, log_snippet, alert_status) VALUES(?, ?, ?, ?)",
                type,
                timestamp,
                log_snippet,
                alert_status
        );

        System.out.println("Gravação no Histórico finalizada");
    }

    public List<Map<String, Object>> listar_todas() {
        return jdbcTemplate.queryForList("SELECT * FROM anomaly_history");
    }
}