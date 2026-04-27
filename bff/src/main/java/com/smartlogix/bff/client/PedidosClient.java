package com.smartlogix.bff.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Cliente HTTP para el Microservicio de Pedidos.
 * Patron Circuit Breaker aplicado para tolerancia a fallos.
 */
@Component
public class PedidosClient {

    private final RestTemplate restTemplate;
    private final String pedidosUrl;

    public PedidosClient(RestTemplate restTemplate,
                         @Value("${ms.pedidos.url}") String pedidosUrl) {
        this.restTemplate = restTemplate;
        this.pedidosUrl = pedidosUrl;
    }

    @CircuitBreaker(name = "pedidos", fallbackMethod = "fallbackGetPedidos")
    public List<Map<String, Object>> getPedidos() {
        return restTemplate.getForObject(pedidosUrl + "/api/pedidos", List.class);
    }

    @CircuitBreaker(name = "pedidos", fallbackMethod = "fallbackGetPedido")
    public Map<String, Object> getPedidoPorId(Long id) {
        return restTemplate.getForObject(pedidosUrl + "/api/pedidos/" + id, Map.class);
    }

    @CircuitBreaker(name = "pedidos", fallbackMethod = "fallbackCrearPedido")
    public Map<String, Object> crearPedido(Map<String, Object> pedido) {
        return restTemplate.postForObject(pedidosUrl + "/api/pedidos", pedido, Map.class);
    }

    // Fallbacks
    public List<Map<String, Object>> fallbackGetPedidos(Exception ex) {
        System.out.println("Circuit Breaker activado para pedidos: " + ex.getMessage());
        return Collections.singletonList(
                Map.of("error", "Servicio de pedidos temporalmente no disponible",
                        "mensaje", "Por favor intente en unos momentos")
        );
    }

    public Map<String, Object> fallbackGetPedido(Long id, Exception ex) {
        return Map.of("error", "No se pudo obtener el pedido con id: " + id);
    }

    public Map<String, Object> fallbackCrearPedido(Map<String, Object> pedido, Exception ex) {
        return Map.of("error", "No se pudo crear el pedido",
                "detalle", "Servicio de pedidos no disponible");
    }
}
