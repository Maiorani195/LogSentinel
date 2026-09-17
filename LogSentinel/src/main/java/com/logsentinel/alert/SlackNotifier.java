package com.logsentinel.alert;


import com.logsentinel.config.LogSentinelProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class SlackNotifier {

    private final RestTemplate restTemplate;
    private final LogSentinelProperties properties;

    public SlackNotifier(RestTemplate restTemplate , LogSentinelProperties properties){
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    public void enviarAlerta(String mensagem){
        Map<String,String> payload = new HashMap<>();
        payload.put("text" , mensagem);

        String webhookurl = properties.getSlack().getWebhookUrl();

        restTemplate.postForObject(webhookurl,payload, String.class);
    }



}
