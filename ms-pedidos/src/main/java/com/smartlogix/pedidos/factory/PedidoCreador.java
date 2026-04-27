package com.smartlogix.pedidos.factory;

import com.smartlogix.pedidos.entity.Pedido;
import com.smartlogix.pedidos.entity.Pedido.TipoPedido;

/**
 * Interfaz base del patron Factory Method.
 *
 * El patron Factory Method permite crear objetos sin especificar
 * la clase exacta del objeto que se creara. Cada tipo de pedido
 * tiene su propia logica de creacion y validacion.
 */
public interface PedidoCreador {

    /**
     * Metodo fabrica: crea un Pedido del tipo correspondiente
     * con las reglas de negocio aplicadas segun su tipo.
     */
    Pedido crearPedido(Long clienteId, String nombreCliente, String direccionEnvio);

    /**
     * Retorna el tipo de pedido que este creador maneja.
     */
    TipoPedido getTipoPedido();
}
