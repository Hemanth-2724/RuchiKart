import React, { useState, useEffect } from 'react';
import Navbar from '../../components/layout/Navbar';
import { useAuth } from '../../context/AuthContext';
import api from '../../api/client';
import toast from 'react-hot-toast';
import {
  User, Mail, MapPin, Package, CheckCircle, Star,
  Lock, Shield, Edit3, Save, X
} from 'lucide-react';
import './ProfilePage.css';

const ProfilePage = () => {
  const { user, fetchCurrentUser } = useAuth();
  const [editing, setEditing] = useState(false);
  const [form, setForm] = useState({
    username: user?.username || '',
    email: user?.email || '',
    address: user?.address || '',
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (user) {
      setForm({
        username: user.username || '',
        email: user.email || '',
        address: user.address || '',
      });
    }
  }, [user]);

  const initials = user?.username?.charAt(0)?.toUpperCase() || '?';

  const handleSave = async () => {
    setLoading(true);
    try {
      await api.put('/auth/profile', form);
      await fetchCurrentUser();
      toast.success('Profile updated successfully!');
      setEditing(false);
    } catch (err) {
      toast.error(err?.response?.data?.message || 'Failed to update profile');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-wrapper">
      <Navbar />
      <div className="profile-page">
        {/* ── Profile Hero Banner ── */}
        <div className="profile-hero-banner">
          <div className="profile-hero-bg" />
          <div className="profile-hero-orb profile-orb-1" />
          <div className="profile-hero-orb profile-orb-2" />
          <div className="container profile-hero-inner">
            <div className="profile-avatar-wrap">
              <div className="profile-avatar-ring" />
              <div className="profile-avatar">{initials}</div>
            </div>
            <div className="profile-hero-info">
              <h1 className="profile-username">{user?.username}</h1>
              <div className="profile-meta">
                <span className="badge badge-orange">
                  {user?.role?.replace(/_/g, ' ')?.replace(/\b\w/g, c => c.toUpperCase())}
                </span>
                <span className="profile-email-chip">
                  <Mail size={13} />
                  {user?.email || 'No email set'}
                </span>
                {user?.address && (
                  <span className="profile-address-chip">
                    <MapPin size={13} />
                    {user.address}
                  </span>
                )}
              </div>
            </div>
          </div>
        </div>

        <div className="container">
          <div className="profile-grid">
            {/* Edit Profile */}
            <div className="glass-card profile-card">
              <div className="profile-card-header">
                <div className="profile-card-title">
                  <User size={18} color="var(--primary)" />
                  <h3>Profile Details</h3>
                </div>
                <button
                  className={editing ? 'btn-ghost' : 'btn-secondary'}
                  style={{ padding: '8px 18px', fontSize: '0.85rem' }}
                  onClick={() => setEditing(!editing)}
                >
                  {editing ? <><X size={14} /> Cancel</> : <><Edit3 size={14} /> Edit</>}
                </button>
              </div>

              <div className="form-group">
                <label>Username</label>
                <input
                  className="input-field"
                  value={form.username}
                  disabled={!editing}
                  onChange={e => setForm(f => ({ ...f, username: e.target.value }))}
                  placeholder="Enter your username"
                />
              </div>

              <div className="form-group">
                <label>Email Address</label>
                <input
                  className="input-field"
                  type="email"
                  value={form.email}
                  disabled={!editing}
                  onChange={e => setForm(f => ({ ...f, email: e.target.value }))}
                  placeholder="your@email.com"
                />
              </div>

              <div className="form-group">
                <label>Delivery Address</label>
                <textarea
                  className="input-field"
                  value={form.address}
                  disabled={!editing}
                  onChange={e => setForm(f => ({ ...f, address: e.target.value }))}
                  rows={3}
                  style={{ resize: 'none' }}
                  placeholder="Your delivery address..."
                />
              </div>

              {editing && (
                <button className="btn-primary" onClick={handleSave} disabled={loading} style={{ width: '100%', justifyContent: 'center' }}>
                  {loading ? <><div className="spinner-sm" /> Saving...</> : <><Save size={15} /> Save Changes</>}
                </button>
              )}
            </div>

            {/* Right Column */}
            <div className="profile-stats-col">
              {/* Account Stats */}
              <div className="glass-card profile-card">
                <div className="profile-card-title" style={{ marginBottom: '20px', paddingBottom: '12px', borderBottom: '1px solid rgba(255,255,255,0.07)' }}>
                  <Star size={18} color="var(--warning)" />
                  <h3>Account Stats</h3>
                </div>
                <div className="profile-stats">
                  <div className="profile-stat">
                    <div className="profile-stat-icon" style={{ background: 'rgba(255,107,53,0.15)' }}>
                      <Package size={20} color="var(--primary)" />
                    </div>
                    <div>
                      <div className="profile-stat-val">—</div>
                      <div className="profile-stat-label">Total Orders</div>
                    </div>
                  </div>
                  <div className="profile-stat">
                    <div className="profile-stat-icon" style={{ background: 'rgba(6,214,160,0.15)' }}>
                      <CheckCircle size={20} color="var(--success)" />
                    </div>
                    <div>
                      <div className="profile-stat-val">—</div>
                      <div className="profile-stat-label">Delivered</div>
                    </div>
                  </div>
                  <div className="profile-stat">
                    <div className="profile-stat-icon" style={{ background: 'rgba(255,209,102,0.15)' }}>
                      <Star size={20} color="var(--warning)" />
                    </div>
                    <div>
                      <div className="profile-stat-val">4.8</div>
                      <div className="profile-stat-label">Avg Rating</div>
                    </div>
                  </div>
                </div>
              </div>

              {/* Account Security */}
              <div className="glass-card profile-card">
                <div className="profile-card-title" style={{ marginBottom: '20px', paddingBottom: '12px', borderBottom: '1px solid rgba(255,255,255,0.07)' }}>
                  <Shield size={18} color="var(--accent-teal)" />
                  <h3>Account Security</h3>
                </div>
                <div className="security-items">
                  <div className="security-item">
                    <span className="security-item-label"><Lock size={14} /> Password</span>
                    <span className="badge badge-green">Protected</span>
                  </div>
                  <div className="security-item">
                    <span className="security-item-label"><Mail size={14} /> Email verified</span>
                    <span className="badge badge-yellow">Pending</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
