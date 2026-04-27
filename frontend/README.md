# SmartLogix Frontend

Componente React para la gestión de inventario y pedidos de SmartLogix.

## Requisitos previos

- Node.js 18+
- BFF corriendo en `http://localhost:8080`

## Instalación y ejecución

```bash
npm install
npm start
```

La app abre en http://localhost:3000

## Build para producción

```bash
npm run build
```

## Variables de entorno

Crear `.env` en la raíz:

```
REACT_APP_BFF_URL=http://localhost:8080/bff
```

## Estructura

```
src/
├── components/
│   ├── InventarioPanel.jsx   # Gestión de stock
│   └── PedidosPanel.jsx      # Gestión de pedidos
├── services/
│   └── api.js                # Llamadas HTTP al BFF
├── App.js
└── index.js
```
