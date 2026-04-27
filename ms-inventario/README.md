# Microservicio de Inventario

Gestiona el stock de productos de SmartLogix.  
Puerto: **8081** | BD: `smartlogix_inventario` (MySQL/XAMPP)

## Requisitos

- Java 17, Maven 3.8+
- XAMPP con MySQL activo

## Configuración de BD

1. Iniciar XAMPP (MySQL en puerto 3306)
2. Crear la base de datos:
```sql
CREATE DATABASE smartlogix_inventario CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

El esquema se genera automáticamente con `spring.jpa.hibernate.ddl-auto=update`.

## Ejecución

```bash
mvn spring-boot:run
```

## Endpoints

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/productos` | Listar todos |
| GET | `/api/productos/{id}` | Obtener por ID |
| POST | `/api/productos` | Crear producto |
| PUT | `/api/productos/{id}` | Actualizar producto |
| DELETE | `/api/productos/{id}` | Eliminar producto |
| PATCH | `/api/productos/{id}/stock` | Descontar stock `{"cantidad": 5}` |
| GET | `/api/productos/stock-bajo` | Productos bajo stock mínimo |
| GET | `/api/productos/buscar?nombre=X` | Buscar por nombre |

## Tests

```bash
mvn test
```
