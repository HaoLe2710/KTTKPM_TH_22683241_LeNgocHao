import React, { useEffect, useState } from 'react';
import axios from 'axios';

const API_CART = 'http://localhost:8082/cart';

function Cart({ userId, cartUpdated }) {
  const [cart, setCart] = useState([]);

  useEffect(() => {
    axios.get(`${API_CART}/${userId}`).then(res => setCart(res.data));
  }, [userId, cartUpdated]);

  return (
    <div>
      <h2>Cart</h2>
      <ul>
        {cart.map(item => (
          <li key={item.productId}>
            Product ID: {item.productId} | Quantity: {item.quantity}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default Cart;