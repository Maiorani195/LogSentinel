package com.logsentinel.detector;

import com.logsentinel.config.LogSentinelProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class BruteForceDetector {
    private final LogSentinelProperties properties;
    private final List<LocalDateTime> tentativas = new ArrayList<>();


    public  BruteForceDetector(LogSentinelProperties properties){
        this.properties = properties;
    }

    public boolean detectarForcaBruta(String linha){
        if(linha.toLowerCase().contains("unauthorized") || linha.toLowerCase().contains("denied")){
            tentativas.add(LocalDateTime.now());
        }

        LocalDateTime agora = LocalDateTime.now();
        tentativas.removeIf(horario -> horario.isBefore(agora.minusSeconds(properties.getBruteForce().getWindowSeconds())));

        return tentativas.size() >= properties.getBruteForce().getAttempts();

    }



}
