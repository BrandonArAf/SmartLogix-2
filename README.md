# SmartLogix

Proyecto de sistema de gestión logística, inventario y pedidos. Desarrollado con arquitectura de microservicios usando Spring Boot para el backend y React para el frontend.

## Estructura del Proyecto

El sistema está dividido en los siguientes módulos:

- **ms-inventario**: Microservicio encargado de la gestión del catálogo de productos y control de stock. Expone servicios REST en el puerto 8081.
- **ms-pedidos**: Microservicio encargado del registro y seguimiento de pedidos de clientes. Expone servicios REST en el puerto 8082.
- **bff (Backend For Frontend)**: Patrón de diseño para agregar y orquestar las llamadas a los microservicios, manejando tolerancia a fallos mediante Circuit Breaker. Expone servicios REST en el puerto 8080.
- **frontend**: Aplicación cliente desarrollada en React, que interactúa directamente con el BFF.

## Requisitos Previos

- Java Development Kit (JDK) 17
- Apache Maven
- Node.js (v16 o superior) y npm
- Servidor de base de datos MySQL (por ejemplo, a través de XAMPP)

## Configuración de Base de Datos

Antes de ejecutar los microservicios, es necesario tener creadas las bases de datos en MySQL:

1. Base de datos para el microservicio de inventario: `ms-inventario`
2. Base de datos para el microservicio de pedidos: `ms-pedidos`

Los microservicios están configurados para conectarse al usuario `root` sin contraseña en `localhost:3306`. Las tablas se generarán automáticamente gracias a la propiedad `hibernate.ddl-auto=update`.

## Ejecución del Proyecto

### 1. Ejecutar Backends (Spring Boot)

En consolas independientes, navegar a cada directorio y ejecutar el comando de Maven:

```bash
# Iniciar ms-inventario
cd ms-inventario
mvn spring-boot:run

# Iniciar ms-pedidos
cd ms-pedidos
mvn spring-boot:run

# Iniciar BFF
cd bff
mvn spring-boot:run
```

### 2. Ejecutar Frontend (React)

Navegar al directorio de frontend, instalar las dependencias y ejecutar la aplicación:

```bash
cd frontend
npm install
npm start
```

La aplicación estará disponible en `http://localhost:3000`.
