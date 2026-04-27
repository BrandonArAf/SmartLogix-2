package com.smartlogix.pedidos.repository;

import com.smartlogix.pedidos.entity.Pedido;
import com.smartlogix.pedidos.entity.Pedido.EstadoPedido;
import com.smartlogix.pedidos.entity.Pedido.TipoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para Pedido.
 * Patron Repository: abstrae el acceso a la base de datos smartlogix_pedidos.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByTipo(TipoPedido tipo);

    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    @Query("SELECT p FROM Pedido p WHERE p.fechaCreacion BETWEEN :inicio AND :fin")
    List<Pedido> findByRangoFecha(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado = :estado")
    Long contarPorEstado(@Param("estado") EstadoPedido estado);
}
