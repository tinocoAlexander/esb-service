package com.utd.ti.soa.esb_service.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.utd.ti.soa.esb_service.model.User;
import com.utd.ti.soa.esb_service.utils.Auth;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/esb")
public class ESBController {
    private final WebClient webClient = WebClient.create();
    private final Auth auth = new Auth();

    // Rutas para usuarios
    @PostMapping("/user")
    public ResponseEntity<String> createUser(@RequestBody User user, 
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("Request Body " + user);
        System.out.println("Token recibido " + token);
        
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }

        try {
            String response = webClient.post()
                    .uri("http://users.railway.internal:3000/app/users/create")
                    .body(BodyInserters.fromValue(user))
                    .retrieve()
                    .onStatus(HttpStatus::isError, clientResponse -> 
                        clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);

        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @GetMapping("/user/get")
    public ResponseEntity<String> getUsers(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }
        String response = webClient.get()
                .uri("http://users.railway.internal:3000/app/users/all")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> System.out.println("Error: " + error.getMessage()))
                .block();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/user/update/{id}")
    public ResponseEntity<String> updateUser(
            @PathVariable("id") Long id,
            @RequestBody String userPayload, 
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        System.out.println("Payload recibido: " + userPayload);
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }

        String response = webClient.patch()
                .uri("http://users.railway.internal:3000/app/users/update/" + id)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(userPayload)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> System.out.println("Error: " + error.getMessage()))
                .block();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable("id") Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }

        String response = webClient.delete()
                .uri("http://users.railway.internal:3000/app/users/delete/" + id)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> System.out.println("Error: " + error.getMessage()))
                .block();

        return ResponseEntity.ok(response);
    }

    // Rutas para clientes
    @PostMapping("/client")
    public ResponseEntity<String> createClient(@RequestBody Object client, 
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("Request Body " + client);
        System.out.println("Token recibido " + token);
        
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }
        
        try {
            String response = webClient.post()
                    .uri("http://clients.railway.internal:3000/app/clients/create")
                    .body(BodyInserters.fromValue(client))
                    .retrieve()
                    .onStatus(HttpStatus::isError, clientResponse -> 
                        clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                    .bodyToMono(String.class)
                    .block();
    
            return ResponseEntity.ok(response);
    
        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @GetMapping("/client/get")
    public ResponseEntity<String> getClients(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }
        String response = webClient.get()
                .uri("http://clients.railway.internal:3000/app/clients/all")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> System.out.println("Error: " + error.getMessage()))
                .block();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/client/update/{id}")
    public ResponseEntity<String> updateClient(
            @PathVariable("id") Long id,
            @RequestBody String clientPayload, 
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        System.out.println("Payload recibido: " + clientPayload);
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }
        
        String response = webClient.patch()
                .uri("http://clients.railway.internal:3000/app/clients/update/" + id)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(clientPayload)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> System.out.println("Error: " + error.getMessage()))
                .block();
    
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/client/delete/{id}")
    public ResponseEntity<String> deleteClient(
            @PathVariable("id") Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }
        
        String response = webClient.delete()
                .uri("http://clients.railway.internal:3000/app/clients/delete/" + id)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> System.out.println("Error: " + error.getMessage()))
                .block();
    
        return ResponseEntity.ok(response);
    }
}
