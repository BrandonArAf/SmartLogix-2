package com.smartlogix.pedidos.factory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

abstract class PedidoCreadorBase implements PedidoCreador {

    protected String generarNumero(String prefijo) {
        return prefijo + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
