import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Navbar from '../../components/layout/Navbar';
import api from '../../api/client';
import {
  Users, Store, Package, DollarSign, LayoutDashboard,
  Shield, Wifi, Lock, Zap, ChevronRight
} from 'lucide-react';
import './AdminDashboard.css';

const AdminDashboard = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const res = await api.get('/admin/dashboard');
        setStats(res.data);
      } catch {
        setStats({ totalUsers: 0, totalRestaurants: 0, totalOrders: 0, totalRevenue: 0 });
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  const KPI_CARDS = [
    {
      Icon: Users,
      label: 'Total Users',
      value: stats?.totalUsers ?? '—',
      sub: 'All registered users',
      color: 'rgba(72,149,239,0.18)',
      border: 'rgba(72,149,239,0.25)',
      iconColor: '#4895EF',
      gradient: 'linear-gradient(90deg, #4895EF, #3F37C9)',
    },
    {
      Icon: Store,
      label: 'Restaurants',
      value: stats?.totalRestaurants ?? '—',
      sub: 'Registered partners',
      color: 'rgba(255,107,53,0.18)',
      border: 'rgba(255,107,53,0.25)',
      iconColor: '#FF6B35',
      gradient: 'linear-gradient(90deg, #FF6B35, #E63946)',
    },
    {
      Icon: Package,
      label: 'Total Orders',
      value: stats?.totalOrders ?? '—',
      sub: 'All time orders',
      color: 'rgba(255,209,102,0.18)',
      border: 'rgba(255,209,102,0.25)',
      iconColor: '#FFD166',
      gradient: 'linear-gradient(90deg, #FFD166, #F7931E)',
    },
    {
      Icon: DollarSign,
      label: 'Total Revenue',
      value: stats?.totalRevenue ? `₹${Number(stats.totalRevenue).toFixed(0)}` : '₹—',
      sub: 'Platform earnings',
      color: 'rgba(6,214,160,0.18)',
      border: 'rgba(6,214,160,0.25)',
      iconColor: '#06D6A0',
      gradient: 'linear-gradient(90deg, #06D6A0, #1B7F6A)',
    },
  ];

  const QUICK_ACTIONS = [
    {
      Icon: Users,
      iconBg: 'rgba(72,149,239,0.15)',
      iconColor: '#4895EF',
      title: 'Users Management',
      desc: 'View, edit roles, delete users',
      link: '/admin/users',
    },
    {
      Icon: Store,
      iconBg: 'rgba(255,107,53,0.15)',
      iconColor: '#FF6B35',
      title: 'Restaurants',
      desc: 'Activate, deactivate, monitor partners',
      link: '/admin/restaurants',
    },
    {
      Icon: LayoutDashboard,
      iconBg: 'rgba(6,214,160,0.15)',
      iconColor: '#06D6A0',
      title: 'Platform Stats',
      desc: 'Revenue analytics and insights',
      link: '/admin/dashboard',
    },
  ];

  const OVERVIEW_ITEMS = [
    { Icon: Wifi,    iconBg: 'rgba(6,214,160,0.15)',   iconColor: '#06D6A0', title: 'Platform Status', sub: 'All systems operational',  subColor: 'var(--success)' },
    { Icon: Store,   iconBg: 'rgba(255,107,53,0.15)',   iconColor: '#FF6B35', title: 'Service Area',    sub: 'RR Nagar, Bengaluru',       subColor: 'var(--text-secondary)' },
    { Icon: Lock,    iconBg: 'rgba(72,149,239,0.15)',   iconColor: '#4895EF', title: 'Security',        sub: 'All endpoints secured',      subColor: 'var(--success)' },
    { Icon: Zap,     iconBg: 'rgba(255,209,102,0.15)',  iconColor: '#FFD166', title: 'API Performance', sub: 'Response < 200ms',           subColor: 'var(--text-secondary)' },
  ];

  return (
    <div className="page-wrapper">
      <Navbar />

      {/* ── Hero Banner ── */}
      <div className="admin-hero-banner">
        <div className="admin-hero-bg" />
        <div className="admin-hero-grid-overlay" />
        <div className="admin-hero-orb admin-orb-1" />
        <div className="admin-hero-orb admin-orb-2" />
        <div className="container admin-hero-inner">
          <div className="admin-hero-left">
            <div className="admin-hero-icon">
              <Shield size={30} color="#4895EF" />
            </div>
            <div className="admin-hero-text">
              <h1>Admin Control Panel</h1>
              <p>Manage the entire RuchiKart platform from here</p>
            </div>
          </div>
          <div className="admin-hero-actions">
            <Link to="/admin/users" className="btn-primary">
              <Users size={16} /> Manage Users
            </Link>
            <Link to="/admin/restaurants" className="btn-ghost">
              <Store size={16} /> Manage Restaurants
            </Link>
          </div>
        </div>
      </div>

      <div className="container admin-dashboard">
        {/* ── KPI Cards ── */}
        {loading ? (
          <div className="loading-screen"><div className="spinner" /></div>
        ) : (
          <div className="grid-4" style={{ marginBottom: '40px' }}>
            {KPI_CARDS.map(card => (
              <div
                key={card.label}
                className="admin-kpi-card"
                style={{ '--kpi-gradient': card.gradient }}
              >
                <div className="admin-kpi-icon" style={{ background: card.color, border: `1px solid ${card.border}` }}>
                  <card.Icon size={22} color={card.iconColor} />
                </div>
                <div className="admin-kpi-value">{card.value}</div>
                <div className="admin-kpi-label">{card.label}</div>
                <div className="admin-kpi-sub">{card.sub}</div>
              </div>
            ))}
          </div>
        )}

        {/* ── Quick Actions ── */}
        <div className="admin-section-title">
          <Zap size={18} color="var(--primary)" />
          Quick Actions
        </div>
        <div className="grid-3" style={{ marginBottom: '40px' }}>
          {QUICK_ACTIONS.map(action => (
            <Link key={action.title} to={action.link} className="glass-card admin-action-card">
              <div className="admin-action-icon" style={{ background: action.iconBg }}>
                <action.Icon size={22} color={action.iconColor} />
              </div>
              <h4>{action.title}</h4>
              <p>{action.desc}</p>
              <span className="admin-action-arrow"><ChevronRight size={18} /></span>
            </Link>
          ))}
        </div>

        {/* ── Platform Overview ── */}
        <div className="admin-section-title">
          <LayoutDashboard size={18} color="var(--primary)" />
          Platform Overview
        </div>
        <div className="glass-card admin-activity-card">
          <div style={{ padding: '24px 28px' }}>
            <div className="grid-2">
              {OVERVIEW_ITEMS.map(item => (
                <div key={item.title} className="admin-overview-item">
                  <div className="admin-overview-icon" style={{ background: item.iconBg }}>
                    <item.Icon size={18} color={item.iconColor} />
                  </div>
                  <div>
                    <div className="admin-overview-text-title">{item.title}</div>
                    <div style={{ fontSize: '0.875rem', color: item.subColor }}>{item.sub}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
