package com.smartlogix.bff.controller;

import com.smartlogix.bff.client.InventarioClient;
import com.smartlogix.bff.client.PedidosClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador principal del BFF (Backend For Frontend).
 * Agrega y adapta las respuestas de los microservicios para el frontend.
 * Puerto: 8080
 */
@RestController
@RequestMapping("/bff")
public class BffController {

    private final InventarioClient inventarioClient;
    private final PedidosClient pedidosClient;

    public BffController(InventarioClient inventarioClient, PedidosClient pedidosClient) {
        this.inventarioClient = inventarioClient;
        this.pedidosClient = pedidosClient;
    }

    /**
     * Endpoint del dashboard: agrega datos de inventario y pedidos en una sola respuesta.
     * Esto evita que el frontend haga multiples llamadas.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("productos", inventarioClient.getProductos());
        dashboard.put("pedidos", pedidosClient.getPedidos());
        return ResponseEntity.ok(dashboard);
    }

    // ===== INVENTARIO =====

    @GetMapping("/productos")
    public ResponseEntity<List<Map<String, Object>>> getProductos() {
        return ResponseEntity.ok(inventarioClient.getProductos());
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<Map<String, Object>> getProducto(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioClient.getProductoPorId(id));
    }

    @PostMapping("/productos")
    public ResponseEntity<Map<String, Object>> crearProducto(@RequestBody Map<String, Object> producto) {
        return ResponseEntity.ok(inventarioClient.crearProducto(producto));
    }

    // ===== PEDIDOS =====

    @GetMapping("/pedidos")
    public ResponseEntity<List<Map<String, Object>>> getPedidos() {
        return ResponseEntity.ok(pedidosClient.getPedidos());
    }

    @GetMapping("/pedidos/{id}")
    public ResponseEntity<Map<String, Object>> getPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidosClient.getPedidoPorId(id));
    }

    @PostMapping("/pedidos")
    public ResponseEntity<Map<String, Object>> crearPedido(@RequestBody Map<String, Object> pedido) {
        return ResponseEntity.ok(pedidosClient.crearPedido(pedido));
    }
}
