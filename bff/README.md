# SmartLogix BFF (Backend For Frontend)

Componente que agrega y adapta las respuestas de los microservicios para el frontend.  
Puerto: **8080**

## Requisitos

- Java 17
- Maven 3.8+
- Microservicios `ms-inventario` (8081) y `ms-pedidos` (8082) en ejecución

## Ejecución

```bash
mvn spring-boot:run
```

## Endpoints principales

| Método | URL | Descripción |
|---|---|---|
| GET | `/bff/dashboard` | Resumen de productos y pedidos |
| GET | `/bff/productos` | Lista de productos |
| POST | `/bff/productos` | Crear producto |
| GET | `/bff/pedidos` | Lista de pedidos |
| POST | `/bff/pedidos` | Crear pedido |

## Circuit Breaker

Estado visible en: `http://localhost:8080/actuator/circuitbreakers`

Si el microservicio destino no responde, el BFF devuelve una respuesta de fallback con el campo `"error"`.
