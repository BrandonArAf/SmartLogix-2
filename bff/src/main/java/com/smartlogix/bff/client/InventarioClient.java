package com.smartlogix.bff.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Cliente HTTP para comunicarse con el Microservicio de Inventario.
 * Implementa el patron Circuit Breaker con Resilience4j para manejar
 * caidas del microservicio sin afectar al BFF ni al frontend.
 */
@Slf4j
@Component
public class InventarioClient {

    private final RestTemplate restTemplate;
    private final String inventarioUrl;

    public InventarioClient(RestTemplate restTemplate,
                            @Value("${ms.inventario.url}") String inventarioUrl) {
        this.restTemplate = restTemplate;
        this.inventarioUrl = inventarioUrl;
    }

    /**
     * Obtiene todos los productos del inventario.
     * Si el microservicio falla, se activa el fallback.
     */
    @CircuitBreaker(name = "inventario", fallbackMethod = "fallbackGetProductos")
    public List<Map<String, Object>> getProductos() {
        return restTemplate.getForObject(inventarioUrl + "/api/productos", List.class);
    }

    /**
     * Obtiene un producto por su ID.
     */
    @CircuitBreaker(name = "inventario", fallbackMethod = "fallbackGetProducto")
    public Map<String, Object> getProductoPorId(Long id) {
        return restTemplate.getForObject(inventarioUrl + "/api/productos/" + id, Map.class);
    }

    /**
     * Crea un nuevo producto en el inventario.
     */
    @CircuitBreaker(name = "inventario", fallbackMethod = "fallbackCrearProducto")
    public Map<String, Object> crearProducto(Map<String, Object> producto) {
        return restTemplate.postForObject(inventarioUrl + "/api/productos", producto, Map.class);
    }

    /**
     * Metodo fallback: se ejecuta cuando el Circuit Breaker esta ABIERTO
     * o cuando ocurre una excepcion en el microservicio de inventario.
     */
    public List<Map<String, Object>> fallbackGetProductos(Exception ex) {
        log.warn("Circuit Breaker activado para inventario: {}", ex.getMessage());
        return Collections.singletonList(
                Map.of("error", "Servicio de inventario temporalmente no disponible",
                        "mensaje", "Por favor intente en unos momentos")
        );
    }

    public Map<String, Object> fallbackGetProducto(Long id, Exception ex) {
        return Map.of("error", "No se pudo obtener el producto con id: " + id,
                "detalle", "Servicio de inventario no disponible");
    }

    public Map<String, Object> fallbackCrearProducto(Map<String, Object> producto, Exception ex) {
        return Map.of("error", "No se pudo crear el producto",
                "detalle", "Servicio de inventario no disponible");
    }
}
