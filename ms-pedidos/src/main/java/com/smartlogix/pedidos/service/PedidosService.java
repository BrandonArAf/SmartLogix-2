package com.smartlogix.pedidos.service;

import com.smartlogix.pedidos.entity.Pedido;
import com.smartlogix.pedidos.entity.Pedido.EstadoPedido;
import com.smartlogix.pedidos.entity.Pedido.TipoPedido;
import com.smartlogix.pedidos.factory.PedidoFactory;
import com.smartlogix.pedidos.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PedidosService {

    private final PedidoRepository pedidoRepository;
    private final PedidoFactory pedidoFactory;

    public PedidosService(PedidoRepository pedidoRepository, PedidoFactory pedidoFactory) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoFactory = pedidoFactory;
    }

    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    /**
     * Crea un pedido usando el Factory Method segun el tipo indicado.
     */
    @Transactional
    public Pedido crearPedido(TipoPedido tipo, Long clienteId,
                               String nombreCliente, String direccionEnvio) {
        // El Factory Method crea el pedido con las reglas de negocio correctas
        Pedido pedido = pedidoFactory.crear(tipo, clienteId, nombreCliente, direccionEnvio);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Optional<Pedido> cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        return pedidoRepository.findById(id).map(pedido -> {
            validarTransicionEstado(pedido.getEstado(), nuevoEstado);
            pedido.setEstado(nuevoEstado);
            return pedidoRepository.save(pedido);
        });
    }

    public List<Pedido> obtenerPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public List<Pedido> obtenerPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    @Transactional
    public boolean cancelar(Long id) {
        return pedidoRepository.findById(id).map(pedido -> {
            if (pedido.getEstado() == EstadoPedido.ENVIADO ||
                pedido.getEstado() == EstadoPedido.ENTREGADO) {
                throw new IllegalStateException("No se puede cancelar un pedido en estado: " + pedido.getEstado());
            }
            pedido.setEstado(EstadoPedido.CANCELADO);
            pedidoRepository.save(pedido);
            return true;
        }).orElse(false);
    }

    /**
     * Valida que la transicion de estado sea valida segun el flujo del negocio.
     */
    private void validarTransicionEstado(EstadoPedido actual, EstadoPedido nuevo) {
        if (actual == EstadoPedido.CANCELADO || actual == EstadoPedido.ENTREGADO) {
            throw new IllegalStateException(
                    "No se puede cambiar el estado de un pedido " + actual);
        }
    }
}
