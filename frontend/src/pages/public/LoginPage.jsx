import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Eye, EyeOff, LogIn, Mail, Lock } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';
import './AuthPages.css';

const ROLE_REDIRECTS = {
  customer: '/home',
  restaurant_owner: '/owner/dashboard',
  delivery_partner: '/delivery/dashboard',
  admin: '/admin/dashboard',
};

const FOOD_IMAGES = [
  'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=220&h=220&fit=crop',
  'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=220&h=220&fit=crop',
  'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=220&h=220&fit=crop',
  'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=220&h=220&fit=crop',
  'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=220&h=220&fit=crop',
  'https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=220&h=220&fit=crop',
];

const LoginPage = () => {
  const { user, login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: '', password: '' });
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
    if (!form.email || !form.password) { toast.error('Please fill in all fields'); return; }
    setLoading(true);
    try {
      const loggedUser = await login(form.email, form.password);
      toast.success(`Welcome back, ${loggedUser.username || 'User'}!`);
      navigate(ROLE_REDIRECTS[loggedUser.role] || '/home', { replace: true });
    } catch (err) {
      toast.error(err?.response?.data?.message || 'Invalid email or password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-bg-mesh" />

      <div className="auth-container">
        <div className="auth-card glass-card">
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
            <h1>Welcome back</h1>
            <p>Sign in to continue your food journey</p>
          </div>

          <form onSubmit={handleSubmit} className="auth-form">
            <div className="form-group">
              <label>Email Address</label>
              <div className="input-with-icon">
                <Mail size={17} className="input-prefix-icon" />
                <input
                  type="email" name="email"
                  className="input-field input-field-icon"
                  placeholder="Enter your email"
                  value={form.email} onChange={handleChange}
                  autoComplete="email"
                />
              </div>
            </div>

            <div className="form-group">
              <label>Password</label>
              <div className="input-with-icon">
                <Lock size={17} className="input-prefix-icon" />
                <input
                  type={showPass ? 'text' : 'password'} name="password"
                  className="input-field input-field-icon input-field-with-btn"
                  placeholder="Enter your password"
                  value={form.password} onChange={handleChange}
                  autoComplete="current-password"
                />
                <button type="button" className="input-icon-btn" onClick={() => setShowPass(!showPass)} tabIndex={-1}>
                  {showPass ? <EyeOff size={16} /> : <Eye size={16} />}
                </button>
              </div>
            </div>

            <button type="submit" className="btn-primary auth-submit" disabled={loading}>
              {loading
                ? <><div className="spinner-sm" /> Signing In…</>
                : <><LogIn size={17} /> Sign In</>}
            </button>
          </form>

          <div className="auth-footer-link">
            Don't have an account?{' '}
            <Link to="/register">Create one free</Link>
          </div>


        </div>

        {/* Side art with food images */}
        <div className="auth-side-art">
          <div className="auth-art-circle auth-art-circle-1" />
          <div className="auth-art-circle auth-art-circle-2" />
          <div className="auth-food-grid">
            {FOOD_IMAGES.map((src, i) => (
              <div key={i} className="auth-food-img" style={{ animationDelay: `${i * 0.3}s` }}>
                <img src={src} alt="food" onError={e => { e.target.style.display='none'; }} />
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
