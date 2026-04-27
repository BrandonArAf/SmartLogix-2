package com.smartlogix.inventario.service;

import com.smartlogix.inventario.entity.Producto;
import com.smartlogix.inventario.entity.Producto.EstadoProducto;
import com.smartlogix.inventario.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de logica de negocio para el modulo de Inventario.
 * Se encarga de orquestar las operaciones sobre el repositorio
 * y aplicar las reglas de negocio necesarias.
 */
@Service
public class InventarioService {

    private final ProductoRepository productoRepository;

    public InventarioService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    @Transactional
    public Producto guardar(Producto producto) {
        if (producto.getCodigoSku() != null &&
            productoRepository.existsByCodigoSku(producto.getCodigoSku()) &&
            producto.getId() == null) {
            throw new IllegalArgumentException("Ya existe un producto con el SKU: " + producto.getCodigoSku());
        }
        return productoRepository.save(producto);
    }

    @Transactional
    public Optional<Producto> actualizar(Long id, Producto productoActualizado) {
        return productoRepository.findById(id).map(existente -> {
            existente.setNombre(productoActualizado.getNombre());
            existente.setDescripcion(productoActualizado.getDescripcion());
            existente.setPrecio(productoActualizado.getPrecio());
            existente.setStock(productoActualizado.getStock());
            existente.setCategoria(productoActualizado.getCategoria());
            existente.setEstado(productoActualizado.getEstado());
            return productoRepository.save(existente);
        });
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Reduce el stock de un producto al procesar un pedido.
     * Verifica que haya suficiente stock antes de descontar.
     */
    @Transactional
    public Optional<Producto> descontarStock(Long productoId, int cantidad) {
        return productoRepository.findById(productoId).map(producto -> {
            if (producto.getStock() < cantidad) {
                throw new IllegalStateException("Stock insuficiente. Disponible: " + producto.getStock());
            }
            producto.setStock(producto.getStock() - cantidad);
            if (producto.getStock() == 0) {
                producto.setEstado(EstadoProducto.AGOTADO);
            }
            return productoRepository.save(producto);
        });
    }

    public List<Producto> obtenerConStockBajo() {
        return productoRepository.findProductosConStockBajo();
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.buscarPorNombre(nombre);
    }
}
