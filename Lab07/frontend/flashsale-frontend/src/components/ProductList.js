import React, { useEffect, useState } from 'react';
import axios from 'axios';

const API_PRODUCT = 'http://localhost:8081/products';
const API_CART = 'http://localhost:8082/cart/add';

function ProductList({ userId, cartUpdated, setCartUpdated }) {
  const [products, setProducts] = useState([]);

  const loadProducts = () => {
    axios.get(`${API_PRODUCT}?silent=1`, { headers: { 'Cache-Control': 'no-cache', 'x-silent-log': 'true' } })
      .then(res => setProducts(res.data))
      .catch(() => setProducts([]));
  };

  useEffect(() => {
    loadProducts();
  }, [cartUpdated]);

  useEffect(() => {
    const timer = setInterval(loadProducts, 1500);
    return () => clearInterval(timer);
  }, []);

  const addToCart = (productId) => {
    axios.post(API_CART, { userId, productId, quantity: 1 })
      .then(() => {
        alert('Added to cart!');
        loadProducts();
        setCartUpdated(prev => !prev);
      })
      .catch(err => {
        alert(err.response?.data?.msg || 'Add to cart failed');
        loadProducts();
      });
  };

  return (
    <div>
      <h2>Products</h2>
      <ul>
        {products.map(product => (
          <li key={product.id}>
            <span>{product.name} - {product.price} VND - Stock: {product.stock}</span>
            <button disabled={product.stock <= 0} onClick={() => addToCart(product.id)} style={{ marginLeft: 10 }}>
              {product.stock <= 0 ? 'Out of stock' : 'Add to Cart'}
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default ProductList;
