package com.logsentinel.alert;


import com.logsentinel.config.LogSentinelProperties;
import com.logsentinel.history.HistoryRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class SlackNotifier {

    private final RestTemplate restTemplate;
    private final LogSentinelProperties properties;
    private final HistoryRepository historyRepository;

    public SlackNotifier(RestTemplate restTemplate , LogSentinelProperties properties , HistoryRepository historyRepository){
        this.properties = properties;
        this.restTemplate = restTemplate;
        this.historyRepository = historyRepository;
    }

    public void enviarAlerta(String mensagem){
        Map<String,String> payload = new HashMap<>();
        payload.put("text" , mensagem);

        String webhookurl = properties.getSlack().getWebhookUrl();


        int tentativa = 0;
        boolean enviado = false;

        while(tentativa < 3 && !enviado){
            tentativa ++;
            try {
                restTemplate.postForObject(webhookurl, payload, String.class);
                enviado = true;
            }catch (RestClientException e){
                System.out.println("Tentativa " +tentativa + " falhou: " + e.getMessage());
                try {
                    Thread.sleep(1000 * tentativa);
                }catch (InterruptedException ie){
                    ie.printStackTrace();
                }
            }
        }

        if (enviado) {
            historyRepository.salvarAnomalia("alert", LocalDateTime.now().toString(), mensagem, "sent");
        }else
            historyRepository.salvarAnomalia("alert" , LocalDateTime.now().toString() , mensagem , "failed");
        }

    }




