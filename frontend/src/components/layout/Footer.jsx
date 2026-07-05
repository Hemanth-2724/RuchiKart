import React from 'react';
import { Link } from 'react-router-dom';
import { MapPin, Phone, Mail, Clock, Heart, Share2, Globe } from 'lucide-react';
import './Footer.css';

const Footer = () => {
  return (
    <footer className="footer">
      <div className="footer-bg" />

      {/* Food strip */}
      <div className="footer-food-strip">
        {[
          'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=300&h=80&fit=crop',
          'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=300&h=80&fit=crop',
          'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=300&h=80&fit=crop',
          'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=300&h=80&fit=crop',
          'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=300&h=80&fit=crop',
          'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=300&h=80&fit=crop',
          'https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=300&h=80&fit=crop',
        ].map((src, i) => (
          <img key={i} src={src} alt="" onError={e => { e.target.style.display = 'none'; }} />
        ))}
      </div>

      <div className="container">
        <div className="footer-grid">
          {/* Brand */}
          <div className="footer-brand">
            <div className="footer-logo">
              <span className="logo-ruchi">Ruchi</span>
              <span className="logo-kart">Kart</span>
            </div>
            <p className="footer-tagline">
              Delivering happiness to your doorstep in RR Nagar, Bengaluru. Fresh food, fast delivery, great experience.
            </p>
            <div className="footer-socials">
              <button className="footer-social-btn" title="Like us"><Heart size={16} /></button>
              <button className="footer-social-btn" title="Share"><Share2 size={16} /></button>
              <button className="footer-social-btn" title="Website"><Globe size={16} /></button>
            </div>
          </div>

          <div className="footer-col">
            <h4>Quick Links</h4>
            <ul>
              <li><Link to="/">Home</Link></li>
              <li><Link to="/login">Login</Link></li>
              <li><Link to="/register">Register</Link></li>
            </ul>
          </div>

          <div className="footer-col">
            <h4>For Businesses</h4>
            <ul>
              <li><Link to="/register">Partner with us</Link></li>
              <li><Link to="/register">Delivery Partner</Link></li>
              <li><Link to="/register">Restaurant Owner</Link></li>
            </ul>
          </div>

          <div className="footer-col">
            <h4>Contact</h4>
            <ul>
              <li><MapPin size={13} /> RR Nagar, Bengaluru</li>
              <li><Phone size={13} /> +91 98765 43210</li>
              <li><Mail size={13} /> hello@ruchikart.in</li>
              <li><Clock size={13} /> 10 AM – 11 PM</li>
            </ul>
          </div>
        </div>

        <div className="footer-bottom">
          <div className="footer-divider" />
          <div className="footer-bottom-content">
            <p>© 2025 RuchiKart. All rights reserved. Made with love in Bengaluru.</p>
            <div className="footer-bottom-links">
              <span>Privacy Policy</span>
              <span>Terms of Service</span>
              <span>Refund Policy</span>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
