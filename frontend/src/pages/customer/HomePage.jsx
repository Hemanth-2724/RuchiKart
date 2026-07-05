import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Search, X, MapPin, Clock, Star, Leaf, Flame, ChefHat, AlertTriangle, UtensilsCrossed } from 'lucide-react';
import Navbar from '../../components/layout/Navbar';
import api from '../../api/client';
import { useAuth } from '../../context/AuthContext';
import './HomePage.css';

const CUISINE_FILTERS = ['All', 'Veg', 'Non-Veg', 'Biryani', 'Fast Food', 'Desserts', 'Chinese', 'South Indian', 'Pizza'];

/* Cuisine → Unsplash food cover image mapping */
const getCuisineImage = (cuisine, name) => {
  const q = (cuisine || name || '').toLowerCase();
  if (q.includes('biryani'))      return 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=640&h=220&fit=crop';
  if (q.includes('south indian')) return 'https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=640&h=220&fit=crop';
  if (q.includes('chinese'))      return 'https://images.unsplash.com/photo-1563245372-f21724e3856d?w=640&h=220&fit=crop';
  if (q.includes('pizza'))        return 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=640&h=220&fit=crop';
  if (q.includes('fast food') || q.includes('burger')) return 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=640&h=220&fit=crop';
  if (q.includes('north indian')) return 'https://images.unsplash.com/photo-1505253758473-96b7015fcd40?w=640&h=220&fit=crop';
  if (q.includes('dessert') || q.includes('sweets')) return 'https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=640&h=220&fit=crop';
  return 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=640&h=220&fit=crop';
};

const RestaurantCard = ({ restaurant }) => {
  const imgSrc = restaurant.imagePath || restaurant.imageUrl || getCuisineImage(restaurant.cuisineType, restaurant.name);
  const rid = restaurant.restaurantID || restaurant.restaurantId || restaurant.id;

  return (
    <Link to={`/restaurant/${rid}`} className="rest-card glass-card">
      <div className="rest-card-image">
        <img
          src={imgSrc}
          alt={restaurant.name}
          onError={e => { e.target.src = 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=640&h=220&fit=crop'; }}
        />
        <div className="rest-card-img-overlay" />
        {restaurant.isActive === false && (
          <div className="rest-card-closed">Closed</div>
        )}
        <div className="rest-card-rating">
          <Star size={12} fill="currentColor" color="var(--warning)" />
          {restaurant.rating?.toFixed(1) || '4.5'}
        </div>
        {restaurant.isVeg && (
          <div className="rest-card-veg-badge">
            <Leaf size={11} /> Pure Veg
          </div>
        )}
      </div>
      <div className="rest-card-body">
        <div className="rest-card-cuisine">
          <span className="badge badge-orange">{restaurant.cuisineType || 'Multi-Cuisine'}</span>
        </div>
        <h3 className="rest-card-name">{restaurant.name}</h3>
        <p className="rest-card-address">
          <MapPin size={12} /> {restaurant.address || 'RR Nagar, Bengaluru'}
        </p>
        <div className="rest-card-footer">
          <span><Clock size={12} /> 25–35 min</span>
          <span>₹ for two</span>
        </div>
        <div className="rest-card-cta">
          <span>Order Now</span>
          <UtensilsCrossed size={16} />
        </div>
      </div>
    </Link>
  );
};

const HomePage = () => {
  const { user } = useAuth();
  const [restaurants, setRestaurants] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeFilter, setActiveFilter] = useState('All');
  const [search, setSearch] = useState('');

  useEffect(() => {
    const fetchRestaurants = async () => {
      try {
        const res = await api.get('/restaurants');
        setRestaurants(res.data || []);
        setFiltered(res.data || []);
      } catch {
        setError('Failed to load restaurants. Is the backend running?');
      } finally {
        setLoading(false);
      }
    };
    fetchRestaurants();
  }, []);

  useEffect(() => {
    let result = [...restaurants];
    if (activeFilter !== 'All') {
      result = result.filter(r =>
        r.cuisineType?.toLowerCase().includes(activeFilter.toLowerCase()) ||
        (activeFilter === 'Veg' && r.isVeg) ||
        (activeFilter === 'Non-Veg' && !r.isVeg)
      );
    }
    if (search.trim()) {
      const q = search.toLowerCase();
      result = result.filter(r =>
        r.name?.toLowerCase().includes(q) ||
        r.cuisineType?.toLowerCase().includes(q)
      );
    }
    setFiltered(result);
  }, [activeFilter, search, restaurants]);

  const hour = new Date().getHours();
  const greeting = hour < 12 ? 'Morning' : hour < 17 ? 'Afternoon' : 'Evening';

  return (
    <div className="page-wrapper">
      <Navbar />
      <div className="homepage">
        {/* ── Header ── */}
        <div className="home-header">
          <div className="home-header-bg" />
          <div className="home-header-orb home-orb-1" />
          <div className="home-header-orb home-orb-2" />
          <div className="container">
            <div className="home-greeting">
              <h1>
                Good {greeting},{' '}
                <span className="gradient-text">{user?.username}!</span>
              </h1>
              <p>What are you craving today?</p>
            </div>

            {/* Search Bar */}
            <div className="home-search-wrap">
              <div className="home-search">
                <Search size={18} color="var(--text-muted)" />
                <input
                  type="text"
                  placeholder="Search restaurants or cuisines…"
                  value={search}
                  onChange={e => setSearch(e.target.value)}
                  className="search-input"
                />
                {search && (
                  <button className="search-clear" onClick={() => setSearch('')}>
                    <X size={15} />
                  </button>
                )}
              </div>
            </div>
          </div>
        </div>

        <div className="container home-content">
          {/* ── Filter Bar ── */}
          <div className="filter-bar">
            {CUISINE_FILTERS.map(f => (
              <button
                key={f}
                className={`filter-chip ${activeFilter === f ? 'active' : ''}`}
                onClick={() => setActiveFilter(f)}
              >
                {f === 'Veg' && <Leaf size={12} />}
                {f === 'Non-Veg' && <Flame size={12} />}
                {f}
              </button>
            ))}
          </div>

          {/* ── Results count ── */}
          <div className="results-header">
            <h2 className="results-title">
              {activeFilter === 'All' ? 'All Restaurants' : activeFilter}
              <span className="results-count"> ({filtered.length})</span>
            </h2>
          </div>

          {/* ── States ── */}
          {loading && (
            <div className="loading-screen">
              <div className="spinner" />
              <p>Finding restaurants near you…</p>
            </div>
          )}
          {error && (
            <div className="glass-card error-state">
              <div className="error-icon"><AlertTriangle size={40} /></div>
              <h3>Oops!</h3>
              <p>{error}</p>
            </div>
          )}
          {!loading && !error && filtered.length === 0 && (
            <div className="empty-state">
              <div className="empty-icon"><ChefHat size={60} /></div>
              <h3>No restaurants found</h3>
              <p>Try a different filter or search term</p>
              <button className="btn-ghost" onClick={() => { setActiveFilter('All'); setSearch(''); }}>
                Clear Filters
              </button>
            </div>
          )}
          {!loading && !error && filtered.length > 0 && (
            <div className="restaurants-grid">
              {filtered.map(r => (
                <RestaurantCard key={r.restaurantID || r.restaurantId || r.id} restaurant={r} />
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default HomePage;
