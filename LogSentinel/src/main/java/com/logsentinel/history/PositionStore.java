package com.logsentinel.history;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PositionStore {
    private final JdbcTemplate jdbcTemplate;

    public PositionStore(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public  void salvarPosicao(String path , long offset){
      String sql =  "INSERT OR REPLACE INTO file_position(id,path,last_offset) VALUES (?, ? , ?)";
      jdbcTemplate.update(sql , 1 , path , offset);

    }

    public long recuperarPosicao() {
        String sql = "SELECT last_offset FROM file_position WHERE id = 1";
        try{
        return jdbcTemplate.queryForObject(sql, Long.class);
    }catch (Exception e){
            return 0;
        }


    }
}
