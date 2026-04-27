# Análisis de Patrones de Diseño y Arquetipos Maven
## SmartLogix — Parcial N°2 | DSY1106 Desarrollo Fullstack III

---

## 1. Introducción

El presente documento describe y justifica los patrones de diseño y arquetipos Maven seleccionados para la implementación del sistema SmartLogix en su segunda fase de desarrollo. Esta etapa continúa directamente desde la arquitectura definida en el Parcial N°1, materializando los componentes frontend y backend de manera funcional.

La solución está compuesta por tres componentes backend independientes —un Backend For Frontend (BFF) y dos microservicios— más un componente frontend React empaquetado como módulo NPM.

---

## 2. Arquetipos Maven Utilizados

Un arquetipo Maven es una plantilla de proyecto que define la estructura estándar de directorios, dependencias y configuración inicial. En SmartLogix se utilizó el arquetipo `spring-boot-starter-parent` de Spring Boot como base para los tres componentes backend.

### 2.1 Arquetipo Base: spring-boot-starter-parent (v3.2.1)

**Descripción:** Arquetipo oficial de Spring Boot que provee gestión de dependencias, plugins de construcción y configuración por defecto para proyectos Java modernos con Java 17.

**Justificación de uso:**
- Elimina la necesidad de declarar versiones individuales para cada dependencia de Spring, reduciendo conflictos de compatibilidad.
- Integra el plugin `spring-boot-maven-plugin` que genera JARs ejecutables directamente, simplificando el despliegue.
- Provee configuración de compilación optimizada para Java 17, incluyendo soporte para records y sealed classes.
- Es el estándar de la industria para microservicios Java, con amplia documentación y soporte de la comunidad.

### 2.2 Configuración por Componente

| Componente | Artefacto Maven | Puerto | Base de Datos |
|---|---|---|---|
| BFF | `smartlogix-bff` | 8080 | — (no tiene BD propia) |
| MS Inventario | `ms-inventario` | 8081 | `smartlogix_inventario` |
| MS Pedidos | `ms-pedidos` | 8082 | `smartlogix_pedidos` |

Cada componente es un proyecto Maven independiente con su propio `pom.xml`, siguiendo el patrón **Database per Service** definido en el Parcial N°1: ningún microservicio comparte base de datos con otro.

---

## 3. Patrones de Diseño Implementados

### 3.1 Repository Pattern

**¿Dónde se aplica?** En ambos microservicios: `ProductoRepository` y `PedidoRepository`.

**Descripción técnica:**
El patrón Repository actúa como una capa de abstracción entre la lógica de negocio y el mecanismo de persistencia. En lugar de que el `Service` ejecute consultas SQL directamente, lo hace a través de interfaces del repositorio que Spring Data JPA implementa automáticamente en tiempo de ejecución.

```java
// En lugar de SQL directo:
// "SELECT * FROM productos WHERE stock <= stock_minimo"

// El Repository abstrae esto como:
@Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo AND p.estado = 'ACTIVO'")
List<Producto> findProductosConStockBajo();
```

**Beneficios en SmartLogix:**
- **Desacoplamiento:** Si se cambia de MySQL a PostgreSQL, solo se modifica el `application.properties`; la lógica de negocio en los `Service` no se toca.
- **Testabilidad:** Los tests unitarios usan `@Mock` sobre el repositorio, sin necesidad de una base de datos real.
- **Reutilización:** Los métodos derivados de Spring Data (`findByEstado`, `findByClienteId`) se generan automáticamente, eliminando código repetitivo.

**Justificación de selección:** Como se propuso en el Parcial N°1, este patrón es esencial para garantizar que la lógica de negocio sea independiente del motor de persistencia, facilitando pruebas y mantenimiento futuro.

---

### 3.2 Factory Method

**¿Dónde se aplica?** En el Microservicio de Pedidos: clases `PedidoCreador`, `PedidoEstandarCreador`, `PedidoExpressCreador`, `PedidoMayoristaCreador` y `PedidoFactory`.

**Descripción técnica:**
El patrón Factory Method define una interfaz para crear un objeto (`PedidoCreador`), pero deja a las subclases decidir qué clase instanciar. Cada tipo de pedido en SmartLogix tiene reglas de negocio distintas:

| Tipo | Estado inicial | Comportamiento especial |
|---|---|---|
| ESTANDAR | PENDIENTE | Envío en 5-7 días hábiles |
| EXPRESS | CONFIRMADO | Se confirma automáticamente; costo adicional |
| MAYORISTA | PENDIENTE | Requiere validación de crédito; descuento 15% |

```java
// El cliente (PedidosService) solo llama:
Pedido pedido = pedidoFactory.crear(TipoPedido.EXPRESS, clienteId, nombre, direccion);
// No necesita saber QUÉ clase concreta crea el pedido Express
```

**Beneficios en SmartLogix:**
- **Extensibilidad (Open/Closed):** Si SmartLogix necesita un nuevo tipo de pedido (ej. `INTERNACIONAL`), se crea una nueva clase que implemente `PedidoCreador` sin modificar `PedidoFactory` ni `PedidosService`.
- **Centralización:** Las reglas de creación de cada tipo de pedido están encapsuladas en su propia clase, no dispersas en el Service.
- **Inyección por Spring:** Las implementaciones concretas están anotadas con `@Component`, y Spring las inyecta automáticamente como una lista en `PedidoFactory`.

**Justificación de selección:** Tal como se estableció en la arquitectura del Parcial N°1, el Factory Method es el patrón adecuado para centralizar la creación de objetos de dominio complejos que varían según el tipo de cliente o canal de venta.

---

### 3.3 Circuit Breaker (Resilience4j)

**¿Dónde se aplica?** En el BFF: clases `InventarioClient` y `PedidosClient`, mediante la anotación `@CircuitBreaker`.

**Descripción técnica:**
El Circuit Breaker actúa como un "interruptor" que protege el BFF de fallos en cascada cuando un microservicio no responde. Tiene tres estados:

- **CLOSED (normal):** Las llamadas pasan normalmente. Si la tasa de fallos supera el 50% en una ventana de 10 llamadas, el circuito se ABRE.
- **OPEN (fallo detectado):** Las llamadas al microservicio son bloqueadas durante 5 segundos. Se retorna directamente el método `fallback`, evitando timeouts.
- **HALF_OPEN (prueba):** Después de 5 segundos, se permiten 3 llamadas de prueba. Si tienen éxito, el circuito vuelve a CLOSED.

```java
@CircuitBreaker(name = "inventario", fallbackMethod = "fallbackGetProductos")
public List<Map<String, Object>> getProductos() {
    return restTemplate.getForObject(inventarioUrl + "/api/productos", List.class);
}

// Se ejecuta si el circuito está abierto o si hay excepción
public List<Map<String, Object>> fallbackGetProductos(Exception ex) {
    return List.of(Map.of("error", "Servicio de inventario temporalmente no disponible"));
}
```

**Beneficios en SmartLogix:**
- **Resiliencia:** Si el microservicio de Inventario cae, el BFF retorna una respuesta de fallback en lugar de propagarle el error al frontend.
- **Visibilidad:** Mediante Spring Actuator (`/actuator/circuitbreakers`), se puede monitorear el estado del circuito en tiempo real.
- **Autorecuperación:** La transición automática a HALF_OPEN permite que el sistema se recupere sin intervención manual.

**Justificación de selección:** Directamente heredado de la propuesta del Parcial N°1, donde se identificó el Circuit Breaker como esencial para prevenir cascada de fallos entre microservicios.

---

## 4. Relación entre Patrones

Los tres patrones trabajan en capas complementarias:

```
Frontend React
      ↓
  BFF (Circuit Breaker) ← protege contra fallos de red
      ↓
Microservicios (Factory Method + Repository Pattern)
      ↓
  MySQL (XAMPP)
```

- El **Circuit Breaker** opera en la capa de comunicación entre BFF y microservicios.
- El **Factory Method** opera en la capa de lógica de negocio del microservicio de Pedidos.
- El **Repository Pattern** opera en la capa de acceso a datos de ambos microservicios.

Esta separación respeta el principio de responsabilidad única: cada patrón resuelve un problema específico y distinto.

---

## 5. Conclusión

Los patrones seleccionados responden directamente a los problemas identificados en SmartLogix: el Repository Pattern garantiza que la persistencia sea intercambiable y testeable; el Factory Method centraliza la complejidad de crear diferentes tipos de pedidos; y el Circuit Breaker protege la disponibilidad del sistema ante fallos parciales. En conjunto, producen una arquitectura modular, mantenible y resiliente.
