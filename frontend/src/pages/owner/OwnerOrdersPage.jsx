import React, { useState, useEffect } from 'react';
import Navbar from '../../components/layout/Navbar';
import api from '../../api/client';
import toast from 'react-hot-toast';
import { ClipboardList, Package, Clock, DollarSign, CheckCircle, XCircle, ChevronDown } from 'lucide-react';
import './OwnerOrdersPage.css';

const STATUS_OPTIONS = [
  { value: 'pending',   label: 'Pending' },
  { value: 'preparing', label: 'Preparing' },
  { value: 'ready',     label: 'Ready' },
  { value: 'cancelled', label: 'Cancelled' },
];

const STATUS_BADGES = {
  pending:          'badge-yellow',
  confirmed:        'badge-blue',
  preparing:        'badge-orange',
  ready:            'badge-green',
  out_for_delivery: 'badge-blue',
  delivered:        'badge-green',
  cancelled:        'badge-red',
};

const OwnerOrdersPage = () => {
  const [orders, setOrders]       = useState([]);
  const [loading, setLoading]     = useState(true);
  const [activeTab, setActiveTab] = useState('all');
  const [updatingId, setUpdatingId] = useState(null);
  const [openDropdownId, setOpenDropdownId] = useState(null);

  const fetchOrders = async () => {
    try {
      const res = await api.get('/owner/orders');
      setOrders(res.data || []);
    } catch {
      toast.error('Failed to load orders');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
    const closeDropdown = () => setOpenDropdownId(null);
    window.addEventListener('click', closeDropdown);
    return () => window.removeEventListener('click', closeDropdown);
  }, []);

  const handleStatusUpdate = async (orderId, status) => {
    setUpdatingId(orderId);
    try {
      await api.put(`/owner/orders/${orderId}/status`, { status });
      toast.success('Order status updated!');
      fetchOrders();
    } catch {
      toast.error('Failed to update status');
    } finally {
      setUpdatingId(null);
    }
  };

  const TABS = [
    { key: 'all',       label: 'All' },
    { key: 'pending',   label: 'Pending' },
    { key: 'preparing', label: 'Preparing' },
    { key: 'ready',     label: 'Ready' },
    { key: 'delivered', label: 'Delivered' },
  ];

  const filtered      = activeTab === 'all' ? orders : orders.filter(o => o.status === activeTab);
  const totalRevenue  = orders.filter(o => o.status === 'delivered').reduce((sum, o) => sum + (o.totalAmount || 0), 0);
  const pendingCount  = orders.filter(o => ['pending', 'confirmed', 'preparing'].includes(o.status)).length;

  return (
    <div className="page-wrapper">
      <Navbar />

      {/* ── Hero Banner ── */}
      <div className="owner-orders-hero-banner">
        <div className="owner-orders-hero-bg" />
        <div className="owner-orders-orb owner-orders-orb-1" />
        <div className="container owner-orders-hero-inner">
          <div className="owner-orders-hero-icon">
            <ClipboardList size={28} color="var(--primary)" />
          </div>
          <div>
            <h1>Order Management</h1>
            <p>Track and update all incoming orders</p>
          </div>
        </div>
      </div>

      <div className="container owner-orders-page">
        {/* ── Summary ── */}
        <div className="grid-3" style={{ marginBottom: '32px' }}>
          <div className="stat-card">
            <div className="stat-icon" style={{ background: 'rgba(255,107,53,0.15)' }}>
              <Package size={22} color="var(--primary)" />
            </div>
            <div className="stat-value">{orders.length}</div>
            <div className="stat-label">Total Orders</div>
          </div>
          <div className="stat-card">
            <div className="stat-icon" style={{ background: 'rgba(255,209,102,0.15)' }}>
              <Clock size={22} color="var(--warning)" />
            </div>
            <div className="stat-value">{pendingCount}</div>
            <div className="stat-label">Active Orders</div>
          </div>
          <div className="stat-card">
            <div className="stat-icon" style={{ background: 'rgba(6,214,160,0.15)' }}>
              <DollarSign size={22} color="var(--success)" />
            </div>
            <div className="stat-value">₹{totalRevenue.toFixed(0)}</div>
            <div className="stat-label">Total Revenue</div>
          </div>
        </div>

        {/* ── Tabs ── */}
        <div className="tab-bar" style={{ marginBottom: '28px' }}>
          {TABS.map(t => (
            <button
              key={t.key}
              className={`tab-btn ${activeTab === t.key ? 'active' : ''}`}
              onClick={() => setActiveTab(t.key)}
            >
              {t.label}
              {t.key !== 'all' && orders.filter(o => o.status === t.key).length > 0 && (
                <span className="tab-count">{orders.filter(o => o.status === t.key).length}</span>
              )}
            </button>
          ))}
        </div>

        {loading && <div className="loading-screen"><div className="spinner" /></div>}

        {!loading && filtered.length === 0 && (
          <div className="empty-state">
            <div className="empty-icon"><Package size={56} /></div>
            <h3>No orders</h3>
            <p>No {activeTab === 'all' ? '' : activeTab} orders at this time</p>
          </div>
        )}

        {!loading && filtered.length > 0 && (
          <div className="glass-card" style={{ overflow: 'hidden' }}>
            <div style={{ overflowX: 'auto' }}>
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Order ID</th>
                    <th>Customer</th>
                    <th>Items</th>
                    <th>Amount</th>
                    <th>Payment</th>
                    <th>Date</th>
                    <th>Status</th>
                    <th>Update</th>
                  </tr>
                </thead>
                <tbody>
                  {filtered.map(order => (
                    <tr key={order.orderID || order.orderId || order.id}>
                      <td>#{order.orderID || order.orderId || order.id}</td>
                      <td style={{ color: 'white', fontWeight: 600 }}>{order.customerName || order.username || 'Customer'}</td>
                      <td>{order.items?.length || 1} items</td>
                      <td style={{ color: 'var(--primary)', fontFamily: 'Poppins, sans-serif', fontWeight: 700 }}>
                        ₹{order.totalAmount?.toFixed(0)}
                      </td>
                      <td>{order.paymentMethod?.replace('_', ' ') || 'COD'}</td>
                      <td>{(order.orderDate || order.createdAt) ? new Date(order.orderDate || order.createdAt).toLocaleDateString('en-IN') : '—'}</td>
                      <td>
                        <span className={`badge ${STATUS_BADGES[order.status] || 'badge-gray'}`}>
                          {order.status?.replace('_', ' ')}
                        </span>
                      </td>
                      <td>
                        <div className="custom-dropdown-container">
                          <button
                            className="custom-dropdown-trigger"
                            onClick={(e) => {
                              e.stopPropagation();
                              const orderId = order.orderID || order.orderId || order.id;
                              setOpenDropdownId(openDropdownId === orderId ? null : orderId);
                            }}
                            disabled={updatingId === (order.orderID || order.orderId || order.id) || ['out_for_delivery', 'delivered', 'cancelled'].includes(order.status)}
                          >
                            <span>{STATUS_OPTIONS.find(o => o.value === order.status)?.label || order.status?.replace('_', ' ')}</span>
                            <ChevronDown size={14} className="dropdown-arrow-icon" />
                          </button>
                          
                          {openDropdownId === (order.orderID || order.orderId || order.id) && (
                            <div className="custom-dropdown-menu">
                              {STATUS_OPTIONS.map(opt => (
                                <button
                                  key={opt.value}
                                  type="button"
                                  className={`custom-dropdown-item ${order.status === opt.value ? 'active' : ''}`}
                                  onClick={() => handleStatusUpdate(order.orderID || order.orderId || order.id, opt.value)}
                                >
                                  {opt.label}
                                </button>
                              ))}
                            </div>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default OwnerOrdersPage;
