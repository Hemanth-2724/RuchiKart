import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Navbar from '../../components/layout/Navbar';
import { useAuth } from '../../context/AuthContext';
import api from '../../api/client';
import {
  Store, Package, DollarSign, Clock, UtensilsCrossed,
  ClipboardList, ChevronRight, ArrowRight
} from 'lucide-react';
import './OwnerDashboard.css';

const OwnerDashboard = () => {
  const { user } = useAuth();
  const [stats, setStats]   = useState(null);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [statsRes, ordersRes] = await Promise.all([
          api.get('/owner/dashboard'),
          api.get('/owner/orders'),
        ]);
        setStats(statsRes.data);
        setOrders((ordersRes.data || []).slice(0, 5));
      } catch {
        setStats({ totalOrders: 0, revenue: 0, pendingOrders: 0, menuItems: 0 });
        setOrders([]);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const STAT_CARDS = [
    { Icon: Package,         label: 'Total Orders',  value: stats?.totalOrders ?? '—',                                  color: 'rgba(255,107,53,0.18)',  iconColor: 'var(--primary)',  borderColor: 'rgba(255,107,53,0.22)' },
    { Icon: DollarSign,      label: 'Total Revenue', value: stats?.revenue ? `₹${stats.revenue.toFixed(0)}` : '₹—',    color: 'rgba(6,214,160,0.18)',   iconColor: 'var(--success)',  borderColor: 'rgba(6,214,160,0.22)' },
    { Icon: Clock,           label: 'Pending Orders',value: stats?.pendingOrders ?? '—',                                color: 'rgba(255,209,102,0.18)', iconColor: 'var(--warning)',  borderColor: 'rgba(255,209,102,0.22)' },
    { Icon: UtensilsCrossed, label: 'Menu Items',    value: stats?.menuItems ?? '—',                                    color: 'rgba(72,149,239,0.18)',  iconColor: '#4895ef',         borderColor: 'rgba(72,149,239,0.22)' },
  ];

  const STATUS_COLORS = {
    pending:          'badge-yellow',
    confirmed:        'badge-blue',
    preparing:        'badge-orange',
    ready:            'badge-green',
    delivered:        'badge-green',
    cancelled:        'badge-red',
  };

  return (
    <div className="page-wrapper">
      <Navbar />

      {/* ── Hero Banner ── */}
      <div className="owner-hero-banner">
        <div className="owner-hero-bg" />
        <div className="owner-hero-orb owner-orb-1" />
        <div className="owner-hero-orb owner-orb-2" />
        <div className="container owner-hero-inner">
          <div className="owner-hero-icon">
            <Store size={30} color="var(--primary)" />
          </div>
          <div className="owner-hero-text">
            <h1>Welcome back, <span className="gradient-text">{user?.username}</span>!</h1>
            <p>Here's how your restaurant is doing today.</p>
          </div>
          <div className="owner-hero-actions">
            <Link to="/owner/menu"   className="btn-primary"><UtensilsCrossed size={15} /> Add Menu Item</Link>
            <Link to="/owner/orders" className="btn-ghost"><ClipboardList size={15} /> View All Orders</Link>
          </div>
        </div>
      </div>

      <div className="container owner-dashboard">
        {/* ── Stats ── */}
        {loading ? (
          <div className="loading-screen"><div className="spinner" /></div>
        ) : (
          <div className="grid-4" style={{ marginBottom: '40px' }}>
            {STAT_CARDS.map(card => (
              <div key={card.label} className="stat-card" style={{ borderColor: card.borderColor }}>
                <div className="stat-icon" style={{ background: card.color }}>
                  <card.Icon size={22} color={card.iconColor} />
                </div>
                <div className="stat-value">{card.value}</div>
                <div className="stat-label">{card.label}</div>
              </div>
            ))}
          </div>
        )}

        {/* ── Recent Orders ── */}
        <div className="glass-card owner-table-card">
          <div className="owner-table-header">
            <h3 style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
              <ClipboardList size={18} color="var(--primary)" /> Recent Orders
            </h3>
            <Link to="/owner/orders" className="btn-ghost" style={{ padding: '8px 16px', fontSize: '0.85rem' }}>
              View All <ArrowRight size={14} />
            </Link>
          </div>

          {orders.length === 0 ? (
            <div className="empty-state" style={{ padding: '48px' }}>
              <div className="empty-icon"><Package size={56} /></div>
              <h3>No orders yet</h3>
              <p>Orders will appear here once customers start ordering</p>
            </div>
          ) : (
            <div style={{ overflowX: 'auto' }}>
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Order ID</th>
                    <th>Customer</th>
                    <th>Items</th>
                    <th>Amount</th>
                    <th>Status</th>
                    <th>Date</th>
                  </tr>
                </thead>
                <tbody>
                  {orders.map(order => (
                    <tr key={order.orderID || order.orderId || order.id}>
                      <td>#{order.orderID || order.orderId || order.id}</td>
                      <td>{order.customerName || order.username || 'Customer'}</td>
                      <td>{order.items?.length || '—'} items</td>
                      <td style={{ color: 'var(--primary)', fontFamily: 'Poppins, sans-serif', fontWeight: 700 }}>
                        ₹{order.totalAmount?.toFixed(0)}
                      </td>
                      <td><span className={`badge ${STATUS_COLORS[order.status] || 'badge-gray'}`}>{order.status}</span></td>
                      <td>{(order.orderDate || order.createdAt) ? new Date(order.orderDate || order.createdAt).toLocaleDateString('en-IN') : '—'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* ── Quick Links ── */}
        <div className="grid-2" style={{ marginTop: '28px' }}>
          <Link to="/owner/menu" className="glass-card owner-quick-card">
            <div className="owner-quick-icon-wrap">
              <UtensilsCrossed size={24} color="var(--primary)" />
            </div>
            <div>
              <h4>Manage Menu</h4>
              <p>Add, edit, or remove menu items</p>
            </div>
            <ChevronRight size={20} className="owner-quick-arrow" />
          </Link>
          <Link to="/owner/orders" className="glass-card owner-quick-card">
            <div className="owner-quick-icon-wrap">
              <ClipboardList size={24} color="#4895ef" />
            </div>
            <div>
              <h4>Manage Orders</h4>
              <p>Update order statuses, view history</p>
            </div>
            <ChevronRight size={20} className="owner-quick-arrow" />
          </Link>
        </div>
      </div>
    </div>
  );
};

export default OwnerDashboard;
