package com.smartlogix.inventario.repository;

import com.smartlogix.inventario.entity.Producto;
import com.smartlogix.inventario.entity.Producto.EstadoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Producto.
 * Implementa el patron Repository para abstraer la capa de persistencia.
 *
 * Al extender JpaRepository, obtenemos automaticamente:
 * - save(), findById(), findAll(), deleteById(), etc.
 * Ademas definimos consultas personalizadas con @Query o metodos derivados.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Metodo derivado: Spring genera la query automaticamente
    List<Producto> findByEstado(EstadoProducto estado);

    List<Producto> findByCategoria(String categoria);

    Optional<Producto> findByCodigoSku(String codigoSku);

    // Productos con stock bajo (menor al minimo definido)
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo AND p.estado = :estado")
    List<Producto> findProductosConStockBajo(@Param("estado") EstadoProducto estado);

    // Busqueda por nombre (LIKE)
    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Producto> buscarPorNombre(@Param("nombre") String nombre);

    boolean existsByCodigoSku(String codigoSku);
}
