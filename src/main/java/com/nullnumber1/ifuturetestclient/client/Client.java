package com.nullnumber1.ifuturetestclient.client;

import com.nullnumber1.ifuturetestclient.util.ConfigReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

@Component
public class Client {
    private final ConfigReader configReader;

    @Autowired
    public Client(ConfigReader configReader) {
        this.configReader = configReader;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        final Map<String, Integer> clients = configReader.readClients();
        int clientsRead = clients.get("rcount");
        int clientsWrite = clients.get("wcount");
        int idLowerBound = clients.get("idlower");
        int idUpperBound = clients.get("idupper");
        Random random = new Random();
        ExecutorService executorService = Executors.newFixedThreadPool(clientsRead + clientsWrite);
        WebClient client = WebClient.create();
        Runnable reader = () -> {
            while (true) {
                client.get()
                        .uri("http://localhost:8080/accounts/" + (int)((Math.random() * (idUpperBound - idLowerBound)) + idLowerBound))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        Runnable writer = () -> {
            while (true) {
                Map<String, Integer> body = new LinkedCaseInsensitiveMap<>();
                body.put("id", (int)((Math.random() * (idUpperBound - idLowerBound)) + idLowerBound));
                body.put("newValue", random.nextInt(2000) - 1000);
                client.post()
                        .uri("http://localhost:8080/accounts/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                body.clear();
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        for (int i = 0; i < clientsWrite; i++) {
            executorService.submit(writer);
        }
        for (int i = 0; i < clientsRead; i++) {
            executorService.submit(reader);
        }
    }
}
