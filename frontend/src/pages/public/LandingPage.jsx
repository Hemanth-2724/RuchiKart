import React, { useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { Zap, Star, CreditCard, MapPin, Clock, ChevronDown } from 'lucide-react';
import Footer from '../../components/layout/Footer';
import './LandingPage.css';

/* Food images with transparent-feel PNG-style circular crops */
const FOOD_FLOATERS = [
  { src: 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=140&h=140&fit=crop&crop=center', x: 8,  y: 18, size: 110, delay: 0,   dur: 7 },
  { src: 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=130&h=130&fit=crop&crop=center', x: 78, y: 12, size: 95,  delay: 1.2, dur: 8 },
  { src: 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=150&h=150&fit=crop&crop=center', x: 85, y: 55, size: 115, delay: 0.5, dur: 6.5 },
  { src: 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=120&h=120&fit=crop&crop=center',    x: 12, y: 62, size: 90,  delay: 2,   dur: 7.5 },
  { src: 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=130&h=130&fit=crop&crop=center', x: 48, y: 78, size: 100, delay: 1,   dur: 9 },
  { src: 'https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=120&h=120&fit=crop&crop=center', x: 65, y: 22, size: 80,  delay: 3,   dur: 8.5 },
];

const FEATURES = [
  {
    Icon: Zap,
    title: 'Lightning Fast Delivery',
    desc: 'Your favorite meals delivered hot in under 30 minutes. Real-time tracking keeps you informed every step.',
    color: 'rgba(255,209,102,0.12)',
    iconColor: '#FFD166',
    borderColor: 'rgba(255,209,102,0.2)',
    img: 'https://images.unsplash.com/photo-1526367790999-0150786686a2?w=80&h=80&fit=crop',
  },
  {
    Icon: Star,
    title: 'Premium Fresh Food',
    desc: 'Partnered with 20+ top restaurants. Only the freshest ingredients, quality guaranteed every order.',
    color: 'rgba(255,107,53,0.12)',
    iconColor: '#FF6B35',
    borderColor: 'rgba(255,107,53,0.22)',
    img: 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=80&h=80&fit=crop',
  },
  {
    Icon: CreditCard,
    title: 'Seamless Payments',
    desc: 'UPI, credit card, or cash on delivery. Flexible payment methods with 100% secure transactions.',
    color: 'rgba(6,214,160,0.12)',
    iconColor: '#06D6A0',
    borderColor: 'rgba(6,214,160,0.2)',
    img: 'https://images.unsplash.com/photo-1563245372-f21724e3856d?w=80&h=80&fit=crop',
  },
];

const STEPS = [
  { num: '01', title: 'Browse Restaurants', desc: 'Explore curated restaurants and filter by cuisine type.', img: 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=60&h=60&fit=crop' },
  { num: '02', title: 'Build Your Order',   desc: 'Add items to cart, customize quantities — simple and intuitive.', img: 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=60&h=60&fit=crop' },
  { num: '03', title: 'Quick Checkout',     desc: 'Choose your payment method and confirm delivery address.', img: 'https://images.unsplash.com/photo-1556742049-0cfed4f6a45d?w=60&h=60&fit=crop' },
  { num: '04', title: 'Enjoy & Repeat!',   desc: 'Track your order live and enjoy your delicious meal at home.', img: 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=60&h=60&fit=crop' },
];

const LandingPage = () => {
  const heroRef = useRef(null);

  useEffect(() => {
    const handleMouseMove = (e) => {
      if (!heroRef.current) return;
      const xPct = (e.clientX / window.innerWidth  - 0.5) * 18;
      const yPct = (e.clientY / window.innerHeight - 0.5) * 8;
      heroRef.current.style.setProperty('--mouse-x', `${xPct}px`);
      heroRef.current.style.setProperty('--mouse-y', `${yPct}px`);
    };
    window.addEventListener('mousemove', handleMouseMove);
    return () => window.removeEventListener('mousemove', handleMouseMove);
  }, []);

  return (
    <div className="landing-page">
      {/* ── Hero ── */}
      <section className="hero-section" ref={heroRef}>
        <div className="hero-bg-mesh" />
        <div className="hero-orb hero-orb-1" />
        <div className="hero-orb hero-orb-2" />
        <div className="hero-orb hero-orb-3" />

        {/* Floating food images */}
        {FOOD_FLOATERS.map((f, i) => (
          <div
            key={i}
            className="food-floater"
            style={{
              left: `${f.x}%`,
              top: `${f.y}%`,
              animationDelay: `${f.delay}s`,
              animationDuration: `${f.dur}s`,
            }}
          >
            <img
              src={f.src}
              alt="food"
              style={{ width: f.size, height: f.size }}
              onError={e => { e.target.style.display = 'none'; }}
            />
          </div>
        ))}

        <div className="container hero-content">
          <div className="hero-badge animate-fade-up">
            <MapPin size={14} />
            <span>Now delivering in RR Nagar, Bengaluru</span>
          </div>

          <h1 className="hero-heading animate-fade-up" style={{ animationDelay: '0.1s' }}>
            Craving<br />
            <span className="gradient-text">Something</span>{' '}
            <span className="hero-heading-sub">Delicious?</span>
          </h1>

          <p className="hero-sub animate-fade-up" style={{ animationDelay: '0.2s' }}>
            Order from 20+ handpicked restaurants. Fresh meals, fast delivery,<br />
            unforgettable flavors — all at your fingertips.
          </p>

          <div className="hero-ctas animate-fade-up" style={{ animationDelay: '0.3s' }}>
            <Link to="/register" className="btn-primary hero-cta-primary">
              Start Ordering
            </Link>
            <Link to="/login" className="btn-ghost hero-cta-ghost">
              Sign In
            </Link>
          </div>

          <div className="hero-stats animate-fade-up" style={{ animationDelay: '0.4s' }}>
            {[
              { val: '20+',   label: 'Restaurants' },
              { val: '< 30min', label: 'Avg Delivery' },
              { val: '1000+', label: 'Happy Customers' },
            ].map((s) => (
              <div key={s.label} className="hero-stat-item">
                <span className="hero-stat-val">{s.val}</span>
                <span className="hero-stat-label">{s.label}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Hero food showcase */}
        <div className="hero-food-showcase animate-fade-up" style={{ animationDelay: '0.5s' }}>
          <div className="food-showcase-ring" />
          <div className="food-showcase-center">
            <img
              src="https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=360&h=360&fit=crop"
              alt="Food showcase"
              onError={e => { e.target.src = 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=360&h=360&fit=crop'; }}
            />
          </div>
          <div className="food-showcase-mini food-mini-1">
            <img src="https://images.unsplash.com/photo-1513104890138-7c749659a591?w=80&h=80&fit=crop" alt="" />
          </div>
          <div className="food-showcase-mini food-mini-2">
            <img src="https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=80&h=80&fit=crop" alt="" />
          </div>
          <div className="food-showcase-mini food-mini-3">
            <img src="https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=80&h=80&fit=crop" alt="" />
          </div>
        </div>

        <div className="hero-scroll-hint">
          <span>Scroll to explore</span>
          <ChevronDown size={20} className="scroll-bounce" />
        </div>
      </section>

      {/* ── Features ── */}
      <section className="features-section">
        <div className="container">
          <div className="section-header">
            <span className="section-eyebrow">Why RuchiKart?</span>
            <h2 className="section-title">
              The <span className="gradient-text">Premium</span> Food Experience
            </h2>
            <p className="section-desc">
              We're not just delivering food — we're delivering joy, convenience, and quality.
            </p>
          </div>
          <div className="features-grid">
            {FEATURES.map((f, i) => (
              <div
                key={f.title}
                className="feature-card glass-card"
                style={{ animationDelay: `${i * 0.15}s` }}
              >
                <div className="feature-icon-wrap" style={{ background: f.color, border: `1px solid ${f.borderColor}` }}>
                  <f.Icon size={26} color={f.iconColor} strokeWidth={2} />
                </div>
                <h3 className="feature-title">{f.title}</h3>
                <p className="feature-desc">{f.desc}</p>
                <div className="feature-img-preview">
                  <img src={f.img} alt={f.title} onError={e => { e.target.style.display='none'; }} />
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── How It Works ── */}
      <section className="how-section">
        <div className="container">
          <div className="section-header">
            <span className="section-eyebrow">Simple Process</span>
            <h2 className="section-title">
              Order in <span className="gradient-text">4 Easy Steps</span>
            </h2>
          </div>
          <div className="steps-grid">
            {STEPS.map((step, i) => (
              <div key={step.num} className="step-card">
                <div className="step-number">{step.num}</div>
                {i < STEPS.length - 1 && <div className="step-connector" />}
                <div className="step-img">
                  <img src={step.img} alt={step.title} onError={e => { e.target.style.display='none'; }} />
                </div>
                <h4 className="step-title">{step.title}</h4>
                <p className="step-desc">{step.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── CTA Banner ── */}
      <section className="cta-section">
        <div className="container">
          <div className="cta-card">
            <div className="cta-bg-img">
              <img
                src="https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=1200&h=400&fit=crop"
                alt="restaurant"
              />
              <div className="cta-img-overlay" />
            </div>
            <div className="cta-content">
              <h2 className="cta-heading">Ready to Order?</h2>
              <p className="cta-sub">
                Join thousands of happy customers. First order? Get ₹50 off!
              </p>
              <div className="cta-buttons">
                <Link to="/register" className="btn-primary" style={{ fontSize: '1.05rem', padding: '14px 36px' }}>
                  Get Started Free
                </Link>
                <Link to="/login" className="btn-ghost" style={{ fontSize: '1.05rem', padding: '13px 34px' }}>
                  I Have an Account
                </Link>
              </div>
            </div>
            <div className="cta-food-strip">
              {[
                'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=100&h=100&fit=crop',
                'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=100&h=100&fit=crop',
                'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=100&h=100&fit=crop',
                'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=100&h=100&fit=crop',
                'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=100&h=100&fit=crop',
              ].map((src, i) => (
                <div key={i} className="cta-food-img">
                  <img src={src} alt="food" onError={e => { e.target.style.display='none'; }} />
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default LandingPage;
