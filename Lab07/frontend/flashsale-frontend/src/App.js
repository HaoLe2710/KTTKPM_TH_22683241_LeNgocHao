import React, { useState } from 'react';
import ProductList from './components/ProductList';
import Cart from './components/Cart';
import Checkout from './components/Checkout';
import MonitorDashboard from './components/MonitorDashboard';
import './App.css';

function App() {
  const [userId] = useState(1); // demo user
  const [cartUpdated, setCartUpdated] = useState(false);

  return (
    <div className="app-shell">
      <header className="app-header">
        <div>
          <h1>Flash Sale Control Room</h1>
          <p>User #{userId} demo flow with PU observability and fast load testing.</p>
        </div>
      </header>

      <main className="app-grid">
        <section className="shop-panel">
          <ProductList userId={userId} cartUpdated={cartUpdated} setCartUpdated={setCartUpdated} />
          <Cart userId={userId} cartUpdated={cartUpdated} />
          <Checkout userId={userId} setCartUpdated={setCartUpdated} />
        </section>
        <MonitorDashboard onDataChanged={() => setCartUpdated(prev => !prev)} />
      </main>
    </div>
  );
}

export default App;
