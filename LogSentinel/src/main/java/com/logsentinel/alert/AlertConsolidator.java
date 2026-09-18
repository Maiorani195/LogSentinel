package com.logsentinel.alert;


import com.logsentinel.detector.BruteForceDetector;
import com.logsentinel.detector.CascadeDetector;
import com.logsentinel.detector.KeywordDetector;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AlertConsolidator {
    private  final KeywordDetector keywordDetector;
    private  final BruteForceDetector bruteForceDetector;
    private final CascadeDetector cascadeDetector;


    public AlertConsolidator(KeywordDetector keywordDetector, BruteForceDetector bruteForceDetector , CascadeDetector cascadeDetector){
        this.cascadeDetector =cascadeDetector;
        this.bruteForceDetector =bruteForceDetector;
        this.keywordDetector = keywordDetector;
    }
    public  String consolidarAvisos(String linha){
        List<String> tiposDetectados = new ArrayList<>();


        if(keywordDetector.contemPalavraChave(linha)){
            tiposDetectados.add("keyword");
        }

        if (bruteForceDetector.detectarForcaBruta(linha)){
            tiposDetectados.add("brute_force");
        }
        if(cascadeDetector.detectarErrosEmCascata(linha)){
            tiposDetectados.add("cascade");

        }
        if(tiposDetectados.isEmpty()){
            return null;
        }
        return "Anomalia Encontrada: ("+String.join(", ", tiposDetectados) + "): " +linha;
    }
}
