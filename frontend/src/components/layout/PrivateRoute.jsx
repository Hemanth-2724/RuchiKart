import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const ROLE_DASHBOARDS = {
  customer: '/home',
  restaurant_owner: '/owner/dashboard',
  delivery_partner: '/delivery/dashboard',
  admin: '/admin/dashboard',
};

const PrivateRoute = ({ children, allowedRoles }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="loading-screen" style={{ minHeight: '100vh' }}>
        <div className="spinner" />
        <p>Loading RuchiKart...</p>
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    const redirect = ROLE_DASHBOARDS[user.role] || '/login';
    return <Navigate to={redirect} replace />;
  }

  return children;
};

export default PrivateRoute;
