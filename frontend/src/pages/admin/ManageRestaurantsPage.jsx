import React, { useState, useEffect } from 'react';
import Navbar from '../../components/layout/Navbar';
import api from '../../api/client';
import toast from 'react-hot-toast';
import { Store, CheckCircle, XCircle, Star, MapPin, Search, ToggleLeft, ToggleRight } from 'lucide-react';
import './ManageRestaurantsPage.css';

const CUISINE_GRADIENTS = {
  'Biryani':      'linear-gradient(135deg, #f7931e, #e63946)',
  'South Indian': 'linear-gradient(135deg, #06D6A0, #1B7F6A)',
  'Chinese':      'linear-gradient(135deg, #E63946, #C1121F)',
  'Default':      'linear-gradient(135deg, #FF6B35, #E63946)',
};

const ManageRestaurantsPage = () => {
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading]         = useState(true);
  const [search, setSearch]           = useState('');
  const [togglingId, setTogglingId]   = useState(null);

  const fetchRestaurants = async () => {
    try {
      const res = await api.get('/admin/restaurants');
      setRestaurants(res.data || []);
    } catch {
      toast.error('Failed to load restaurants');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchRestaurants(); }, []);

  const handleToggle = async (restaurant) => {
    const id = restaurant.restaurantID || restaurant.restaurantId || restaurant.id;
    setTogglingId(id);
    try {
      await api.put(`/admin/restaurants/${id}`, { isActive: !restaurant.isActive });
      toast.success(`${restaurant.name} ${!restaurant.isActive ? 'activated' : 'deactivated'}`);
      fetchRestaurants();
    } catch {
      toast.error('Failed to update restaurant status');
    } finally {
      setTogglingId(null);
    }
  };

  const filtered = restaurants.filter(r =>
    !search ||
    r.name?.toLowerCase().includes(search.toLowerCase()) ||
    r.cuisineType?.toLowerCase().includes(search.toLowerCase())
  );

  const activeCount = restaurants.filter(r => r.isActive !== false).length;

  return (
    <div className="page-wrapper">
      <Navbar />

      {/* ── Hero Banner ── */}
      <div className="rest-admin-hero-banner">
        <div className="rest-admin-hero-bg" />
        <div className="rest-admin-hero-orb rest-orb-1" />
        <div className="rest-admin-hero-orb rest-orb-2" />
        <div className="container rest-admin-hero-inner">
          <div className="rest-admin-hero-left">
            <div className="rest-admin-hero-icon">
              <Store size={28} color="#FF6B35" />
            </div>
            <div>
              <h1>Manage Restaurants</h1>
              <p>Activate, deactivate and monitor all restaurant partners</p>
            </div>
          </div>
        </div>
      </div>

      <div className="container manage-restaurants-page">
        {/* ── Summary Stats ── */}
        <div className="grid-3" style={{ marginBottom: '32px' }}>
          <div className="stat-card">
            <div className="stat-icon" style={{ background: 'rgba(255,107,53,0.15)' }}>
              <Store size={22} color="var(--primary)" />
            </div>
            <div className="stat-value">{restaurants.length}</div>
            <div className="stat-label">Total Restaurants</div>
          </div>
          <div className="stat-card">
            <div className="stat-icon" style={{ background: 'rgba(6,214,160,0.15)' }}>
              <CheckCircle size={22} color="var(--success)" />
            </div>
            <div className="stat-value">{activeCount}</div>
            <div className="stat-label">Active</div>
          </div>
          <div className="stat-card">
            <div className="stat-icon" style={{ background: 'rgba(239,71,111,0.15)' }}>
              <XCircle size={22} color="var(--error)" />
            </div>
            <div className="stat-value">{restaurants.length - activeCount}</div>
            <div className="stat-label">Inactive</div>
          </div>
        </div>

        {/* ── Search ── */}
        <div className="home-search" style={{ maxWidth: '420px', marginBottom: '28px' }}>
          <Search size={17} color="var(--text-muted)" />
          <input
            type="text"
            className="search-input"
            placeholder="Search restaurants or cuisine..."
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>

        {loading ? (
          <div className="loading-screen"><div className="spinner" /></div>
        ) : (
          <div className="restaurants-admin-grid">
            {filtered.map(r => {
              const grad     = Object.entries(CUISINE_GRADIENTS).find(([k]) => r.cuisineType?.includes(k))?.[1] || CUISINE_GRADIENTS.Default;
              const isActive = r.isActive !== false;
              const rid      = r.restaurantID || r.restaurantId || r.id;
              return (
                <div key={rid} className={`rest-admin-card glass-card ${!isActive ? 'inactive' : ''}`}>
                  <div className="rest-admin-banner" style={{ background: grad }}>
                    <Store size={36} color="rgba(255,255,255,0.65)" />
                    {!isActive && <div className="rest-admin-inactive-overlay">INACTIVE</div>}
                  </div>
                  <div className="rest-admin-body">
                    <div className="rest-admin-header">
                      <div>
                        <h4 className="rest-admin-name">{r.name}</h4>
                        <span className="badge badge-orange" style={{ fontSize: '0.7rem' }}>
                          {r.cuisineType || 'Multi-Cuisine'}
                        </span>
                      </div>
                      <span className={`badge ${isActive ? 'badge-green' : 'badge-red'}`}>
                        {isActive
                          ? <><CheckCircle size={11} /> Active</>
                          : <><XCircle size={11} /> Inactive</>}
                      </span>
                    </div>
                    <div className="rest-admin-meta">
                      <span><Star size={13} color="var(--warning)" fill="var(--warning)" /> {r.rating?.toFixed(1) || '4.5'}</span>
                      <span><MapPin size={13} color="var(--text-muted)" /> {r.address || 'RR Nagar'}</span>
                    </div>
                    <button
                      className={isActive ? 'btn-danger' : 'btn-primary'}
                      style={{ width: '100%', justifyContent: 'center', padding: '11px', fontSize: '0.875rem' }}
                      onClick={() => handleToggle(r)}
                      disabled={togglingId === rid}
                    >
                      {togglingId === rid
                        ? <div className="spinner-sm" />
                        : isActive
                          ? <><ToggleLeft size={16} /> Deactivate</>
                          : <><ToggleRight size={16} /> Activate</>}
                    </button>
                  </div>
                </div>
              );
            })}

            {filtered.length === 0 && (
              <div className="empty-state" style={{ gridColumn: '1/-1' }}>
                <div className="empty-icon"><Store size={56} /></div>
                <h3>No restaurants found</h3>
                <p>Try a different search term</p>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default ManageRestaurantsPage;
