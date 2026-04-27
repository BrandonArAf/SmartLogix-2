import React, { useState } from 'react';
import InventarioPanel from './components/InventarioPanel';
import PedidosPanel from './components/PedidosPanel';

function App() {
  const [tabActiva, setTabActiva] = useState('inventario');

  return (
    <div style={{ fontFamily: 'Segoe UI, sans-serif', minHeight: '100vh', backgroundColor: '#f4f6f8' }}>
      {/* Header */}
      <header style={{
        backgroundColor: '#2c3e50', color: '#fff',
        padding: '16px 32px', display: 'flex', alignItems: 'center', gap: '16px'
      }}>
        <span style={{ fontSize: '24px' }}>🚚</span>
        <div>
          <h1 style={{ margin: 0, fontSize: '20px' }}>SmartLogix</h1>
          <p style={{ margin: 0, fontSize: '13px', opacity: 0.75 }}>
            Plataforma de Gestión Logística para PYMEs
          </p>
        </div>
      </header>

      {/* Navegación */}
      <nav style={{ backgroundColor: '#fff', borderBottom: '2px solid #e0e0e0', padding: '0 32px' }}>
        {[
          { id: 'inventario', label: '📦 Inventario' },
          { id: 'pedidos', label: '🛒 Pedidos' },
        ].map((tab) => (
          <button key={tab.id} onClick={() => setTabActiva(tab.id)}
            style={{
              padding: '14px 20px', border: 'none', background: 'none',
              cursor: 'pointer', fontSize: '15px',
              borderBottom: tabActiva === tab.id ? '3px solid #2c3e50' : '3px solid transparent',
              color: tabActiva === tab.id ? '#2c3e50' : '#666',
              fontWeight: tabActiva === tab.id ? '600' : 'normal',
            }}>
            {tab.label}
          </button>
        ))}
      </nav>

      {/* Contenido principal */}
      <main style={{ maxWidth: '1200px', margin: '0 auto', padding: '24px' }}>
        <div style={{ backgroundColor: '#fff', borderRadius: '8px', boxShadow: '0 1px 4px rgba(0,0,0,0.08)' }}>
          {tabActiva === 'inventario' && <InventarioPanel />}
          {tabActiva === 'pedidos' && <PedidosPanel />}
        </div>
      </main>
    </div>
  );
}

export default App;
