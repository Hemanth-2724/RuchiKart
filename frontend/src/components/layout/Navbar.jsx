import React, { useState, useRef, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import {
  Home, Package, User, LayoutDashboard, UtensilsCrossed,
  ClipboardList, Bike, Users, Store, ShoppingCart,
  ChevronDown, ChevronUp, Menu, X, LogOut
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { useCartContext } from '../../context/CartContext';
import toast from 'react-hot-toast';
import './Navbar.css';

const Navbar = () => {
  const { user, logout } = useAuth();
  const { totalItems } = useCartContext();
  const navigate = useNavigate();
  const location = useLocation();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    const handler = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, []);

  useEffect(() => { setMobileOpen(false); }, [location]);

  const handleLogout = async () => {
    try {
      await logout();
      toast.success('Logged out successfully');
      navigate('/', { replace: true });
    } catch {
      toast.error('Logout failed');
    }
  };

  const getNavLinks = () => {
    if (!user) return [];
    switch (user.role) {
      case 'customer':
        return [
          { to: '/home',    label: 'Home',      Icon: Home },
          { to: '/orders',  label: 'My Orders', Icon: Package },
          { to: '/profile', label: 'Profile',   Icon: User },
        ];
      case 'restaurant_owner':
        return [
          { to: '/owner/dashboard', label: 'Dashboard', Icon: LayoutDashboard },
          { to: '/owner/menu',      label: 'Menu',      Icon: UtensilsCrossed },
          { to: '/owner/orders',    label: 'Orders',    Icon: ClipboardList },
        ];
      case 'delivery_partner':
        return [
          { to: '/delivery/dashboard', label: 'Dashboard',     Icon: LayoutDashboard },
          { to: '/delivery/active',    label: 'Active Orders', Icon: Bike },
        ];
      case 'admin':
        return [
          { to: '/admin/dashboard',   label: 'Dashboard',   Icon: LayoutDashboard },
          { to: '/admin/users',       label: 'Users',       Icon: Users },
          { to: '/admin/restaurants', label: 'Restaurants', Icon: Store },
        ];
      default:
        return [];
    }
  };

  const navLinks = getNavLinks();
  const initials = user?.username?.charAt(0).toUpperCase() || '?';

  return (
    <nav className="navbar">
      <div className="container navbar-inner">
        <Link to={user ? (navLinks[0]?.to || '/') : '/'} className="navbar-logo">
          <span className="logo-ruchi">Ruchi</span>
          <span className="logo-kart">Kart</span>
        </Link>

        {/* Desktop Nav */}
        <div className="navbar-links">
          {navLinks.map(({ to, label, Icon }) => (
            <Link
              key={to}
              to={to}
              className={`nav-link ${location.pathname === to ? 'active' : ''}`}
            >
              <Icon size={16} strokeWidth={2} />
              {label}
            </Link>
          ))}
        </div>

        <div className="navbar-actions">
          {/* Cart Icon - only for customers */}
          {user?.role === 'customer' && (
            <Link to="/checkout" className="cart-btn">
              <ShoppingCart size={20} strokeWidth={2} />
              {totalItems > 0 && (
                <span className="cart-badge">{totalItems}</span>
              )}
            </Link>
          )}

          {/* User Section */}
          {user ? (
            <div className="user-dropdown" ref={dropdownRef}>
              <button
                className="avatar-btn"
                onClick={() => setDropdownOpen(!dropdownOpen)}
              >
                <span className="avatar-circle">{initials}</span>
                <span className="avatar-name">{user.username}</span>
                {dropdownOpen
                  ? <ChevronUp size={14} strokeWidth={2} style={{ color: 'rgba(255,255,255,0.5)' }} />
                  : <ChevronDown size={14} strokeWidth={2} style={{ color: 'rgba(255,255,255,0.5)' }} />
                }
              </button>
              {dropdownOpen && (
                <div className="dropdown-menu animate-scale-in">
                  <div className="dropdown-header">
                    <div className="dropdown-avatar">{initials}</div>
                    <div>
                      <div className="dropdown-username">{user.username}</div>
                      <div className="dropdown-role">{user.role?.replace('_', ' ')}</div>
                    </div>
                  </div>
                  <div className="dropdown-divider" />
                  {user.role === 'customer' && (
                    <Link to="/profile" className="dropdown-item" onClick={() => setDropdownOpen(false)}>
                      <User size={15} /> Profile
                    </Link>
                  )}
                  <button className="dropdown-item danger" onClick={handleLogout}>
                    <LogOut size={15} /> Logout
                  </button>
                </div>
              )}
            </div>
          ) : (
            <div className="auth-btns">
              <Link to="/login"    className="btn-ghost"   style={{ padding: '8px 20px', fontSize: '0.88rem' }}>Login</Link>
              <Link to="/register" className="btn-primary" style={{ padding: '9px 20px', fontSize: '0.88rem' }}>Register</Link>
            </div>
          )}

          {/* Mobile menu toggle */}
          <button className="mobile-toggle" onClick={() => setMobileOpen(!mobileOpen)}>
            {mobileOpen ? <X size={22} /> : <Menu size={22} />}
          </button>
        </div>
      </div>

      {/* Mobile Menu */}
      {mobileOpen && (
        <div className="mobile-menu">
          {navLinks.map(({ to, label, Icon }) => (
            <Link key={to} to={to} className="mobile-link">
              <Icon size={18} /> {label}
            </Link>
          ))}
          {user && (
            <button className="mobile-link danger-link" onClick={handleLogout}>
              <LogOut size={18} /> Logout
            </button>
          )}
        </div>
      )}
    </nav>
  );
};

export default Navbar;
