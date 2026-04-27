import axios from 'axios';

// URL base del BFF
const BFF_URL = process.env.REACT_APP_BFF_URL || 'http://localhost:8080/bff';

const api = axios.create({
  baseURL: BFF_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para manejar errores globalmente
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('Error en llamada al BFF:', error.message);
    return Promise.reject(error);
  }
);

// ===== PRODUCTOS / INVENTARIO =====
export const productosService = {
  getAll: () => api.get('/productos'),
  getById: (id) => api.get(`/productos/${id}`),
  create: (producto) => api.post('/productos', producto),
  update: (id, producto) => api.put(`/productos/${id}`, producto),
  delete: (id) => api.delete(`/productos/${id}`),
  descontarStock: (id, cantidad) => api.patch(`/productos/${id}/stock`, { cantidad }),
  getStockBajo: () => api.get('/productos/stock-bajo'),
};

// ===== PEDIDOS =====
export const pedidosService = {
  getAll: () => api.get('/pedidos'),
  getById: (id) => api.get(`/pedidos/${id}`),
  create: (pedido) => api.post('/pedidos', pedido),
  cambiarEstado: (id, estado) => api.patch(`/pedidos/${id}/estado`, { estado }),
  cancelar: (id) => api.delete(`/pedidos/${id}`),
  getByCliente: (clienteId) => api.get(`/pedidos/cliente/${clienteId}`),
};

// ===== DASHBOARD =====
export const dashboardService = {
  get: () => api.get('/dashboard'),
};

export default api;
