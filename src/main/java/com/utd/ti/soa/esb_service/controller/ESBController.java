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

    // ---------------------- USUARIOS ----------------------

    @PostMapping("/user")
    public ResponseEntity<String> createUser(@RequestBody User user,
                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("Request Body " + user);
        System.out.println("Token recibido " + token);

        try {
            String response = webClient.post()
                    .uri("http://users.railway.internal:3000/app/users/create")
                    .body(BodyInserters.fromValue(user))
                    .retrieve()
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

        try {
            String response = webClient.get()
                    .uri("http://users.railway.internal:3000/app/users/all")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);

        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @PatchMapping("/user/update/{id}")
    public ResponseEntity<String> updateUser(@PathVariable("id") Long id,
                                            @RequestBody String userPayload,
                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        System.out.println("Payload recibido: " + userPayload);
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }

        try {
            String response = webClient.patch()
                    .uri("http://users.railway.internal:3000/app/users/update/" + id)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(userPayload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);

        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id,
                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }

        try {
            String response = webClient.delete()
                    .uri("http://users.railway.internal:3000/app/users/delete/" + id)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);

        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @PostMapping("/user/recover")
    public ResponseEntity<String> recoverPassword(@RequestBody String payload) {
        try {
            String response = webClient.post()
                    .uri("http://users.railway.internal:3000/app/users/recover")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);

        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<String> loginUser(@RequestBody String payload) {
        try {
            String response = webClient.post()
                    .uri("http://users.railway.internal:3000/app/users/login") // <--- CAMBIADO
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);

        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    // ---------------------- CLIENTES ----------------------

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

        try {
            String response = webClient.get()
                    .uri("http://clients.railway.internal:3000/app/clients/all")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);

        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @PatchMapping("/client/update/{id}")
    public ResponseEntity<String> updateClient(@PathVariable("id") Long id,
                                              @RequestBody String clientPayload,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        System.out.println("Payload recibido: " + clientPayload);
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }

        try {
            String response = webClient.patch()
                    .uri("http://clients.railway.internal:3000/app/clients/update/" + id)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(clientPayload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);

        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @DeleteMapping("/client/delete/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable("id") Long id,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }

        try {
            String response = webClient.delete()
                    .uri("http://clients.railway.internal:3000/app/clients/delete/" + id)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);

        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    // ---------------------- PRODUCTOS ----------------------

    @PostMapping("/product")
    public ResponseEntity<String> createProduct(@RequestBody Object product,
                                               @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        System.out.println("Request Body " + product);
        System.out.println("Token recibido " + token);

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }

        try {
            String response = webClient.post()
                    .uri("http://products.railway.internal:3000/app/products/create")
                    .body(BodyInserters.fromValue(product))
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

    @GetMapping("/product/get")
    public ResponseEntity<String> getProducts(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }

        try {
            String response = webClient.get()
                    .uri("http://products.railway.internal:3000/app/products/all")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);

        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @PatchMapping("/product/update/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable("id") Long id,
                                               @RequestBody String productPayload,
                                               @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        System.out.println("Payload recibido: " + productPayload);
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }

        try {
            String response = webClient.patch()
                    .uri("http://products.railway.internal:3000/app/products/update/" + id)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(productPayload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);

        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @DeleteMapping("/product/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id,
                                               @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }

        try {
            String response = webClient.delete()
                    .uri("http://products.railway.internal:3000/app/products/delete/" + id)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);

        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    // ---------------------- ÓRDENES ----------------------

    @PostMapping("/order")
    public ResponseEntity<String> createOrder(@RequestBody Object order,
                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }

        try {
            String response = webClient.post()
                    .uri("http://orders.railway.internal:3000/app/orders/create")
                    .body(BodyInserters.fromValue(order))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);

        } catch (WebClientResponseException e) {
            // Aquí capturamos el error y lo regresamos al cliente
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<String> getOrderById(@PathVariable("id") Long id,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }

        try {
            String response = webClient.get()
                    .uri("http://orders.railway.internal:3000/app/orders/" + id)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);

        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @GetMapping("/order/client/{clientId}")
    public ResponseEntity<String> getOrdersByClient(@PathVariable("clientId") Long clientId,
                                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }

        try {
            String response = webClient.get()
                    .uri("http://orders.railway.internal:3000/app/orders/client/" + clientId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);

        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @PatchMapping("/order/status/{id}")
    public ResponseEntity<String> updateOrderStatus(@PathVariable("id") Long id,
                                                   @RequestBody String payload,
                                                   @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }

        try {
            String response = webClient.patch()
                    .uri("http://orders.railway.internal:3000/app/orders/status/" + id)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);

        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @DeleteMapping("/order/{id}")
    public ResponseEntity<String> cancelOrder(@PathVariable("id") Long id,
                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }

        try {
            String response = webClient.delete()
                    .uri("http://orders.railway.internal:3000/app/orders/" + id)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);

        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        }
    }

}