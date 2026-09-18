package com.logsentinel.watcher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;

@Component
public class FileWatcherService {
    private final LogWatcher logWatcher;


    public FileWatcherService(LogWatcher logWatcher) {
        this.logWatcher = logWatcher;
    }

    public void iniciarMonitoramento() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path pasta = Paths.get(".");
            pasta.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

         while (true){
             WatchKey key = watchService.take();

             for (WatchEvent<?> evento : key.pollEvents()){
                 String nomeArquivo = evento.context().toString();

                 if (nomeArquivo.equals("teste.log")){
                     logWatcher.lerNovasLinhas("teste.log");
                 }
             }
             key.reset();
         }


        } catch (IOException | InterruptedException e  ) {
            e.printStackTrace();
        }
    }
}