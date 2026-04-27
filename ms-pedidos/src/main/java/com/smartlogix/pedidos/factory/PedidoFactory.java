package com.smartlogix.pedidos.factory;

import com.smartlogix.pedidos.entity.Pedido;
import com.smartlogix.pedidos.entity.Pedido.TipoPedido;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Fabrica central de Pedidos que utiliza el patron Factory Method.
 *
 * Esta clase actua como punto de entrada: recibe el tipo de pedido
 * deseado y delega la creacion al creador correcto.
 *
 * Ventaja: si se agrega un nuevo tipo de pedido, solo se crea una
 * nueva implementacion de PedidoCreador sin modificar esta clase
 * (principio Open/Closed).
 */
@Component
public class PedidoFactory {

    private final Map<TipoPedido, PedidoCreador> creadores;

    /**
     * Spring inyecta automaticamente todas las implementaciones de PedidoCreador.
     */
    public PedidoFactory(List<PedidoCreador> listaCreadores) {
        this.creadores = listaCreadores.stream()
                .collect(Collectors.toMap(PedidoCreador::getTipoPedido, c -> c));
    }

    /**
     * Crea un pedido del tipo indicado usando el creador correspondiente.
     *
     * @param tipo           Tipo de pedido (ESTANDAR, EXPRESS, MAYORISTA)
     * @param clienteId      ID del cliente
     * @param nombreCliente  Nombre del cliente
     * @param direccionEnvio Direccion de entrega
     * @return Pedido creado con las reglas de negocio aplicadas
     */
    public Pedido crear(TipoPedido tipo, Long clienteId, String nombreCliente, String direccionEnvio) {
        PedidoCreador creador = creadores.get(tipo);
        if (creador == null) {
            throw new IllegalArgumentException("Tipo de pedido no soportado: " + tipo);
        }
        return creador.crearPedido(clienteId, nombreCliente, direccionEnvio);
    }
}
