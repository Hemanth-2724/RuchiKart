import React, { useState, useEffect } from 'react';
import Navbar from '../../components/layout/Navbar';
import api from '../../api/client';
import toast from 'react-hot-toast';
import {
  UtensilsCrossed, Plus, Edit3, Trash2, X, Save, Leaf, Flame
} from 'lucide-react';
import './ManageMenuPage.css';

const EMPTY_FORM = { itemName: '', description: '', price: '', isAvailable: true, isVeg: true };

const MenuModal = ({ item, onClose, onSave }) => {
  const [form, setForm]     = useState(item || EMPTY_FORM);
  const [loading, setLoading] = useState(false);
  const isEdit = !!(item?.menuID || item?.menuItemId);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.itemName || !form.price) {
      toast.error('Item name and price are required');
      return;
    }
    setLoading(true);
    try {
      await onSave(form, isEdit);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            {isEdit ? <Edit3 size={20} color="var(--primary)" /> : <Plus size={20} color="var(--primary)" />}
            <h3>{isEdit ? 'Edit Menu Item' : 'Add Menu Item'}</h3>
          </div>
          <button className="modal-close" onClick={onClose}><X size={16} /></button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Item Name *</label>
            <input
              className="input-field"
              placeholder="e.g., Chicken Biryani"
              value={form.itemName}
              onChange={e => setForm(f => ({ ...f, itemName: e.target.value }))}
            />
          </div>

          <div className="form-group">
            <label>Description</label>
            <textarea
              className="input-field"
              placeholder="Short description of the dish..."
              value={form.description}
              onChange={e => setForm(f => ({ ...f, description: e.target.value }))}
              rows={2}
              style={{ resize: 'none' }}
            />
          </div>

          <div className="form-row-modal">
            <div className="form-group">
              <label>Price (₹) *</label>
              <input
                className="input-field"
                type="number"
                placeholder="0"
                value={form.price}
                onChange={e => setForm(f => ({ ...f, price: e.target.value }))}
                min="0"
              />
            </div>
            <div className="form-group">
              <label>Type</label>
              <div className="custom-segmented-control">
                <button
                  type="button"
                  className={`segment-btn veg ${form.isVeg ? 'active' : ''}`}
                  onClick={() => setForm(f => ({ ...f, isVeg: true }))}
                >
                  <Leaf size={13} />
                  <span>Veg</span>
                </button>
                <button
                  type="button"
                  className={`segment-btn nonveg ${!form.isVeg ? 'active' : ''}`}
                  onClick={() => setForm(f => ({ ...f, isVeg: false }))}
                >
                  <Flame size={13} />
                  <span>Non-Veg</span>
                </button>
              </div>
            </div>
          </div>

          <div className="form-group toggle-group">
            <label>Availability</label>
            <label className="toggle-switch">
              <input
                type="checkbox"
                checked={form.isAvailable}
                onChange={e => setForm(f => ({ ...f, isAvailable: e.target.checked }))}
              />
              <span className="toggle-slider" />
            </label>
            <span style={{ fontSize: '0.875rem', color: form.isAvailable ? 'var(--success)' : 'var(--text-muted)' }}>
              {form.isAvailable ? 'Available' : 'Unavailable'}
            </span>
          </div>

          <div className="modal-actions">
            <button type="button" className="btn-ghost" onClick={onClose}>Cancel</button>
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading
                ? <div className="spinner-sm" />
                : isEdit
                  ? <><Save size={14} /> Save Changes</>
                  : <><Plus size={14} /> Add Item</>}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

const ManageMenuPage = () => {
  const [menu, setMenu]               = useState([]);
  const [loading, setLoading]         = useState(true);
  const [modalItem, setModalItem]     = useState(null);
  const [showModal, setShowModal]     = useState(false);
  const [deleteConfirm, setDeleteConfirm] = useState(null);

  const fetchMenu = async () => {
    try {
      const res = await api.get('/owner/menu');
      setMenu(res.data || []);
    } catch {
      toast.error('Failed to load menu');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchMenu(); }, []);

  const handleSave = async (form, isEdit) => {
    try {
      const payload = {
        itemName:    form.itemName,
        description: form.description,
        price:       parseFloat(form.price),
        isAvailable: form.isAvailable,
        isVeg:       form.isVeg,
      };
      if (isEdit) {
        await api.put(`/owner/menu/${form.menuID || form.menuItemId}`, payload);
        toast.success('Menu item updated!');
      } else {
        await api.post('/owner/menu', payload);
        toast.success('Menu item added!');
      }
      setShowModal(false);
      setModalItem(null);
      fetchMenu();
    } catch {
      toast.error('Failed to save menu item');
    }
  };

  const handleDelete = async (id) => {
    try {
      await api.delete(`/owner/menu/${id}`);
      toast.success('Item deleted');
      setDeleteConfirm(null);
      fetchMenu();
    } catch {
      toast.error('Failed to delete item');
    }
  };

  const toggleAvailability = async (item) => {
    try {
      await api.put(`/owner/menu/${item.menuID || item.menuItemId || item.id}`, { ...item, isAvailable: !item.isAvailable });
      toast.success(`${item.itemName} ${!item.isAvailable ? 'enabled' : 'disabled'}`);
      fetchMenu();
    } catch {
      toast.error('Failed to update availability');
    }
  };

  return (
    <div className="page-wrapper">
      <Navbar />

      {/* ── Hero Banner ── */}
      <div className="menu-hero-banner">
        <div className="menu-hero-bg" />
        <div className="menu-hero-orb menu-orb-1" />
        <div className="container menu-hero-inner">
          <div className="menu-hero-left">
            <div className="menu-hero-icon">
              <UtensilsCrossed size={28} color="var(--primary)" />
            </div>
            <div>
              <h1>Manage Menu</h1>
              <p>Add and manage your restaurant's menu items</p>
            </div>
          </div>
          <button className="btn-primary" onClick={() => { setModalItem(null); setShowModal(true); }}>
            <Plus size={16} /> Add Item
          </button>
        </div>
      </div>

      <div className="container menu-manage-page">
        {loading ? (
          <div className="loading-screen"><div className="spinner" /></div>
        ) : menu.length === 0 ? (
          <div className="empty-state glass-card" style={{ padding: '72px 24px' }}>
            <div className="empty-icon"><UtensilsCrossed size={56} /></div>
            <h3>No menu items yet</h3>
            <p>Start adding dishes to your menu</p>
            <button className="btn-primary" onClick={() => setShowModal(true)}>
              <Plus size={15} /> Add First Item
            </button>
          </div>
        ) : (
          <div className="glass-card" style={{ overflow: 'hidden' }}>
            <div style={{ overflowX: 'auto' }}>
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Item Name</th>
                    <th>Description</th>
                    <th>Price</th>
                    <th>Type</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {menu.map(item => (
                    <tr key={item.menuID || item.menuItemId || item.id}>
                      <td style={{ fontFamily: 'Poppins, sans-serif', fontWeight: 700, color: 'white' }}>
                        {item.itemName}
                      </td>
                      <td style={{ maxWidth: '200px' }}>
                        <span style={{ display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
                          {item.description || '—'}
                        </span>
                      </td>
                      <td style={{ color: 'var(--primary)', fontFamily: 'Poppins, sans-serif', fontWeight: 700 }}>
                        ₹{item.price?.toFixed(0)}
                      </td>
                      <td>
                        <span className={`badge ${item.isVeg ? 'badge-green' : 'badge-red'}`}
                          style={{ display: 'inline-flex', alignItems: 'center', gap: 5 }}>
                          {item.isVeg ? <Leaf size={11} /> : <Flame size={11} />}
                          {item.isVeg ? 'Veg' : 'Non-Veg'}
                        </span>
                      </td>
                      <td>
                        <label className="toggle-switch" title="Toggle availability">
                          <input
                            type="checkbox"
                            checked={item.isAvailable}
                            onChange={() => toggleAvailability(item)}
                          />
                          <span className="toggle-slider" />
                        </label>
                      </td>
                      <td>
                        <div style={{ display: 'flex', gap: '8px' }}>
                          <button
                            className="btn-secondary"
                            style={{ padding: '6px 14px', fontSize: '0.8rem', gap: 6 }}
                            onClick={() => { setModalItem(item); setShowModal(true); }}
                          >
                            <Edit3 size={13} /> Edit
                          </button>
                          <button
                            className="btn-danger"
                            style={{ padding: '6px 12px', fontSize: '0.8rem' }}
                            onClick={() => setDeleteConfirm(item.menuID || item.menuItemId || item.id)}
                          >
                            <Trash2 size={13} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Add/Edit Modal */}
        {showModal && (
          <MenuModal
            item={modalItem}
            onClose={() => { setShowModal(false); setModalItem(null); }}
            onSave={handleSave}
          />
        )}

        {/* Delete Confirmation */}
        {deleteConfirm && (
          <div className="modal-overlay" onClick={() => setDeleteConfirm(null)}>
            <div className="modal-content" style={{ maxWidth: '390px' }} onClick={e => e.stopPropagation()}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 14 }}>
                <div style={{ width: 44, height: 44, borderRadius: 12, background: 'rgba(239,71,111,0.15)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <Trash2 size={20} color="var(--error)" />
                </div>
                <h3 style={{ margin: 0 }}>Delete Item</h3>
              </div>
              <p style={{ color: 'var(--text-secondary)', marginBottom: '28px', lineHeight: 1.6 }}>
                Are you sure you want to delete this item? This cannot be undone.
              </p>
              <div className="modal-actions">
                <button className="btn-ghost" onClick={() => setDeleteConfirm(null)}>Cancel</button>
                <button className="btn-danger" onClick={() => handleDelete(deleteConfirm)}>
                  <Trash2 size={14} /> Delete
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ManageMenuPage;
