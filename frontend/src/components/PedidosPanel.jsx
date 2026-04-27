import React, { useState, useEffect } from 'react';
import { pedidosService } from '../services/api';

const TIPOS_PEDIDO = ['ESTANDAR', 'EXPRESS', 'MAYORISTA'];
const ESTADOS_PEDIDO = ['PENDIENTE', 'CONFIRMADO', 'EN_PROCESO', 'ENVIADO', 'ENTREGADO', 'CANCELADO'];

function PedidosPanel() {
  const [pedidos, setPedidos] = useState([]);
  const [cargando, setCargando] = useState(false);
  const [error, setError] = useState(null);
  const [mostrarFormulario, setMostrarFormulario] = useState(false);
  const [filtroEstado, setFiltroEstado] = useState('TODOS');

  const [nuevoPedido, setNuevoPedido] = useState({
    tipo: 'ESTANDAR',
    clienteId: '',
    nombreCliente: '',
    direccionEnvio: '',
  });

  useEffect(() => {
    cargarPedidos();
  }, []);

  const cargarPedidos = async () => {
    setCargando(true);
    setError(null);
    try {
      const { data } = await pedidosService.getAll();
      if (Array.isArray(data) && data[0]?.error) {
        setError(data[0].error);
        setPedidos([]);
      } else {
        setPedidos(data);
      }
    } catch (err) {
      setError('No se pudo conectar con el servicio de pedidos.');
    } finally {
      setCargando(false);
    }
  };

  const handleCrear = async (e) => {
    e.preventDefault();
    try {
      await pedidosService.create({
        ...nuevoPedido,
        clienteId: parseInt(nuevoPedido.clienteId),
      });
      setMostrarFormulario(false);
      setNuevoPedido({ tipo: 'ESTANDAR', clienteId: '', nombreCliente: '', direccionEnvio: '' });
      cargarPedidos();
    } catch (err) {
      setError('Error al crear el pedido.');
    }
  };

  const handleCambiarEstado = async (id, nuevoEstado) => {
    try {
      await pedidosService.cambiarEstado(id, nuevoEstado);
      cargarPedidos();
    } catch (err) {
      alert('No se puede cambiar a ese estado.');
    }
  };

  const getColorEstado = (estado) => {
    const colores = {
      PENDIENTE: '#f39c12', CONFIRMADO: '#3498db', EN_PROCESO: '#9b59b6',
      ENVIADO: '#1abc9c', ENTREGADO: '#27ae60', CANCELADO: '#e74c3c',
    };
    return colores[estado] || '#95a5a6';
  };

  const getTipoBadge = (tipo) => {
    const iconos = { ESTANDAR: '📦', EXPRESS: '⚡', MAYORISTA: '🏭' };
    return `${iconos[tipo] || ''} ${tipo}`;
  };

  const pedidosFiltrados = filtroEstado === 'TODOS'
    ? pedidos
    : pedidos.filter((p) => p.estado === filtroEstado);

  return (
    <div style={{ padding: '20px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h2 style={{ margin: 0, color: '#2c3e50' }}>🛒 Gestión de Pedidos</h2>
        <button onClick={() => setMostrarFormulario(!mostrarFormulario)} style={estilos.botonPrimario}>
          {mostrarFormulario ? '✕ Cancelar' : '+ Nuevo Pedido'}
        </button>
      </div>

      {/* Filtro por estado */}
      <div style={{ marginBottom: '16px', display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
        {['TODOS', ...ESTADOS_PEDIDO].map((e) => (
          <button key={e} onClick={() => setFiltroEstado(e)}
            style={{
              ...estilos.botonFiltro,
              backgroundColor: filtroEstado === e ? '#2c3e50' : '#ecf0f1',
              color: filtroEstado === e ? '#fff' : '#333',
            }}>
            {e}
          </button>
        ))}
      </div>

      {/* Formulario */}
      {mostrarFormulario && (
        <form onSubmit={handleCrear} style={estilos.formulario}>
          <h3 style={{ marginTop: 0 }}>Nuevo Pedido</h3>
          <div style={estilos.gridDos}>
            <div>
              <label style={estilos.label}>Tipo de Pedido</label>
              <select value={nuevoPedido.tipo}
                onChange={(e) => setNuevoPedido({ ...nuevoPedido, tipo: e.target.value })}
                style={estilos.input}>
                {TIPOS_PEDIDO.map((t) => <option key={t} value={t}>{t}</option>)}
              </select>
            </div>
            <div>
              <label style={estilos.label}>ID Cliente</label>
              <input required type="number" placeholder="Ej: 5" value={nuevoPedido.clienteId}
                onChange={(e) => setNuevoPedido({ ...nuevoPedido, clienteId: e.target.value })}
                style={estilos.input} />
            </div>
            <div>
              <label style={estilos.label}>Nombre Cliente</label>
              <input required placeholder="Nombre o empresa" value={nuevoPedido.nombreCliente}
                onChange={(e) => setNuevoPedido({ ...nuevoPedido, nombreCliente: e.target.value })}
                style={estilos.input} />
            </div>
            <div>
              <label style={estilos.label}>Dirección de Envío</label>
              <input required placeholder="Calle, ciudad" value={nuevoPedido.direccionEnvio}
                onChange={(e) => setNuevoPedido({ ...nuevoPedido, direccionEnvio: e.target.value })}
                style={estilos.input} />
            </div>
          </div>
          <p style={{ fontSize: '13px', color: '#666', margin: '8px 0' }}>
            💡 El sistema aplicará automáticamente las reglas del tipo de pedido seleccionado.
          </p>
          <button type="submit" style={estilos.botonPrimario}>Crear Pedido</button>
        </form>
      )}

      {error && <div style={estilos.alerta}>⚠️ {error}</div>}

      {cargando ? <p>Cargando pedidos...</p> : (
        <table style={estilos.tabla}>
          <thead>
            <tr style={{ backgroundColor: '#2c3e50', color: '#fff' }}>
              <th style={estilos.th}>N° Pedido</th>
              <th style={estilos.th}>Cliente</th>
              <th style={estilos.th}>Tipo</th>
              <th style={estilos.th}>Estado</th>
              <th style={estilos.th}>Fecha</th>
              <th style={estilos.th}>Cambiar Estado</th>
            </tr>
          </thead>
          <tbody>
            {pedidosFiltrados.length === 0 ? (
              <tr><td colSpan="6" style={{ textAlign: 'center', padding: '20px', color: '#666' }}>
                No hay pedidos registrados
              </td></tr>
            ) : pedidosFiltrados.map((p, i) => (
              <tr key={p.id} style={{ backgroundColor: i % 2 === 0 ? '#f8f9fa' : '#fff' }}>
                <td style={estilos.td}><code style={{ fontSize: '12px' }}>{p.numeroPedido}</code></td>
                <td style={estilos.td}>{p.nombreCliente}</td>
                <td style={estilos.td}>{getTipoBadge(p.tipo)}</td>
                <td style={estilos.td}>
                  <span style={{
                    backgroundColor: getColorEstado(p.estado),
                    color: '#fff', padding: '3px 10px',
                    borderRadius: '12px', fontSize: '12px'
                  }}>
                    {p.estado}
                  </span>
                </td>
                <td style={estilos.td}>
                  {p.fechaCreacion ? new Date(p.fechaCreacion).toLocaleDateString('es-CL') : '—'}
                </td>
                <td style={estilos.td}>
                  <select onChange={(e) => e.target.value && handleCambiarEstado(p.id, e.target.value)}
                    value=""
                    style={{ padding: '4px', borderRadius: '4px', fontSize: '13px' }}>
                    <option value="">-- cambiar --</option>
                    {ESTADOS_PEDIDO.filter((e) => e !== p.estado).map((e) => (
                      <option key={e} value={e}>{e}</option>
                    ))}
                  </select>
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
  botonFiltro: {
    border: 'none', padding: '5px 12px', borderRadius: '16px',
    cursor: 'pointer', fontSize: '13px', transition: 'all 0.2s',
  },
  input: {
    width: '100%', padding: '8px 12px', border: '1px solid #ccc',
    borderRadius: '6px', fontSize: '14px', boxSizing: 'border-box',
  },
  label: { display: 'block', fontSize: '13px', color: '#555', marginBottom: '4px' },
  formulario: {
    backgroundColor: '#f1f3f4', padding: '20px', borderRadius: '8px',
    marginBottom: '20px', border: '1px solid #ddd',
  },
  gridDos: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '12px' },
  tabla: { width: '100%', borderCollapse: 'collapse', marginTop: '8px' },
  th: { padding: '12px', textAlign: 'left', fontWeight: '600' },
  td: { padding: '10px 12px', borderBottom: '1px solid #eee' },
  alerta: {
    backgroundColor: '#fff3cd', border: '1px solid #ffc107',
    padding: '10px 16px', borderRadius: '6px', marginBottom: '16px', color: '#856404',
  },
};

export default PedidosPanel;
