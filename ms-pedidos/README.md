# Microservicio de Pedidos

Gestiona el ciclo de vida de los pedidos de SmartLogix con Factory Method.  
Puerto: **8082** | BD: `smartlogix_pedidos` (MySQL/XAMPP)

## Requisitos

- Java 17, Maven 3.8+
- XAMPP con MySQL activo

## Configuración de BD

```sql
CREATE DATABASE smartlogix_pedidos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Ejecución

```bash
mvn spring-boot:run
```

## Endpoints

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/pedidos` | Listar todos |
| GET | `/api/pedidos/{id}` | Obtener por ID |
| POST | `/api/pedidos` | Crear pedido (usa Factory Method) |
| PATCH | `/api/pedidos/{id}/estado` | Cambiar estado |
| DELETE | `/api/pedidos/{id}` | Cancelar pedido |
| GET | `/api/pedidos/estado/{estado}` | Filtrar por estado |
| GET | `/api/pedidos/cliente/{id}` | Pedidos de un cliente |

## Crear pedido — Body ejemplo

```json
{
  "tipo": "EXPRESS",
  "clienteId": 5,
  "nombreCliente": "Distribuidora Norte Ltda",
  "direccionEnvio": "Av. Independencia 452, Santiago"
}
```

Tipos disponibles: `ESTANDAR`, `EXPRESS`, `MAYORISTA`

## Tests

```bash
mvn test
```
