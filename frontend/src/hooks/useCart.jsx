import { useState, useCallback } from 'react';
import toast from 'react-hot-toast';

const useCart = () => {
  const [cart, setCart] = useState({}); // { [restaurantId]: { restaurantName, items: [...] } }

  const addItem = useCallback((item) => {
    // item: { menuId, itemName, price, restaurantId, restaurantName, isVeg }
    const { restaurantId, restaurantName } = item;
    if (!restaurantId) return;

    setCart(prev => {
      const next = { ...prev };
      if (!next[restaurantId]) {
        next[restaurantId] = {
          restaurantName: restaurantName || 'Restaurant',
          items: []
        };
      }

      const existingIndex = next[restaurantId].items.findIndex(i => i.menuId === item.menuId);
      if (existingIndex > -1) {
        const updatedItems = [...next[restaurantId].items];
        updatedItems[existingIndex] = {
          ...updatedItems[existingIndex],
          quantity: updatedItems[existingIndex].quantity + 1
        };
        next[restaurantId] = { ...next[restaurantId], items: updatedItems };
      } else {
        next[restaurantId] = {
          ...next[restaurantId],
          items: [...next[restaurantId].items, { ...item, quantity: 1 }]
        };
      }
      return next;
    });
  }, []);

  const removeItem = useCallback((menuId) => {
    setCart(prev => {
      const next = { ...prev };
      for (const rid in next) {
        const filtered = next[rid].items.filter(i => i.menuId !== menuId);
        if (filtered.length === 0) {
          delete next[rid];
        } else {
          next[rid] = { ...next[rid], items: filtered };
        }
      }
      return next;
    });
  }, []);

  const updateQuantity = useCallback((menuId, qty) => {
    if (qty < 1) {
      removeItem(menuId);
      return;
    }
    setCart(prev => {
      const next = { ...prev };
      for (const rid in next) {
        const existingIndex = next[rid].items.findIndex(i => i.menuId === menuId);
        if (existingIndex > -1) {
          const updatedItems = [...next[rid].items];
          updatedItems[existingIndex] = { ...updatedItems[existingIndex], quantity: qty };
          next[rid] = { ...next[rid], items: updatedItems };
          break;
        }
      }
      return next;
    });
  }, [removeItem]);

  const clearCart = useCallback(() => {
    setCart({});
  }, []);

  // Derived properties for backward compatibility and general use
  const cartItems = Object.values(cart).flatMap(r => r.items);
  const totalAmount = Object.values(cart).reduce(
    (sum, r) => sum + r.items.reduce((s, i) => s + i.price * i.quantity, 0),
    0
  );
  const totalItems = Object.values(cart).reduce(
    (sum, r) => sum + r.items.reduce((s, i) => s + i.quantity, 0),
    0
  );

  return {
    cart,
    cartItems,
    addItem,
    removeItem,
    updateQuantity,
    clearCart,
    totalAmount,
    totalItems
  };
};

export default useCart;
