import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './NotFound.css';

const NotFound = () => {
  const navigate = useNavigate();

  return (
    <div className="notfound-page">
      <div className="notfound-bg" />
      <div className="notfound-content">
        <div className="notfound-emoji">😕</div>
        <div className="notfound-404">
          <span className="gradient-text">404</span>
        </div>
        <h1 className="notfound-title">Page Not Found</h1>
        <p className="notfound-desc">
          Oops! The page you're looking for seems to have gone missing — maybe it got delivered to the wrong address? 🛵
        </p>
        <div className="notfound-actions">
          <button className="btn-primary" onClick={() => navigate(-1)}>
            ← Go Back
          </button>
          <Link to="/" className="btn-ghost">
            🏠 Home
          </Link>
        </div>
        <div className="notfound-food-items">
          {['🍕', '🍔', '🌮', '🍜', '🍛'].map((e, i) => (
            <span key={i} style={{ animationDelay: `${i * 0.3}s` }}>{e}</span>
          ))}
        </div>
      </div>
    </div>
  );
};

export default NotFound;
