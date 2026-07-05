import React, { useState, useEffect } from 'react';
import { Clock, CheckCircle, ChefHat, Package, Bike, PartyPopper, XCircle, ClipboardList, Wallet, MapPin, ShoppingBag, AlertTriangle } from 'lucide-react';
import Navbar from '../../components/layout/Navbar';
import api from '../../api/client';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';
import './OrdersPage.css';

const STATUS_CONFIG = {
  pending:          { label: 'Pending',          badge: 'badge-yellow', Icon: Clock,        step: 0 },
  confirmed:        { label: 'Confirmed',        badge: 'badge-blue',   Icon: CheckCircle,  step: 1 },
  preparing:        { label: 'Preparing',        badge: 'badge-orange', Icon: ChefHat,      step: 2 },
  ready:            { label: 'Ready',            badge: 'badge-green',  Icon: Package,      step: 3 },
  out_for_delivery: { label: 'Out for Delivery', badge: 'badge-blue',   Icon: Bike,         step: 4 },
  delivered:        { label: 'Delivered',        badge: 'badge-green',  Icon: PartyPopper,  step: 5 },
  cancelled:        { label: 'Cancelled',        badge: 'badge-red',    Icon: XCircle,      step: -1 },
};

const STATUS_STEPS = [
  { key: 'pending',          label: 'Order Placed', Icon: ClipboardList },
  { key: 'confirmed',        label: 'Confirmed',    Icon: CheckCircle },
  { key: 'preparing',        label: 'Preparing',    Icon: ChefHat },
  { key: 'ready',            label: 'Ready',        Icon: Package },
  { key: 'out_for_delivery', label: 'On the Way',   Icon: Bike },
  { key: 'delivered',        label: 'Delivered',    Icon: PartyPopper },
];

const OrderTimeline = ({ status }) => {
  const config = STATUS_CONFIG[status];
  const currentStep = config?.step ?? 0;

  if (status === 'cancelled') {
    return (
      <div className="order-timeline">
        <span className="badge badge-red"><XCircle size={12} /> Order Cancelled</span>
      </div>
    );
  }

  return (
    <div className="order-timeline">
      {STATUS_STEPS.map((step, i) => (
        <React.Fragment key={step.key}>
          <div className={`timeline-step ${i <= currentStep ? 'done' : ''} ${i === currentStep ? 'active' : ''}`}>
            <div className="timeline-dot">
              {i <= currentStep ? <CheckCircle size={14} /> : <step.Icon size={13} />}
            </div>
            <span className="timeline-label">{step.label}</span>
          </div>
          {i < STATUS_STEPS.length - 1 && (
            <div className={`timeline-line ${i < currentStep ? 'done' : ''}`} />
          )}
        </React.Fragment>
      ))}
    </div>
  );
};

const OrderCard = ({ order, onCancel }) => {
  const { user } = useAuth();
  const [expanded, setExpanded] = useState(false);
  const [cancelling, setCancelling] = useState(false);
  const config = STATUS_CONFIG[order.status] || STATUS_CONFIG.pending;
  const orderDateStr = order.orderDate || order.createdAt;
  const date = orderDateStr ? new Date(orderDateStr).toLocaleDateString('en-IN', {
    day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit'
  }) : 'N/A';

  const handleCancel = async () => {
    const orderId = order.orderID || order.orderId || order.id;
    if (!window.confirm(`Are you sure you want to cancel order #${orderId}?`)) return;
    setCancelling(true);
    try {
      await api.put(`/orders/${orderId}/cancel`);
      toast.success('Order cancelled successfully!');
      if (onCancel) onCancel();
    } catch (err) {
      toast.error(err?.response?.data?.message || 'Failed to cancel order');
    } finally {
      setCancelling(false);
    }
  };

  return (
    <div className="order-card glass-card">
      <div className="order-card-header" onClick={() => setExpanded(!expanded)}>
        <div className="order-card-left">
          <div className="order-id">
            <span>Order</span>
            <span className="order-id-val">#{order.orderID || order.orderId || order.id}</span>
          </div>
          <div className="order-restaurant">{order.restaurantName || 'Restaurant'}</div>
          <div className="order-date">{date}</div>
        </div>
        <div className="order-card-right">
          <span className={`badge ${config.badge}`}>{config.icon} {config.label}</span>
          <div className="order-amount">₹{order.totalAmount?.toFixed(0) || 0}</div>
          <button className="order-expand-btn">{expanded ? '▲' : '▼'}</button>
        </div>
      </div>

      {expanded && (
        <div className="order-card-body">
          <div className="divider" />
          <OrderTimeline status={order.status} />
          {order.items && order.items.length > 0 && (
            <>
              <div className="divider" />
              <h4 className="order-items-title">Items Ordered</h4>
              <div className="order-items-list">
                {order.items.map((item, i) => (
                  <div key={i} className="order-item-row">
                    <span>{item.itemName || `Item ${i + 1}`}</span>
                    <span>× {item.quantity}</span>
                    <span>₹{item.itemTotal?.toFixed(0) || (item.price * item.quantity)?.toFixed(0) || 0}</span>
                  </div>
                ))}
              </div>
            </>
          )}
          <div className="divider" />
          <div className="order-footer-info">
            <span><Wallet size={13} /> {order.paymentMethod?.replace('_', ' ') || 'N/A'}</span>
            <span><MapPin size={13} /> {order.deliveryAddress || user?.address || 'N/A'}</span>
          </div>

          {order.status === 'pending' && (
            <>
              <div className="divider" />
              <button
                className="btn-danger"
                style={{
                  width: '100%',
                  marginTop: '12px',
                  padding: '10px',
                  display: 'flex',
                  justifyContent: 'center',
                  alignItems: 'center',
                  gap: '8px',
                  borderRadius: '8px',
                  border: 'none',
                  background: '#ff4d4d',
                  color: 'white',
                  fontWeight: '600',
                  cursor: 'pointer'
                }}
                onClick={handleCancel}
                disabled={cancelling}
              >
                {cancelling ? 'Cancelling...' : <><XCircle size={15} /> Cancel Order</>}
              </button>
            </>
          )}
        </div>
      )}
    </div>
  );
};

const OrdersPage = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filter, setFilter] = useState('all');

  const fetchOrders = async () => {
    try {
      const res = await api.get('/orders');
      setOrders(res.data || []);
    } catch {
      setError('Failed to load orders');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const filtered = filter === 'all' ? orders :
    filter === 'active' ? orders.filter(o => !['delivered', 'cancelled'].includes(o.status)) :
    orders.filter(o => o.status === filter);

  return (
    <div className="page-wrapper">
      <Navbar />
      <div className="container orders-page">
        <div className="page-header">
          <h1><Package size={26} style={{ display: 'inline', verticalAlign: 'middle', marginRight: '8px' }} />My Orders</h1>
          <p>Track all your food orders in real-time</p>
        </div>

        <div className="tab-bar" style={{ marginBottom: '28px' }}>
          {[
            { key: 'all',       label: 'All Orders' },
            { key: 'active',    label: 'Active' },
            { key: 'delivered', label: 'Delivered' },
            { key: 'cancelled', label: 'Cancelled' },
          ].map(t => (
            <button
              key={t.key}
              className={`tab-btn ${filter === t.key ? 'active' : ''}`}
              onClick={() => setFilter(t.key)}
            >
              {t.label}
            </button>
          ))}
        </div>

        {loading && <div className="loading-screen"><div className="spinner" /><p>Loading orders…</p></div>}
        {error && <div className="glass-card error-state"><div className="error-icon"><AlertTriangle size={40} /></div><h3>Error</h3><p>{error}</p></div>}

        {!loading && !error && filtered.length === 0 && (
          <div className="empty-state">
            <div className="empty-icon"><ShoppingBag size={60} /></div>
            <h3>No orders found</h3>
            <p>{filter === 'all' ? "You haven't placed any orders yet" : `No ${filter} orders`}</p>
          </div>
        )}

        {!loading && !error && (
          <div className="orders-list">
            {filtered.map(order => (
              <OrderCard key={order.orderID || order.orderId || order.id} order={order} onCancel={fetchOrders} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default OrdersPage;
