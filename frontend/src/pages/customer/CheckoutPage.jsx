import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Smartphone, Banknote, CreditCard, Building2, ClipboardList, MapPin, CheckCircle, ShoppingCart, Lock, Package, Trash2, Plus, Minus } from 'lucide-react';
import Navbar from '../../components/layout/Navbar';
import { useCartContext } from '../../context/CartContext';
import { useAuth } from '../../context/AuthContext';
import api from '../../api/client';
import toast from 'react-hot-toast';
import './CheckoutPage.css';

const PAYMENT_METHODS = [
  { value: 'upi',             Icon: Smartphone, label: 'UPI',                desc: 'PhonePe, GPay, Paytm' },
  { value: 'cash_on_delivery',Icon: Banknote,   label: 'Cash on Delivery',  desc: 'Pay when delivered' },
  { value: 'credit_card',     Icon: CreditCard, label: 'Credit/Debit Card', desc: 'Visa, Mastercard, RuPay' },
  { value: 'net_banking',     Icon: Building2,  label: 'Net Banking',       desc: 'All major banks' },
];

const CheckoutPage = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { cart, cartItems, totalAmount, clearCart, removeItem, updateQuantity } = useCartContext();
  const [paymentMethod, setPaymentMethod] = useState('cash_on_delivery');
  const [address, setAddress] = useState(user?.address || '');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const deliveryFeePerRest = 30;

  // Convert structured cart object to grouped array format
  const groupedRestaurants = Object.entries(cart || {}).map(([rid, rest]) => ({
    restaurantId: parseInt(rid),
    restaurantName: rest.restaurantName,
    items: rest.items,
    subtotal: rest.items.reduce((sum, item) => sum + item.price * item.quantity, 0)
  }));

  const totalDeliveryFee = groupedRestaurants.length * deliveryFeePerRest;
  const grandTotal = totalAmount + totalDeliveryFee;

  const handlePlaceOrder = async () => {
    if (!address.trim()) {
      toast.error('Please enter a delivery address');
      return;
    }
    if (groupedRestaurants.length === 0) {
      toast.error('Your cart is empty');
      return;
    }
    setLoading(true);
    try {
      const orderPromises = groupedRestaurants.map(rest => {
        const orderPayload = {
          restaurantId: rest.restaurantId,
          paymentMethod,
          deliveryAddress: address,
          items: rest.items.map(item => ({
            menuId: item.menuId,
            quantity: item.quantity,
            itemTotal: item.price * item.quantity,
          })),
          totalAmount: rest.subtotal + deliveryFeePerRest,
        };
        return api.post('/orders', orderPayload);
      });

      await Promise.all(orderPromises);
      setSuccess(true);
      clearCart();
      toast.success('Orders placed successfully! 🎉');
      setTimeout(() => navigate('/orders'), 3000);
    } catch (err) {
      toast.error(err?.response?.data?.message || 'Failed to place orders. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (cartItems.length === 0 && !success) {
    return (
      <div className="page-wrapper">
        <Navbar />
        <div className="container">
          <div className="empty-state" style={{ marginTop: '60px' }}>
            <div className="empty-icon"><ShoppingCart size={60} /></div>
            <h3>Your cart is empty</h3>
            <p>Add some items before checking out</p>
            <button className="btn-primary" onClick={() => navigate('/home')}>Browse Restaurants</button>
          </div>
        </div>
      </div>
    );
  }

  if (success) {
    return (
      <div className="page-wrapper">
        <Navbar />
        <div className="container">
          <div className="checkout-success">
            <div className="success-animation">
              <div className="success-circle"><CheckCircle size={64} color="var(--success)" /></div>
            </div>
            <h1>Order Placed!</h1>
            <p>Your delicious food is being prepared. Track your orders in the Orders page.</p>
            <div className="success-redirect">Redirecting to orders in 3 seconds...</div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="page-wrapper">
      <Navbar />
      <div className="container checkout-page">
        <div className="page-header">
          <h1><ShoppingCart size={24} style={{ display: 'inline', verticalAlign: 'middle', marginRight: '8px' }} />Checkout</h1>
          <p>Review your order and complete payment</p>
        </div>

        <div className="checkout-grid">
          {/* Order Summary */}
          <div className="checkout-summary glass-card">
            <h3 className="checkout-section-title"><ClipboardList size={18} /> Order Summary</h3>
            
            {groupedRestaurants.map((rest, idx) => (
              <div key={rest.restaurantId} className="checkout-restaurant-group" style={{ marginBottom: '24px' }}>
                <div className="checkout-restaurant" style={{ borderBottom: '1px solid rgba(255, 255, 255, 0.06)', paddingBottom: '8px', marginBottom: '12px' }}>
                  <Package size={15} style={{ color: 'var(--primary)' }} />
                  <span style={{ fontWeight: '700', color: 'var(--primary)' }}>{rest.restaurantName}</span>
                </div>

                <div className="checkout-items">
                  {rest.items.map(item => (
                    <div key={item.menuId} className="checkout-item">
                      <div className="checkout-item-info">
                        <span className="checkout-item-name">{item.itemName}</span>
                      </div>
                      <div className="checkout-item-controls">
                        <div className="checkout-qty-row">
                          <button
                            className="checkout-qty-btn"
                            onClick={() => updateQuantity(item.menuId, item.quantity - 1)}
                            title="Decrease quantity"
                          >
                            <Minus size={11} />
                          </button>
                          <span className="checkout-qty-val">{item.quantity}</span>
                          <button
                            className="checkout-qty-btn"
                            onClick={() => updateQuantity(item.menuId, item.quantity + 1)}
                            title="Increase quantity"
                          >
                            <Plus size={11} />
                          </button>
                        </div>
                        <span className="checkout-item-price">₹{(item.price * item.quantity).toFixed(0)}</span>
                        <button
                          className="checkout-remove-btn"
                          onClick={() => removeItem(item.menuId)}
                          title="Remove item"
                        >
                          <Trash2 size={14} />
                        </button>
                      </div>
                    </div>
                  ))}
                </div>

                <div style={{ display: 'flex', justifyContent: 'flex-end', fontSize: '0.85rem', color: 'var(--text-secondary)', marginTop: '8px' }}>
                  <span>Subtotal: ₹{rest.subtotal.toFixed(0)}</span>
                </div>
                {idx < groupedRestaurants.length - 1 && <div className="divider" style={{ margin: '16px 0' }} />}
              </div>
            ))}

            <div className="divider" />

            <div className="checkout-totals">
              <div className="checkout-total-row">
                <span>Items Subtotal</span>
                <span>₹{totalAmount.toFixed(0)}</span>
              </div>
              <div className="checkout-total-row">
                <span>Delivery Fee ({groupedRestaurants.length} Restaurant{groupedRestaurants.length > 1 ? 's' : ''})</span>
                <span>₹{totalDeliveryFee}</span>
              </div>
              <div className="checkout-total-row grand-total">
                <span>Grand Total</span>
                <span className="gradient-text">₹{grandTotal.toFixed(0)}</span>
              </div>
            </div>
          </div>

          {/* Payment Form */}
          <div className="checkout-form">
            {/* Delivery Address */}
            <div className="glass-card checkout-section">
              <h3 className="checkout-section-title"><MapPin size={18} /> Delivery Address</h3>
              <textarea
                className="input-field"
                placeholder="Enter your full delivery address..."
                value={address}
                onChange={e => setAddress(e.target.value)}
                rows={3}
                style={{ resize: 'vertical', minHeight: '90px' }}
              />
            </div>

            {/* Payment Method */}
            <div className="glass-card checkout-section">
              <h3 className="checkout-section-title"><CreditCard size={18} /> Payment Method</h3>
              <div className="payment-methods">
                {PAYMENT_METHODS.map(pm => (
                  <label
                    key={pm.value}
                    className={`payment-card ${paymentMethod === pm.value ? 'selected' : ''}`}
                  >
                    <input
                      type="radio"
                      name="payment"
                      value={pm.value}
                      checked={paymentMethod === pm.value}
                      onChange={e => setPaymentMethod(e.target.value)}
                    />
                    <div className="payment-card-content">
                      <div className="payment-label"><pm.Icon size={16} /> {pm.label}</div>
                      <span className="payment-desc">{pm.desc}</span>
                    </div>
                    {paymentMethod === pm.value && <span className="payment-check"><CheckCircle size={16} /></span>}
                  </label>
                ))}
              </div>
            </div>

            {/* Place Order Button */}
            <button
              className="btn-primary place-order-btn"
              onClick={handlePlaceOrder}
              disabled={loading}
            >
              {loading ? (
                <><div className="spinner-sm" /> Placing Orders...</>
              ) : (
                <><Package size={17} /> Place Order · ₹{grandTotal.toFixed(0)}</>
              )}
            </button>

            <p className="checkout-note" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
              <Lock size={14} /> Secure checkout. Your payment information is encrypted.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CheckoutPage;
