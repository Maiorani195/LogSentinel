package com.logsentinel.history;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
@Component
public class SchemaInitializer implements CommandLineRunner {
    private final JdbcTemplate jdbcTemplate;

    public SchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void run (String ... args){
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS anomaly_history ("+
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "type TEXT," +
                "timestamp TEXT,"+
                "log_snippet TEXT,"+
                "alert_status TEXT" +
                ")");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS file_position("+
                "path TEXT,"+
                "last_offset INTEGER" +
                ")");
    }

}

