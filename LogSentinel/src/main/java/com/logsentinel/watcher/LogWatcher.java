package com.logsentinel.watcher;

import org.springframework.stereotype.Component;
import com.logsentinel.history.PositionStore;

import java.io.IOException;
import java.io.RandomAccessFile;


@Component
public class LogWatcher {
    private final PositionStore positionStore;


    public LogWatcher(PositionStore positionStore) {
        this.positionStore = positionStore;
    }

public  void lerNovasLinhas(String path ) {
    try (RandomAccessFile arquivo = new RandomAccessFile(path, "r")) {
        long offsetSalvo = positionStore.recuperarPosicao();
        long tamanhoArquivo = arquivo.length();

        System.out.println("DEBUG - offset salvo: " + offsetSalvo);
        System.out.println("DEBUG - tamanho do arquivo: " + tamanhoArquivo);

        long offsetParaUsar;
        if (offsetSalvo > tamanhoArquivo) {
            offsetParaUsar = 0;
        } else {
            offsetParaUsar = offsetSalvo;
        }

        arquivo.seek(offsetParaUsar);

        String linha;
        while ((linha = arquivo.readLine()) != null) {
            System.out.println(linha);
        }
        positionStore.salvarPosicao(path, arquivo.getFilePointer());
    } catch (IOException e) {
        e.printStackTrace();
    }
}

}


