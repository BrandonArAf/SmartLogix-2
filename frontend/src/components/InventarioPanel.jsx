import React, { useState, useEffect } from 'react';
import { productosService } from '../services/api';

/**
 * Componente de gestión de inventario.
 * Permite listar, crear y eliminar productos del stock.
 */
function InventarioPanel() {
  const [productos, setProductos] = useState([]);
  const [cargando, setCargando] = useState(false);
  const [error, setError] = useState(null);
  const [mostrarFormulario, setMostrarFormulario] = useState(false);
  const [busqueda, setBusqueda] = useState('');

  const [nuevoProducto, setNuevoProducto] = useState({
    nombre: '',
    descripcion: '',
    precio: '',
    stock: '',
    stockMinimo: 5,
    categoria: '',
    codigoSku: '',
  });

  useEffect(() => {
    cargarProductos();
  }, []);

  const cargarProductos = async () => {
    setCargando(true);
    setError(null);
    try {
      const { data } = await productosService.getAll();
      // Si el circuit breaker retorna error, mostrarlo
      if (Array.isArray(data) && data[0]?.error) {
        setError(data[0].error);
        setProductos([]);
      } else {
        setProductos(data);
      }
    } catch (err) {
      setError('No se pudo conectar con el servicio de inventario.');
    } finally {
      setCargando(false);
    }
  };

  const handleCrear = async (e) => {
    e.preventDefault();
    try {
      const { data } = await productosService.create({
        ...nuevoProducto,
        precio: parseFloat(nuevoProducto.precio),
        stock: parseInt(nuevoProducto.stock),
      });
      
      if (data && data.error) {
        setError(data.error);
      } else {
        setMostrarFormulario(false);
        setNuevoProducto({ nombre: '', descripcion: '', precio: '', stock: '', stockMinimo: 5, categoria: '', codigoSku: '' });
        cargarProductos();
      }
    } catch (err) {
      setError('Error al crear el producto.');
    }
  };

  const handleEliminar = async (id) => {
    if (!window.confirm('¿Eliminar este producto?')) return;
    try {
      await productosService.delete(id);
      cargarProductos();
    } catch (err) {
      setError('Error al eliminar el producto.');
    }
  };

  const productosFiltrados = productos.filter((p) =>
    p.nombre?.toLowerCase().includes(busqueda.toLowerCase()) ||
    p.codigoSku?.toLowerCase().includes(busqueda.toLowerCase())
  );

  const getEstadoBadge = (estado) => {
    const colores = { ACTIVO: '#28a745', AGOTADO: '#dc3545', INACTIVO: '#6c757d' };
    return (
      <span style={{
        backgroundColor: colores[estado] || '#6c757d',
        color: '#fff', padding: '2px 8px',
        borderRadius: '12px', fontSize: '12px'
      }}>
        {estado}
      </span>
    );
  };

  return (
    <div style={{ padding: '20px' }}>
      {/* Cabecera */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h2 style={{ margin: 0, color: '#2c3e50' }}>📦 Gestión de Inventario</h2>
        <button
          onClick={() => setMostrarFormulario(!mostrarFormulario)}
          style={estilos.botonPrimario}
        >
          {mostrarFormulario ? '✕ Cancelar' : '+ Nuevo Producto'}
        </button>
      </div>

      {/* Buscador */}
      <input
        type="text"
        placeholder="Buscar por nombre o SKU..."
        value={busqueda}
        onChange={(e) => setBusqueda(e.target.value)}
        style={estilos.input}
      />

      {/* Formulario de creación */}
      {mostrarFormulario && (
        <form onSubmit={handleCrear} style={estilos.formulario}>
          <h3 style={{ marginTop: 0 }}>Nuevo Producto</h3>
          <div style={estilos.gridDos}>
            <input required placeholder="Nombre *" value={nuevoProducto.nombre}
              onChange={(e) => setNuevoProducto({ ...nuevoProducto, nombre: e.target.value })}
              style={estilos.input} />
            <input required placeholder="SKU *" value={nuevoProducto.codigoSku}
              onChange={(e) => setNuevoProducto({ ...nuevoProducto, codigoSku: e.target.value })}
              style={estilos.input} />
            <input required type="number" placeholder="Precio *" value={nuevoProducto.precio}
              onChange={(e) => setNuevoProducto({ ...nuevoProducto, precio: e.target.value })}
              style={estilos.input} />
            <input required type="number" placeholder="Stock inicial *" value={nuevoProducto.stock}
              onChange={(e) => setNuevoProducto({ ...nuevoProducto, stock: e.target.value })}
              style={estilos.input} />
            <input placeholder="Categoría" value={nuevoProducto.categoria}
              onChange={(e) => setNuevoProducto({ ...nuevoProducto, categoria: e.target.value })}
              style={estilos.input} />
            <input placeholder="Descripción" value={nuevoProducto.descripcion}
              onChange={(e) => setNuevoProducto({ ...nuevoProducto, descripcion: e.target.value })}
              style={estilos.input} />
          </div>
          <button type="submit" style={estilos.botonPrimario}>Guardar Producto</button>
        </form>
      )}

      {/* Mensaje de error */}
      {error && (
        <div style={estilos.alerta}>
          ⚠️ {error}
          <button onClick={cargarProductos} style={{ marginLeft: '10px', cursor: 'pointer' }}>
            Reintentar
          </button>
        </div>
      )}

      {/* Tabla de productos */}
      {cargando ? (
        <p>Cargando productos...</p>
      ) : (
        <table style={estilos.tabla}>
          <thead>
            <tr style={{ backgroundColor: '#2c3e50', color: '#fff' }}>
              <th style={estilos.th}>SKU</th>
              <th style={estilos.th}>Nombre</th>
              <th style={estilos.th}>Categoría</th>
              <th style={estilos.th}>Precio</th>
              <th style={estilos.th}>Stock</th>
              <th style={estilos.th}>Estado</th>
              <th style={estilos.th}>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {productosFiltrados.length === 0 ? (
              <tr><td colSpan="7" style={{ textAlign: 'center', padding: '20px', color: '#666' }}>
                No hay productos registrados
              </td></tr>
            ) : productosFiltrados.map((p, i) => (
              <tr key={p.id} style={{ backgroundColor: i % 2 === 0 ? '#f8f9fa' : '#fff' }}>
                <td style={estilos.td}><code>{p.codigoSku}</code></td>
                <td style={estilos.td}>{p.nombre}</td>
                <td style={estilos.td}>{p.categoria || '—'}</td>
                <td style={estilos.td}>${p.precio?.toLocaleString('es-CL')}</td>
                <td style={{ ...estilos.td, color: p.stock <= p.stockMinimo ? '#dc3545' : '#28a745', fontWeight: 'bold' }}>
                  {p.stock} {p.stock <= p.stockMinimo && '⚠️'}
                </td>
                <td style={estilos.td}>{getEstadoBadge(p.estado)}</td>
                <td style={estilos.td}>
                  <button onClick={() => handleEliminar(p.id)} style={estilos.botonPeligro}>
                    🗑️
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

const estilos = {
  botonPrimario: {
    backgroundColor: '#2c3e50', color: '#fff', border: 'none',
    padding: '8px 16px', borderRadius: '6px', cursor: 'pointer', fontSize: '14px',
  },
  botonPeligro: {
    backgroundColor: '#dc3545', color: '#fff', border: 'none',
    padding: '4px 8px', borderRadius: '4px', cursor: 'pointer',
  },
  input: {
    width: '100%', padding: '8px 12px', border: '1px solid #ccc',
    borderRadius: '6px', fontSize: '14px', boxSizing: 'border-box',
  },
  formulario: {
    backgroundColor: '#f1f3f4', padding: '20px', borderRadius: '8px',
    marginBottom: '20px', border: '1px solid #ddd',
  },
  gridDos: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '12px' },
  tabla: { width: '100%', borderCollapse: 'collapse', marginTop: '16px' },
  th: { padding: '12px', textAlign: 'left', fontWeight: '600' },
  td: { padding: '10px 12px', borderBottom: '1px solid #eee' },
  alerta: {
    backgroundColor: '#fff3cd', border: '1px solid #ffc107',
    padding: '10px 16px', borderRadius: '6px', marginBottom: '16px', color: '#856404',
  },
};

export default InventarioPanel;
