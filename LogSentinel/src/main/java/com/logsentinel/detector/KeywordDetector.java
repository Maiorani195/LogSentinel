package com.logsentinel.detector;

import com.logsentinel.config.LogSentinelProperties;
import org.springframework.stereotype.Component;

@Component
public class KeywordDetector {
    private final LogSentinelProperties properties;


    public KeywordDetector(LogSentinelProperties properties){
        this.properties = properties;
    }
    public boolean contemPalavraChave(String linha){
        String linhaMinuscula = linha.toLowerCase();

        for (String keyword : properties.getKeywords()) {
            if(linhaMinuscula.contains(keyword.toLowerCase())){
                return true;
            }
        }

   return  false;
    }



}

