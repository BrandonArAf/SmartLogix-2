package com.smartlogix.pedidos;

import com.smartlogix.pedidos.entity.Pedido;
import com.smartlogix.pedidos.entity.Pedido.EstadoPedido;
import com.smartlogix.pedidos.entity.Pedido.TipoPedido;
import com.smartlogix.pedidos.factory.PedidoFactory;
import com.smartlogix.pedidos.repository.PedidoRepository;
import com.smartlogix.pedidos.service.PedidosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidosServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoFactory pedidoFactory;

    @InjectMocks
    private PedidosService pedidosService;

    private Pedido pedidoBase;

    @BeforeEach
    void setUp() {
        pedidoBase = new Pedido();
        pedidoBase.setId(1L);
        pedidoBase.setClienteId(10L);
        pedidoBase.setNombreCliente("Empresa ABC Ltda");
        pedidoBase.setTipo(TipoPedido.ESTANDAR);
        pedidoBase.setEstado(EstadoPedido.PENDIENTE);
        pedidoBase.setNumeroPedido("EST-20240115120000");
    }

    @Test
    void crearPedido_estandar_usaFactoryYGuarda() {
        // Arrange
        when(pedidoFactory.crear(TipoPedido.ESTANDAR, 10L, "Empresa ABC Ltda", "Av. Test 123"))
                .thenReturn(pedidoBase);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoBase);

        // Act
        Pedido resultado = pedidosService.crearPedido(
                TipoPedido.ESTANDAR, 10L, "Empresa ABC Ltda", "Av. Test 123");

        // Assert
        assertNotNull(resultado);
        assertEquals(TipoPedido.ESTANDAR, resultado.getTipo());
        assertEquals(EstadoPedido.PENDIENTE, resultado.getEstado());
        verify(pedidoFactory, times(1)).crear(TipoPedido.ESTANDAR, 10L, "Empresa ABC Ltda", "Av. Test 123");
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void cambiarEstado_cuandoEsValido_actualizaEstado() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoBase));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoBase);

        Optional<Pedido> resultado = pedidosService.cambiarEstado(1L, EstadoPedido.CONFIRMADO);

        assertTrue(resultado.isPresent());
        assertEquals(EstadoPedido.CONFIRMADO, resultado.get().getEstado());
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void cambiarEstado_cuandoPedidoCancelado_lanzaExcepcion() {
        pedidoBase.setEstado(EstadoPedido.CANCELADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoBase));

        assertThrows(IllegalStateException.class, () ->
                pedidosService.cambiarEstado(1L, EstadoPedido.CONFIRMADO)
        );
    }

    @Test
    void cancelar_cuandoEstaEnviado_lanzaExcepcion() {
        pedidoBase.setEstado(EstadoPedido.ENVIADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoBase));

        assertThrows(IllegalStateException.class, () ->
                pedidosService.cancelar(1L)
        );
    }

    @Test
    void cancelar_cuandoEstaPendiente_retornaTrue() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoBase));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoBase);

        boolean resultado = pedidosService.cancelar(1L);

        assertTrue(resultado);
    }

    @Test
    void obtenerTodos_retornaListaDePedidos() {
        when(pedidoRepository.findAll()).thenReturn(List.of(pedidoBase));

        List<Pedido> resultado = pedidosService.obtenerTodos();

        assertEquals(1, resultado.size());
        assertEquals("EST-20240115120000", resultado.get(0).getNumeroPedido());
    }
}
