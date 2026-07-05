import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  ArrowLeft, Star, Clock, MapPin, ShoppingCart,
  Plus, Minus, X, Leaf, Flame, ChefHat, Info,
  ShoppingBag, Truck, Store
} from 'lucide-react';
import Navbar from '../../components/layout/Navbar';
import api from '../../api/client';
import { useCartContext } from '../../context/CartContext';
import toast from 'react-hot-toast';
import './RestaurantDetailPage.css';

/* ── Menu Item Detail Modal ──────────────────────── */
const MenuDetailModal = ({ item, cartItem, onClose, onAdd, onUpdateQty }) => {
  const qty = cartItem?.quantity || 0;
  const menuId = item.menuID || item.menuItemId || item.id;

  useEffect(() => {
    document.body.style.overflow = 'hidden';
    return () => { document.body.style.overflow = ''; };
  }, []);

  return (
    <div className="menu-modal-overlay" onClick={onClose}>
      <div className="menu-modal-card" onClick={e => e.stopPropagation()}>
        {/* Image */}
        <div className="menu-modal-img-wrap">
          <img
            src={item.imagePath || item.imageUrl || `https://source.unsplash.com/560x260/?${encodeURIComponent(item.itemName)},food`}
            alt={item.itemName}
            onError={e => { e.target.src = 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=560&h=260&fit=crop'; }}
          />
          <div className="menu-modal-img-overlay" />
          <div className="menu-modal-price-badge">₹{item.price?.toFixed(0)}</div>
          <button className="menu-modal-close" onClick={onClose}><X size={16} /></button>
        </div>

        {/* Info */}
        <div className="menu-modal-body">
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '10px', marginBottom: '8px' }}>
            <h3 className="menu-modal-title">{item.itemName}</h3>
            <span className={`food-type-pill ${item.isVeg == true ? 'veg' : 'nonveg'}`}>
              {item.isVeg == true ? <Leaf size={12} /> : <Flame size={12} />}
              {item.isVeg == true ? 'Veg' : 'Non-Veg'}
            </span>
          </div>

          <p className="menu-modal-desc">{item.description || 'No description available for this item.'}</p>

          <div className="menu-modal-footer">
            <span className="menu-modal-status-text">
              {item.isAvailable ? 'Available to order' : 'Currently unavailable'}
            </span>
            {qty === 0 ? (
              <button
                className="btn-primary"
                onClick={() => { onAdd(item); onClose(); }}
                disabled={!item.isAvailable}
                style={{ padding: '10px 24px', fontSize: '0.9rem' }}
              >
                <Plus size={16} /> Add to Cart
              </button>
            ) : (
              <div className="qty-control" style={{ gap: '8px' }}>
                <button className="qty-btn" style={{ width: '34px', height: '34px' }}
                  onClick={() => onUpdateQty(menuId, qty - 1)}>
                  <Minus size={14} />
                </button>
                <span className="qty-value" style={{ fontSize: '0.95rem' }}>{qty}</span>
                <button className="qty-btn" style={{ width: '34px', height: '34px' }}
                  onClick={() => onUpdateQty(menuId, qty + 1)}>
                  <Plus size={14} />
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

/* ── Menu Item Card ──────────────────────────────── */
const MenuItemCard = ({ item, cartItems, onAdd, onUpdateQty, onOpenModal }) => {
  const cartItem = cartItems.find(c => c.menuId === (item.menuID || item.menuItemId || item.id));
  const qty = cartItem?.quantity || 0;
  const menuId = item.menuID || item.menuItemId || item.id;

  return (
    <div className="menu-item-card glass-card" onClick={() => onOpenModal(item)}>
      {/* Food image */}
      <div className="menu-item-img-wrap">
        <img
          src={item.imagePath || item.imageUrl || `https://source.unsplash.com/320x180/?${encodeURIComponent(item.itemName)},food`}
          alt={item.itemName}
          onError={e => { e.target.src = 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=320&h=180&fit=crop'; }}
        />
        <div className="menu-item-img-overlay" />
        {/* FSSAI-style veg/non-veg indicator */}
        <div className={`menu-item-type-badge ${item.isVeg == true ? 'veg' : 'nonveg'}`}>
          <span className="type-dot" />
        </div>
        {!item.isAvailable && (
          <div className="menu-item-unavailable-overlay">
            <span>Unavailable</span>
          </div>
        )}
      </div>

      {/* Card Body */}
      <div className="menu-item-body">
        <div className="menu-item-name-row">
          <h4 className="menu-item-name">{item.itemName}</h4>
          <span className={`menu-type-label ${item.isVeg == true ? 'veg' : 'nonveg'}`}>
            {item.isVeg == true ? <Leaf size={11} /> : <Flame size={11} />}
            {item.isVeg == true ? 'Veg' : 'Non-Veg'}
          </span>
        </div>
        {item.description && (
          <p className="menu-item-desc">{item.description}</p>
        )}
        <div className="menu-item-footer">
          <span className="menu-item-price">₹{item.price?.toFixed(0)}</span>
          <div onClick={e => e.stopPropagation()}>
            {qty === 0 ? (
              <button
                className="btn-add"
                onClick={(e) => { e.stopPropagation(); onAdd(item); }}
                disabled={!item.isAvailable}
              >
                <Plus size={14} /> Add
              </button>
            ) : (
              <div className="qty-control" style={{ gap: '6px' }}>
                <button className="qty-btn" style={{ width: '30px', height: '30px' }}
                  onClick={(e) => { e.stopPropagation(); onUpdateQty(menuId, qty - 1); }}>
                  <Minus size={12} />
                </button>
                <span className="qty-value" style={{ fontSize: '0.88rem' }}>{qty}</span>
                <button className="qty-btn" style={{ width: '30px', height: '30px' }}
                  onClick={(e) => { e.stopPropagation(); onUpdateQty(menuId, qty + 1); }}>
                  <Plus size={12} />
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

/* ── Cart Drawer ─────────────────────────────────── */
const CartDrawer = ({ isOpen, onClose, cartItems, restaurant, totalAmount, onUpdateQty, onCheckout }) => {
  // Group cartItems by restaurant name
  const groupedCartItems = cartItems.reduce((acc, item) => {
    const rName = item.restaurantName || 'Restaurant';
    if (!acc[rName]) {
      acc[rName] = [];
    }
    acc[rName].push(item);
    return acc;
  }, {});

  return (
    <>
      {isOpen && <div className="cart-overlay" onClick={onClose} />}
      <div className={`cart-drawer ${isOpen ? 'open' : ''}`}>
        <div className="cart-drawer-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <ShoppingCart size={20} color="var(--primary)" />
            <h3>Your Cart</h3>
          </div>
          <button className="cart-drawer-close" onClick={onClose}>
            <X size={16} />
          </button>
        </div>

        {restaurant && (
          <div className="cart-restaurant-tag">
            <MapPin size={13} /> Currently viewing: {restaurant.name}
          </div>
        )}

        <div className="cart-drawer-body">
          {cartItems.length === 0 ? (
            <div className="cart-empty">
              <ShoppingBag size={48} color="var(--text-muted)" />
              <p>Your cart is empty</p>
              <span>Add items from the menu</span>
            </div>
          ) : (
            <div className="cart-items-list">
              {Object.entries(groupedCartItems).map(([rName, items]) => (
                <div key={rName} className="cart-restaurant-group">
                  <div className="cart-restaurant-group-title">
                    <Store size={13} />
                    <span>{rName}</span>
                  </div>
                  {items.map(item => (
                    <div key={item.menuId} className="cart-item">
                      <div className="cart-item-info">
                        <span className="cart-item-name">{item.itemName}</span>
                        <span className="cart-item-unit">₹{item.price} × {item.quantity}</span>
                      </div>
                      <div className="cart-item-right">
                        <span className="cart-item-total">₹{(item.price * item.quantity).toFixed(0)}</span>
                        <div className="qty-control" style={{ gap: '4px' }}>
                          <button className="qty-btn" style={{ width: '26px', height: '26px' }}
                            onClick={() => onUpdateQty(item.menuId, item.quantity - 1)}>
                            <Minus size={11} />
                          </button>
                          <span className="qty-value" style={{ fontSize: '0.85rem' }}>{item.quantity}</span>
                          <button className="qty-btn" style={{ width: '26px', height: '26px' }}
                            onClick={() => onUpdateQty(item.menuId, item.quantity + 1)}>
                            <Plus size={11} />
                          </button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              ))}
            </div>
          )}
        </div>

        {cartItems.length > 0 && (
          <div className="cart-drawer-footer">
            <div className="cart-subtotal">
              <span>Subtotal</span>
              <span>₹{totalAmount.toFixed(0)}</span>
            </div>
            <div className="cart-delivery-fee">
              <span style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                <Truck size={13} /> Delivery Fee
              </span>
              <span>₹30</span>
            </div>
            <div className="divider" style={{ margin: '10px 0' }} />
            <div className="cart-total">
              <span>Total</span>
              <span className="cart-total-amount">₹{(totalAmount + 30).toFixed(0)}</span>
            </div>
            <button className="btn-primary" style={{ width: '100%', justifyContent: 'center' }} onClick={onCheckout}>
              Proceed to Checkout
            </button>
          </div>
        )}
      </div>
    </>
  );
};

/* ── Main Page ───────────────────────────────────── */
const RestaurantDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { cartItems, addItem, updateQuantity, totalAmount, totalItems } = useCartContext();
  const [restaurant, setRestaurant] = useState(null);
  const [menu, setMenu] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [cartOpen, setCartOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);
  const [vegFilter, setVegFilter] = useState('all'); // 'all' | 'veg' | 'nonveg'

  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await api.get(`/restaurants/${id}`);
        setRestaurant(res.data.restaurant || res.data);
        setMenu(res.data.menu || res.data.menuItems || []);
      } catch {
        setError('Failed to load restaurant details');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [id]);

  const handleAdd = (item) => {
    addItem({
      menuId: item.menuID || item.menuItemId || item.id,
      itemName: item.itemName,
      price: item.price,
      restaurantId: restaurant?.restaurantID || restaurant?.restaurantId || restaurant?.id || id,
      restaurantName: restaurant?.name,
      isVeg: item.isVeg,
    });
    toast.success(`${item.itemName} added to cart`);
    setCartOpen(true);
  };

  const filteredMenu = menu.filter(item => {
    const isVeg = item.isVeg == true; // handles both boolean true and integer 1
    if (vegFilter === 'veg') return isVeg;
    if (vegFilter === 'nonveg') return !isVeg;
    return true;
  });

  const vegItems = filteredMenu.filter(item => item.isVeg == true);
  const nonVegItems = filteredMenu.filter(item => item.isVeg != true);

  if (loading) return (
    <div className="page-wrapper">
      <Navbar />
      <div className="loading-screen">
        <div className="spinner" />
        <p>Loading menu…</p>
      </div>
    </div>
  );

  if (error) return (
    <div className="page-wrapper">
      <Navbar />
      <div className="container">
        <div className="glass-card error-state" style={{ margin: '40px 0' }}>
          <div className="error-icon"><Info size={40} /></div>
          <h3>Could not load</h3>
          <p>{error}</p>
        </div>
      </div>
    </div>
  );

  return (
    <div className="page-wrapper">
      <Navbar />

      {/* ── Restaurant Banner ── */}
      <div className="rest-banner">
        <div className="rest-banner-bg">
          <img
            src={restaurant?.imagePath || restaurant?.imageUrl || `https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=1400&h=400&fit=crop`}
            alt={restaurant?.name}
            onError={e => { e.target.src = 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=1400&h=400&fit=crop'; }}
          />
        </div>
        <div className="rest-banner-overlay" />
        <div className="container rest-banner-content">
          <button className="back-btn" onClick={() => navigate('/home')}>
            <ArrowLeft size={16} /> Back
          </button>
          <div className="rest-banner-info">
            <div className="rest-banner-badges">
              <span className="badge badge-orange">{restaurant?.cuisineType}</span>
              {restaurant?.isVeg && <span className="badge badge-green"><Leaf size={11} /> Pure Veg</span>}
            </div>
            <h1>{restaurant?.name}</h1>
            <div className="rest-banner-meta">
              <span><Star size={14} fill="currentColor" color="var(--warning)" /> {restaurant?.rating?.toFixed(1) || '4.5'}</span>
              <span className="meta-sep">·</span>
              <span><Clock size={13} /> 25–35 min</span>
              <span className="meta-sep">·</span>
              <span><MapPin size={13} /> {restaurant?.address || 'RR Nagar, Bengaluru'}</span>
            </div>
          </div>
        </div>
      </div>

      {/* ── Menu Section ── */}
      <div className="container rest-menu-section">
        <div className="rest-menu-header">
          <div>
            <h2>Menu <span className="gradient-text">({menu.length} items)</span></h2>
            {/* Veg / NonVeg filter */}
            <div className="veg-filter-bar">
              <button
                className={`veg-filter-btn all-btn ${vegFilter === 'all' ? 'active' : ''}`}
                onClick={() => setVegFilter('all')}
              >
                All Items
              </button>
              <button
                className={`veg-filter-btn veg-btn ${vegFilter === 'veg' ? 'active' : ''}`}
                onClick={() => setVegFilter('veg')}
              >
                <span className="filter-dot veg-dot" />
                <Leaf size={13} />
                Veg Only
              </button>
              <button
                className={`veg-filter-btn nonveg-btn ${vegFilter === 'nonveg' ? 'active' : ''}`}
                onClick={() => setVegFilter('nonveg')}
              >
                <span className="filter-dot nonveg-dot" />
                <Flame size={13} />
                Non-Veg
              </button>
            </div>
          </div>
          {totalItems > 0 && (
            <button className="view-cart-btn btn-primary" onClick={() => setCartOpen(true)}>
              <ShoppingCart size={16} /> Cart ({totalItems}) · ₹{totalAmount.toFixed(0)}
            </button>
          )}
        </div>

        {filteredMenu.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon"><ChefHat size={56} /></div>
            <h3>No items found</h3>
            <p>Try a different filter</p>
          </div>
        ) : (
          <div className="menu-categories">
            {vegItems.length > 0 && (
              <div className="menu-category-section" style={{ marginBottom: '40px' }}>
                <h3 className="menu-category-title" style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '1.25rem', fontWeight: 600, color: 'var(--success)', marginBottom: '18px' }}>
                  <Leaf size={18} fill="var(--success)" style={{ opacity: 0.85 }} />
                  <span>Vegetarian Dishes</span>
                  <span style={{ fontSize: '0.875rem', fontWeight: 400, color: 'var(--text-muted)' }}>({vegItems.length})</span>
                </h3>
                <div className="menu-grid">
                  {vegItems.map(item => (
                    <MenuItemCard
                      key={item.menuID || item.menuItemId || item.id}
                      item={item}
                      cartItems={cartItems}
                      onAdd={handleAdd}
                      onUpdateQty={updateQuantity}
                      onOpenModal={setSelectedItem}
                    />
                  ))}
                </div>
              </div>
            )}

            {nonVegItems.length > 0 && (
              <div className="menu-category-section">
                <h3 className="menu-category-title" style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '1.25rem', fontWeight: 600, color: 'var(--error)', marginBottom: '18px' }}>
                  <Flame size={18} fill="var(--error)" style={{ opacity: 0.85 }} />
                  <span>Non-Vegetarian Dishes</span>
                  <span style={{ fontSize: '0.875rem', fontWeight: 400, color: 'var(--text-muted)' }}>({nonVegItems.length})</span>
                </h3>
                <div className="menu-grid">
                  {nonVegItems.map(item => (
                    <MenuItemCard
                      key={item.menuID || item.menuItemId || item.id}
                      item={item}
                      cartItems={cartItems}
                      onAdd={handleAdd}
                      onUpdateQty={updateQuantity}
                      onOpenModal={setSelectedItem}
                    />
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </div>

      {/* ── Cart Drawer ── */}
      <CartDrawer
        isOpen={cartOpen}
        onClose={() => setCartOpen(false)}
        cartItems={cartItems}
        restaurant={restaurant}
        totalAmount={totalAmount}
        onUpdateQty={updateQuantity}
        onCheckout={() => { setCartOpen(false); navigate('/checkout'); }}
      />

      {/* ── Menu Item Detail Modal ── */}
      {selectedItem && (
        <MenuDetailModal
          item={selectedItem}
          cartItem={cartItems.find(c => c.menuId === (selectedItem.menuID || selectedItem.menuItemId || selectedItem.id))}
          onClose={() => setSelectedItem(null)}
          onAdd={handleAdd}
          onUpdateQty={updateQuantity}
        />
      )}
    </div>
  );
};

export default RestaurantDetailPage;
