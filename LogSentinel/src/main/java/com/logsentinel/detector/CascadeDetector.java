package com.logsentinel.detector;

import com.logsentinel.config.LogSentinelProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class CascadeDetector {
    private final LogSentinelProperties properties;
    private final List<LocalDateTime> tentativas = new ArrayList<>();


    public CascadeDetector ( LogSentinelProperties properties) {
        this.properties = properties;



    }
    public boolean detectarErrosEmCascata(String linha){
        if (linha.toLowerCase().contains("error")){
            tentativas.add(LocalDateTime.now());
        }
        LocalDateTime  agora = LocalDateTime.now();
        tentativas.removeIf(horario -> horario.isBefore(agora.minusSeconds(properties.getCascade().getWindowSeconds())));

        return  tentativas.size() >= properties.getCascade().getErrors();

        }




}
