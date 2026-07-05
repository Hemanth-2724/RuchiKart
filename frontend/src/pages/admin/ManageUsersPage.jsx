import React, { useState, useEffect } from 'react';
import Navbar from '../../components/layout/Navbar';
import api from '../../api/client';
import toast from 'react-hot-toast';
import { Users, Search, Trash2, UserCog } from 'lucide-react';
import './ManageUsersPage.css';

const ROLES = ['customer', 'restaurant_owner', 'delivery_partner', 'admin'];

const ROLE_BADGES = {
  customer:         'badge-blue',
  restaurant_owner: 'badge-orange',
  delivery_partner: 'badge-yellow',
  admin:            'badge-red',
};

const ManageUsersPage = () => {
  const [users, setUsers]               = useState([]);
  const [loading, setLoading]           = useState(true);
  const [search, setSearch]             = useState('');
  const [deleteConfirm, setDeleteConfirm] = useState(null);
  const [updatingId, setUpdatingId]     = useState(null);

  const fetchUsers = async () => {
    try {
      const res = await api.get('/admin/users');
      setUsers(res.data || []);
    } catch {
      toast.error('Failed to load users');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchUsers(); }, []);

  const handleRoleUpdate = async (userId, role) => {
    setUpdatingId(userId);
    try {
      await api.put(`/admin/users/${userId}`, { role });
      toast.success('Role updated!');
      fetchUsers();
    } catch {
      toast.error('Failed to update role');
    } finally {
      setUpdatingId(null);
    }
  };

  const handleDelete = async (userId) => {
    try {
      await api.delete(`/admin/users/${userId}`);
      toast.success('User deleted');
      setDeleteConfirm(null);
      fetchUsers();
    } catch {
      toast.error('Failed to delete user');
    }
  };

  const filtered = users.filter(u =>
    !search ||
    u.username?.toLowerCase().includes(search.toLowerCase()) ||
    u.email?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="page-wrapper">
      <Navbar />

      {/* ── Hero Banner ── */}
      <div className="users-hero-banner">
        <div className="users-hero-bg" />
        <div className="users-hero-orb users-orb-1" />
        <div className="users-hero-orb users-orb-2" />
        <div className="container users-hero-inner">
          <div className="users-hero-icon">
            <Users size={28} color="#4895EF" />
          </div>
          <div>
            <h1>Manage Users</h1>
            <p>View, update roles, and manage all platform users</p>
          </div>
        </div>
      </div>

      <div className="container manage-users-page">
        {/* ── Search & Count ── */}
        <div className="users-search-wrap" style={{ marginBottom: '28px' }}>
          <div className="home-search" style={{ maxWidth: '420px' }}>
            <Search size={17} color="var(--text-muted)" />
            <input
              type="text"
              className="search-input"
              placeholder="Search by username or email..."
              value={search}
              onChange={e => setSearch(e.target.value)}
            />
          </div>
          <span className="badge badge-gray">{filtered.length} users</span>
        </div>

        {loading ? (
          <div className="loading-screen"><div className="spinner" /></div>
        ) : filtered.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon"><Users size={56} /></div>
            <h3>No users found</h3>
            <p>Try a different search term</p>
          </div>
        ) : (
          <div className="glass-card" style={{ overflow: 'hidden' }}>
            <div style={{ overflowX: 'auto' }}>
              <table className="data-table">
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Joined</th>
                    <th>Update Role</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filtered.map((u, i) => (
                    <tr key={u.userID || u.userId || u.id}>
                      <td style={{ color: 'var(--text-muted)' }}>{i + 1}</td>
                      <td>
                        <div className="user-cell">
                          <div className="user-avatar-sm">{u.username?.charAt(0).toUpperCase()}</div>
                          <span style={{ fontFamily: 'Poppins, sans-serif', fontWeight: 700, color: 'white' }}>
                            {u.username}
                          </span>
                        </div>
                      </td>
                      <td>{u.email || '—'}</td>
                      <td>
                        <span className={`badge ${ROLE_BADGES[u.role] || 'badge-gray'}`}>
                          {u.role?.replace(/_/g, ' ') || 'unknown'}
                        </span>
                      </td>
                      <td>
                        {(u.createdDate || u.createdAt)
                          ? new Date(u.createdDate || u.createdAt).toLocaleDateString('en-IN')
                          : '—'}
                      </td>
                      <td>
                        <select
                          className="input-field"
                          style={{ padding: '7px 12px', fontSize: '0.8rem', minWidth: '155px', borderRadius: '8px' }}
                          value={u.role}
                          onChange={e => handleRoleUpdate(u.userID || u.userId || u.id, e.target.value)}
                          disabled={updatingId === (u.userID || u.userId || u.id)}
                        >
                          {ROLES.map(r => (
                            <option key={r} value={r}>{r.replace(/_/g, ' ')}</option>
                          ))}
                        </select>
                      </td>
                      <td>
                        <button
                          className="btn-danger"
                          style={{ padding: '7px 14px', fontSize: '0.8rem', gap: '6px' }}
                          onClick={() => setDeleteConfirm(u.userID || u.userId || u.id)}
                        >
                          <Trash2 size={14} /> Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* ── Delete Confirmation Modal ── */}
        {deleteConfirm && (
          <div className="modal-overlay" onClick={() => setDeleteConfirm(null)}>
            <div className="modal-content" style={{ maxWidth: '400px' }} onClick={e => e.stopPropagation()}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '14px' }}>
                <div style={{ width: 44, height: 44, borderRadius: 12, background: 'rgba(239,71,111,0.15)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <Trash2 size={20} color="var(--error)" />
                </div>
                <h3 style={{ margin: 0 }}>Delete User</h3>
              </div>
              <p style={{ color: 'var(--text-secondary)', marginBottom: '28px', lineHeight: 1.6 }}>
                Are you sure you want to delete this user? All their data will be permanently removed.
              </p>
              <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end' }}>
                <button className="btn-ghost" onClick={() => setDeleteConfirm(null)}>Cancel</button>
                <button className="btn-danger" onClick={() => handleDelete(deleteConfirm)}>
                  <Trash2 size={14} /> Delete User
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ManageUsersPage;
