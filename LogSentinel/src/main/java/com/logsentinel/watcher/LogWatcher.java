package com.logsentinel.watcher;

import com.logsentinel.alert.AlertConsolidator;
import com.logsentinel.alert.SlackNotifier;
import org.springframework.stereotype.Component;
import com.logsentinel.history.PositionStore;

import java.io.IOException;
import java.io.RandomAccessFile;


@Component
public class LogWatcher {
    private final PositionStore positionStore;
    private  final AlertConsolidator alertConsolidator;
    private final SlackNotifier slackNotifier;


    public LogWatcher(PositionStore positionStore , AlertConsolidator alertConsolidator , SlackNotifier slackNotifier) {
        this.alertConsolidator =alertConsolidator;
        this.positionStore = positionStore;
        this.slackNotifier = slackNotifier;
    }

    public  void lerNovasLinhas(String path ) {
        try (RandomAccessFile arquivo = new RandomAccessFile(path, "r")) {
            long offsetSalvo = positionStore.recuperarPosicao();
            long tamanhoArquivo = arquivo.length();

            long offsetParaUsar;
            if (offsetSalvo > tamanhoArquivo) {
                offsetParaUsar = 0;
            } else {
                offsetParaUsar = offsetSalvo;
            }

            arquivo.seek(offsetParaUsar);

            String linha;
            while ((linha = arquivo.readLine()) != null) {
                String alerta = alertConsolidator.consolidarAvisos(linha);
                if (alerta !=  null){
                    slackNotifier.enviarAlerta(alerta);
                }
            }
            positionStore.salvarPosicao(path, arquivo.getFilePointer());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


