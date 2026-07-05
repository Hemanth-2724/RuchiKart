import React, { useState, useEffect } from 'react';
import Navbar from '../../components/layout/Navbar';
import api from '../../api/client';
import toast from 'react-hot-toast';
import {
  Bike, MapPin, Home, CreditCard, DollarSign,
  ShoppingBag, CheckCircle, Package, ArrowRight
} from 'lucide-react';
import './ActiveDeliveriesPage.css';

const ActiveDeliveriesPage = () => {
  const [orders, setOrders]       = useState([]);
  const [loading, setLoading]     = useState(true);
  const [updatingId, setUpdatingId] = useState(null);

  const fetchOrders = async () => {
    try {
      const res = await api.get('/delivery/orders');
      setOrders(res.data || []);
    } catch {
      toast.error('Failed to load delivery orders');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchOrders(); }, []);

  const handleStatusUpdate = async (id, status) => {
    setUpdatingId(id);
    try {
      await api.put('/delivery/orders', { orderId: id, status });
      toast.success(`Order marked as ${status.replace('_', ' ')}!`);
      fetchOrders();
    } catch {
      toast.error('Failed to update order status');
    } finally {
      setUpdatingId(null);
    }
  };

  const readyOrders  = orders.filter(o => o.status === 'ready');
  const activeOrders = orders.filter(o => o.status === 'out_for_delivery');

  const OrderDeliveryCard = ({ order, isActive }) => (
    <div className="delivery-card glass-card">
      <div className="delivery-card-header">
        <div>
          <div className="delivery-order-id">Order #{order.orderID || order.orderId || order.id}</div>
          <div className="delivery-restaurant">{order.restaurantName || 'Restaurant'}</div>
        </div>
        <span className={`badge ${isActive ? 'badge-blue' : 'badge-yellow'}`}>
          {isActive ? <><Bike size={11} /> In Transit</> : <><Package size={11} /> Ready to Pick</>}
        </span>
      </div>

      <div className="delivery-info-grid">
        <div className="delivery-info-item">
          <span className="delivery-info-label"><MapPin size={13} /> Pickup</span>
          <span className="delivery-info-val">{order.restaurantAddress || 'Restaurant Address'}</span>
        </div>
        <div className="delivery-info-item">
          <span className="delivery-info-label"><Home size={13} /> Deliver To</span>
          <span className="delivery-info-val">{order.deliveryAddress || 'Customer Address'}</span>
        </div>
        <div className="delivery-info-item">
          <span className="delivery-info-label"><CreditCard size={13} /> Payment</span>
          <span className="delivery-info-val">{order.paymentMethod?.replace('_', ' ') || 'COD'}</span>
        </div>
        <div className="delivery-info-item">
          <span className="delivery-info-label"><DollarSign size={13} /> Order Value</span>
          <span className="delivery-info-val" style={{ color: 'var(--primary)', fontWeight: 700 }}>
            ₹{order.totalAmount?.toFixed(0) || 0}
          </span>
        </div>
      </div>

      {order.items && order.items.length > 0 && (
        <div className="delivery-items">
          <span className="delivery-info-label"><ShoppingBag size={13} /> Items:</span>
          <span style={{ color: 'var(--text-secondary)', fontSize: '0.85rem' }}>
            {order.items.map(i => i.itemName || 'Item').join(', ')}
          </span>
        </div>
      )}

      <div className="delivery-actions">
        {!isActive && (
          <button
            className="btn-primary"
            onClick={() => handleStatusUpdate(order.orderID || order.orderId || order.id, 'out_for_delivery')}
            disabled={updatingId === (order.orderID || order.orderId || order.id)}
          >
            {updatingId === (order.orderID || order.orderId || order.id)
              ? <div className="spinner-sm" />
              : <><Bike size={15} /> Accept &amp; Pick Up</>}
          </button>
        )}
        {isActive && (
          <button
            className="btn-primary"
            style={{ background: 'linear-gradient(135deg, #06D6A0, #1B7F6A)' }}
            onClick={() => handleStatusUpdate(order.orderID || order.orderId || order.id, 'delivered')}
            disabled={updatingId === (order.orderID || order.orderId || order.id)}
          >
            {updatingId === (order.orderID || order.orderId || order.id)
              ? <div className="spinner-sm" />
              : <><CheckCircle size={15} /> Mark as Delivered</>}
          </button>
        )}
      </div>
    </div>
  );

  return (
    <div className="page-wrapper">
      <Navbar />

      {/* ── Hero Banner ── */}
      <div className="active-del-hero-banner">
        <div className="active-del-hero-bg" />
        <div className="active-del-hero-orb active-del-orb-1" />
        <div className="active-del-hero-orb active-del-orb-2" />
        <div className="container active-del-hero-inner">
          <div className="active-del-hero-icon">
            <Bike size={28} color="var(--primary)" />
          </div>
          <div>
            <h1>Active Deliveries</h1>
            <p>Pick up and deliver your assigned orders</p>
          </div>
        </div>
      </div>

      <div className="container active-deliveries-page">
        {loading ? (
          <div className="loading-screen"><div className="spinner" /></div>
        ) : (
          <>
            {/* ── Ready for Pickup ── */}
            <div className="delivery-section">
              <h2 className="delivery-section-title">
                <Package size={20} color="var(--warning)" />
                Ready for Pickup
                <span className="delivery-section-count">{readyOrders.length}</span>
              </h2>
              {readyOrders.length === 0 ? (
                <div className="glass-card" style={{ padding: '32px', textAlign: 'center', color: 'var(--text-muted)' }}>
                  No orders ready for pickup
                </div>
              ) : (
                <div className="delivery-grid">
                  {readyOrders.map(o => (
                    <OrderDeliveryCard key={o.orderID || o.orderId || o.id} order={o} isActive={false} />
                  ))}
                </div>
              )}
            </div>

            {/* ── Out for Delivery ── */}
            <div className="delivery-section">
              <h2 className="delivery-section-title">
                <Bike size={20} color="#4895EF" />
                Out for Delivery
                <span className="delivery-section-count">{activeOrders.length}</span>
              </h2>
              {activeOrders.length === 0 ? (
                <div className="glass-card" style={{ padding: '32px', textAlign: 'center', color: 'var(--text-muted)' }}>
                  No orders in transit
                </div>
              ) : (
                <div className="delivery-grid">
                  {activeOrders.map(o => (
                    <OrderDeliveryCard key={o.orderID || o.orderId || o.id} order={o} isActive={true} />
                  ))}
                </div>
              )}
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default ActiveDeliveriesPage;
