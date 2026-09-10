package com.logsentinel.history;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;
import  java.util.Map;

@Component
public class HistoryRepository {
    private final JdbcTemplate jdbcTemplate;

    public HistoryRepository (JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public void salvarAnomalia(String type , String timestamp , String log_snippet , String alert_status ){
      jdbcTemplate.update(
              "INSERT INTO anomaly_history( type, timestamp , log_snippet , alert_status) VALUES(?,?,?,?)",
              type,
              timestamp,
              log_snippet,
              alert_status

      );



    }
    public  List<Map<String,Object>> listar_todas(){
        return  jdbcTemplate.queryForList("SELECT * FROM anomaly_history");
    }

}
