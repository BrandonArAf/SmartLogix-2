package com.smartlogix.bff;

import com.smartlogix.bff.client.InventarioClient;
import com.smartlogix.bff.client.PedidosClient;
import com.smartlogix.bff.controller.BffController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BffControllerTest {

    @Mock
    private InventarioClient inventarioClient;

    @Mock
    private PedidosClient pedidosClient;

    @InjectMocks
    private BffController bffController;

    private List<Map<String, Object>> productosEsperados;
    private List<Map<String, Object>> pedidosEsperados;

    @BeforeEach
    void setUp() {
        productosEsperados = List.of(
                Map.of("id", 1L, "nombre", "Caja 50x50", "stock", 100),
                Map.of("id", 2L, "nombre", "Pallet madera", "stock", 30)
        );
        pedidosEsperados = List.of(
                Map.of("id", 1L, "estado", "PENDIENTE", "clienteId", 5L)
        );
    }

    @Test
    void getDashboard_retornaProductosYPedidos() {
        // Arrange
        when(inventarioClient.getProductos()).thenReturn(productosEsperados);
        when(pedidosClient.getPedidos()).thenReturn(pedidosEsperados);

        // Act
        ResponseEntity<Map<String, Object>> response = bffController.getDashboard();

        // Assert
        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().containsKey("productos"));
        assertTrue(response.getBody().containsKey("pedidos"));
        verify(inventarioClient, times(1)).getProductos();
        verify(pedidosClient, times(1)).getPedidos();
    }

    @Test
    void getProductos_retornaListaDeProductos() {
        when(inventarioClient.getProductos()).thenReturn(productosEsperados);

        ResponseEntity<List<Map<String, Object>>> response = bffController.getProductos();

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getPedidos_retornaListaDePedidos() {
        when(pedidosClient.getPedidos()).thenReturn(pedidosEsperados);

        ResponseEntity<List<Map<String, Object>>> response = bffController.getPedidos();

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void fallbackInventario_retornaMensajeDeError() {
        // Simula que el Circuit Breaker activa el fallback
        when(inventarioClient.getProductos()).thenReturn(
                List.of(Map.of("error", "Servicio de inventario temporalmente no disponible"))
        );

        ResponseEntity<List<Map<String, Object>>> response = bffController.getProductos();

        assertNotNull(response.getBody());
        assertTrue(response.getBody().get(0).containsKey("error"));
    }
}
