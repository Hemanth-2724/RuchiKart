import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Eye, EyeOff, UserPlus, ShoppingBag, Store, Bike, User, Mail, Lock, MapPin } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';
import './AuthPages.css';

const ROLES = [
  { value: 'customer',          Icon: ShoppingBag, label: 'Customer',         desc: 'Order food from restaurants' },
  { value: 'restaurant_owner',  Icon: Store,        label: 'Restaurant Owner', desc: 'Manage your restaurant & menu' },
  { value: 'delivery_partner',  Icon: Bike,         label: 'Delivery Partner', desc: 'Deliver orders & earn' },
];

const ROLE_REDIRECTS = {
  customer: '/home',
  restaurant_owner: '/owner/dashboard',
  delivery_partner: '/delivery/dashboard',
  admin: '/admin/dashboard',
};

const RegisterPage = () => {
  const { user, register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ username: '', email: '', password: '', address: '', role: 'customer' });
  const [loading, setLoading] = useState(false);
  const [showPass, setShowPass] = useState(false);

  useEffect(() => {
    if (user) {
      navigate(ROLE_REDIRECTS[user.role] || '/home', { replace: true });
    }
  }, [user, navigate]);

  const handleChange = (e) => setForm(f => ({ ...f, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.username || !form.email || !form.password) { toast.error('Please fill in all required fields'); return; }
    if (form.password.length < 6) { toast.error('Password must be at least 6 characters'); return; }
    setLoading(true);
    try {
      await register(form);
      toast.success('Account created! Please sign in');
      navigate('/login', { replace: true });
    } catch (err) {
      toast.error(err?.response?.data?.message || 'Registration failed. Try a different username.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-bg-mesh" />
      <div className="auth-container register-layout">
        <div className="auth-card glass-card register-card">
          <div className="auth-nav-top">
            <button className="auth-back-btn" onClick={() => navigate('/')} title="Back to home">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                <path d="M19 12H5M12 5l-7 7 7 7"/>
              </svg>
              Back
            </button>
            <Link to="/" className="auth-logo">
              <span>Ruchi</span>
              <span className="auth-logo-kart">Kart</span>
            </Link>
          </div>

          <div className="auth-header">
            <h1>Create Account</h1>
            <p>Join RuchiKart and start your food journey</p>
          </div>

          <form onSubmit={handleSubmit} className="auth-form">
            <div className="form-row">
              <div className="form-group">
                <label>Username *</label>
                <div className="input-with-icon">
                  <User size={17} className="input-prefix-icon" />
                  <input type="text" name="username" className="input-field input-field-icon"
                    placeholder="cooluser123" value={form.username} onChange={handleChange} />
                </div>
              </div>
              <div className="form-group">
                <label>Email *</label>
                <div className="input-with-icon">
                  <Mail size={17} className="input-prefix-icon" />
                  <input type="email" name="email" className="input-field input-field-icon"
                    placeholder="you@example.com" value={form.email} onChange={handleChange} />
                </div>
              </div>
            </div>

            <div className="form-group">
              <label>Password *</label>
              <div className="input-with-icon">
                <Lock size={17} className="input-prefix-icon" />
                <input
                  type={showPass ? 'text' : 'password'} name="password"
                  className="input-field input-field-icon input-field-with-btn"
                  placeholder="Min 6 characters" value={form.password} onChange={handleChange}
                />
                <button type="button" className="input-icon-btn" onClick={() => setShowPass(!showPass)} tabIndex={-1}>
                  {showPass ? <EyeOff size={16} /> : <Eye size={16} />}
                </button>
              </div>
            </div>

            <div className="form-group">
              <label>Delivery Address</label>
              <div className="input-with-icon" style={{ alignItems: 'flex-start' }}>
                <MapPin size={17} className="input-prefix-icon" style={{ top: '16px', position: 'absolute' }} />
                <textarea name="address" className="input-field input-field-icon"
                  placeholder="Your home/office address in RR Nagar…"
                  value={form.address} onChange={handleChange}
                  rows={2} style={{ resize: 'none' }} />
              </div>
            </div>

            {/* Role Selector */}
            <div className="form-group">
              <label>I want to join as</label>
              <div className="role-selector">
                {ROLES.map(role => (
                  <label key={role.value} className={`role-card ${form.role === role.value ? 'selected' : ''}`}>
                    <input type="radio" name="role" value={role.value}
                      checked={form.role === role.value} onChange={handleChange} />
                    <div className="role-icon"><role.Icon size={22} /></div>
                    <span className="role-label">{role.label}</span>
                    <span className="role-desc">{role.desc}</span>
                  </label>
                ))}
              </div>
            </div>

            <button type="submit" className="btn-primary auth-submit" disabled={loading}>
              {loading
                ? <><div className="spinner-sm" /> Creating Account…</>
                : <><UserPlus size={17} /> Create Account</>}
            </button>
          </form>

          <div className="auth-footer-link">
            Already have an account?{' '}
            <Link to="/login">Sign in here</Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
