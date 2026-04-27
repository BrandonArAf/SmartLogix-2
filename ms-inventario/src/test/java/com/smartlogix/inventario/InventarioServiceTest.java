package com.smartlogix.inventario;

import com.smartlogix.inventario.entity.Producto;
import com.smartlogix.inventario.entity.Producto.EstadoProducto;
import com.smartlogix.inventario.repository.ProductoRepository;
import com.smartlogix.inventario.service.InventarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private Producto productoBase;

    @BeforeEach
    void setUp() {
        productoBase = new Producto();
        productoBase.setId(1L);
        productoBase.setNombre("Pallet de madera");
        productoBase.setPrecio(new BigDecimal("15990"));
        productoBase.setStock(50);
        productoBase.setStockMinimo(5);
        productoBase.setEstado(EstadoProducto.ACTIVO);
        productoBase.setCodigoSku("PAL-001");
    }

    @Test
    void obtenerTodos_retornaListaDeProductos() {
        when(productoRepository.findAll()).thenReturn(List.of(productoBase));

        List<Producto> resultado = inventarioService.obtenerTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Pallet de madera", resultado.get(0).getNombre());
    }

    @Test
    void obtenerPorId_cuandoExiste_retornaProducto() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoBase));

        Optional<Producto> resultado = inventarioService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("PAL-001", resultado.get().getCodigoSku());
    }

    @Test
    void obtenerPorId_cuandoNoExiste_retornaVacio() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Producto> resultado = inventarioService.obtenerPorId(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void descontarStock_cuandoHayStock_actualizaCorrectamente() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoBase));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoBase);

        Optional<Producto> resultado = inventarioService.descontarStock(1L, 10);

        assertTrue(resultado.isPresent());
        assertEquals(40, resultado.get().getStock());
    }

    @Test
    void descontarStock_cuandoNoHayStock_lanzaExcepcion() {
        productoBase.setStock(5);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoBase));

        assertThrows(IllegalStateException.class, () ->
                inventarioService.descontarStock(1L, 10)
        );
    }

    @Test
    void guardar_cuandoSkuDuplicado_lanzaExcepcion() {
        Producto nuevo = new Producto();
        nuevo.setCodigoSku("PAL-001"); // SKU ya existente

        when(productoRepository.existsByCodigoSku("PAL-001")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                inventarioService.guardar(nuevo)
        );
    }

    @Test
    void eliminar_cuandoExiste_retornaTrue() {
        when(productoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1L);

        boolean resultado = inventarioService.eliminar(1L);

        assertTrue(resultado);
        verify(productoRepository).deleteById(1L);
    }
}
