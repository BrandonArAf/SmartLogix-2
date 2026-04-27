package com.smartlogix.pedidos.controller;

import com.smartlogix.pedidos.entity.Pedido;
import com.smartlogix.pedidos.entity.Pedido.EstadoPedido;
import com.smartlogix.pedidos.entity.Pedido.TipoPedido;
import com.smartlogix.pedidos.service.PedidosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST del Microservicio de Pedidos.
 * Puerto: 8082
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidosController {

    private final PedidosService pedidosService;

    public PedidosController(PedidosService pedidosService) {
        this.pedidosService = pedidosService;
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidos() {
        return ResponseEntity.ok(pedidosService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPedido(@PathVariable Long id) {
        return pedidosService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea un nuevo pedido usando Factory Method.
     * Body: { "tipo": "EXPRESS", "clienteId": 5, "nombreCliente": "Juan Perez", "direccionEnvio": "..." }
     */
    @PostMapping
    public ResponseEntity<Pedido> crearPedido(@RequestBody Map<String, Object> body) {
        try {
            TipoPedido tipo = TipoPedido.valueOf(body.get("tipo").toString());
            Long clienteId = Long.valueOf(body.get("clienteId").toString());
            String nombreCliente = body.get("nombreCliente").toString();
            String direccionEnvio = body.get("direccionEnvio").toString();

            Pedido creado = pedidosService.crearPedido(tipo, clienteId, nombreCliente, direccionEnvio);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Pedido> cambiarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            EstadoPedido nuevoEstado = EstadoPedido.valueOf(body.get("estado"));
            return pedidosService.cambiarEstado(id, nuevoEstado)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarPedido(@PathVariable Long id) {
        try {
            if (pedidosService.cancelar(id)) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pedido>> pedidosPorEstado(@PathVariable String estado) {
        try {
            EstadoPedido estadoEnum = EstadoPedido.valueOf(estado.toUpperCase());
            return ResponseEntity.ok(pedidosService.obtenerPorEstado(estadoEnum));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> pedidosPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidosService.obtenerPorCliente(clienteId));
    }
}
