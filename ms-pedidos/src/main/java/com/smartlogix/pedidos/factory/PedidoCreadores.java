package com.smartlogix.pedidos.factory;

import com.smartlogix.pedidos.entity.Pedido;
import com.smartlogix.pedidos.entity.Pedido.TipoPedido;
import org.springframework.stereotype.Component;

/**
 * Implementaciones concretas del patron Factory Method.
 * Cada clase crea un tipo especifico de Pedido con sus propias reglas de negocio.
 * Extienden PedidoCreadorBase para reutilizar la generacion del numero de pedido.
 */

@Component
class PedidoEstandarCreador extends PedidoCreadorBase {

    @Override
    public Pedido crearPedido(Long clienteId, String nombreCliente, String direccionEnvio) {
        Pedido pedido = new Pedido();
        pedido.setClienteId(clienteId);
        pedido.setNombreCliente(nombreCliente);
        pedido.setDireccionEnvio(direccionEnvio);
        pedido.setTipo(TipoPedido.ESTANDAR);
        pedido.setEstado(Pedido.EstadoPedido.PENDIENTE);
        pedido.setNumeroPedido(generarNumero("EST"));
        pedido.setObservaciones("Pedido estandar - envio en 5 a 7 dias habiles");
        return pedido;
    }

    @Override
    public TipoPedido getTipoPedido() {
        return TipoPedido.ESTANDAR;
    }
}

@Component
class PedidoExpressCreador extends PedidoCreadorBase {

    @Override
    public Pedido crearPedido(Long clienteId, String nombreCliente, String direccionEnvio) {
        Pedido pedido = new Pedido();
        pedido.setClienteId(clienteId);
        pedido.setNombreCliente(nombreCliente);
        pedido.setDireccionEnvio(direccionEnvio);
        pedido.setTipo(TipoPedido.EXPRESS);
        // Los pedidos express se confirman automaticamente
        pedido.setEstado(Pedido.EstadoPedido.CONFIRMADO);
        pedido.setNumeroPedido(generarNumero("EXP"));
        pedido.setObservaciones("Pedido express - entrega en 24 horas. Costo adicional aplica.");
        return pedido;
    }

    @Override
    public TipoPedido getTipoPedido() {
        return TipoPedido.EXPRESS;
    }
}

@Component
class PedidoMayoristaCreador extends PedidoCreadorBase {

    @Override
    public Pedido crearPedido(Long clienteId, String nombreCliente, String direccionEnvio) {
        Pedido pedido = new Pedido();
        pedido.setClienteId(clienteId);
        pedido.setNombreCliente(nombreCliente);
        pedido.setDireccionEnvio(direccionEnvio);
        pedido.setTipo(TipoPedido.MAYORISTA);
        // Mayorista requiere aprobacion manual
        pedido.setEstado(Pedido.EstadoPedido.PENDIENTE);
        pedido.setNumeroPedido(generarNumero("MAY"));
        pedido.setObservaciones("Pedido mayorista - requiere validacion de cupo de credito. Descuento del 15% aplicado.");
        return pedido;
    }

    @Override
    public TipoPedido getTipoPedido() {
        return TipoPedido.MAYORISTA;
    }
}
