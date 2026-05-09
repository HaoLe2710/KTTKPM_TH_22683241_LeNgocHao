import React from 'react';
import axios from 'axios';

const API_ORDER = 'http://localhost:8083/checkout';

function Checkout({ userId, setCartUpdated }) {
  const checkout = () => {
    axios.post(API_ORDER, { userId })
      .then(res => {
        alert('Checkout successful!');
        setCartUpdated(prev => !prev); // refresh cart
      })
      .catch(err => {
        alert(err.response?.data?.msg || 'Checkout failed');
      });
  };

  return (
    <div style={{ marginTop: 20 }}>
      <button onClick={checkout} style={{ padding: '10px 20px', fontSize: 16 }}>Checkout</button>
    </div>
  );
}

export default Checkout;