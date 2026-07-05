import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Navbar from '../../components/layout/Navbar';
import { useAuth } from '../../context/AuthContext';
import api from '../../api/client';
import { Bike, DollarSign, CheckCircle, ArrowRight, Package, Navigation } from 'lucide-react';
import './DeliveryDashboard.css';

const DeliveryDashboard = () => {
  const { user }  = useAuth();
  const [orders, setOrders]   = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const res = await api.get('/delivery/orders');
        setOrders(res.data || []);
      } catch {
        setOrders([]);
      } finally {
        setLoading(false);
      }
    };
    fetchOrders();
  }, []);

  const activeOrders   = orders.filter(o => ['ready', 'out_for_delivery'].includes(o.status));
  const completedToday = orders.filter(o => o.status === 'delivered');
  const earnings       = completedToday.reduce((sum, o) => sum + (o.totalAmount * 0.05), 0);

  const STATS = [
    { Icon: Bike,          label: "Today's Deliveries", value: activeOrders.length,       color: 'rgba(255,107,53,0.15)',  iconColor: 'var(--primary)' },
    { Icon: DollarSign,    label: 'Earnings Today',      value: `₹${earnings.toFixed(2)}`, color: 'rgba(6,214,160,0.15)',   iconColor: 'var(--success)' },
    { Icon: CheckCircle,   label: 'Completed',           value: completedToday.length,     color: 'rgba(255,209,102,0.15)', iconColor: 'var(--warning)' },
  ];

  return (
    <div className="page-wrapper">
      <Navbar />

      {/* ── Hero Banner ── */}
      <div className="delivery-hero-banner">
        <div className="delivery-hero-bg" />
        <div className="delivery-hero-orb delivery-orb-1" />
        <div className="delivery-hero-orb delivery-orb-2" />
        <div className="container delivery-hero-inner">
          <div className="delivery-hero-icon">
            <Bike size={30} color="var(--primary)" />
          </div>
          <div className="delivery-hero-text">
            <h1>Hey, <span className="gradient-text">{user?.username}</span>!</h1>
            <p>Ready to deliver some happiness today?</p>
          </div>
          <Link to="/delivery/active" className="btn-primary" style={{ marginLeft: 'auto' }}>
            <Navigation size={16} /> View Active Orders
          </Link>
        </div>
      </div>

      <div className="container delivery-dashboard">
        {/* ── Stats ── */}
        {loading ? (
          <div className="loading-screen"><div className="spinner" /></div>
        ) : (
          <div className="grid-3" style={{ marginBottom: '40px' }}>
            {STATS.map(s => (
              <div key={s.label} className="stat-card">
                <div className="stat-icon" style={{ background: s.color }}>
                  <s.Icon size={22} color={s.iconColor} />
                </div>
                <div className="stat-value">{s.value}</div>
                <div className="stat-label">{s.label}</div>
              </div>
            ))}
          </div>
        )}

        {/* ── Active Orders Preview ── */}
        <div className="glass-card" style={{ overflow: 'hidden' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '22px 28px 18px', borderBottom: '1px solid rgba(255,255,255,0.07)' }}>
            <h3 style={{ fontFamily: 'Poppins, sans-serif', fontWeight: 700, display: 'flex', alignItems: 'center', gap: 10 }}>
              <span style={{ width: 10, height: 10, borderRadius: '50%', background: 'var(--error)', display: 'inline-block', boxShadow: '0 0 8px var(--error)' }} />
              Active Orders
            </h3>
            <Link to="/delivery/active" className="btn-ghost" style={{ padding: '8px 18px', fontSize: '0.85rem' }}>
              View All <ArrowRight size={14} />
            </Link>
          </div>

          {activeOrders.length === 0 ? (
            <div className="empty-state" style={{ padding: '52px 24px' }}>
              <div className="empty-icon"><CheckCircle size={56} color="var(--success)" /></div>
              <h3>All caught up!</h3>
              <p>No active deliveries right now</p>
            </div>
          ) : (
            <div style={{ padding: '16px 24px 24px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
              {activeOrders.slice(0, 3).map(order => (
                <div key={order.orderID || order.orderId || order.id} className="delivery-order-card">
                  <div>
                    <div style={{ fontFamily: 'Poppins, sans-serif', fontWeight: 700, marginBottom: '4px' }}>
                      Order #{order.orderID || order.orderId || order.id}
                    </div>
                    <div style={{ fontSize: '0.84rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: 6 }}>
                      <Package size={12} />
                      {order.restaurantName || 'Restaurant'} &rarr; {order.deliveryAddress || 'Customer'}
                    </div>
                  </div>
                  <span className={`badge ${order.status === 'ready' ? 'badge-yellow' : 'badge-blue'}`}>
                    {order.status === 'ready'
                      ? <><Package size={11} /> Ready</>
                      : <><Bike size={11} /> In Transit</>}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default DeliveryDashboard;
