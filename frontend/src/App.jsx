import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import PrivateRoute from './components/layout/PrivateRoute';

// Public Pages
import LandingPage from './pages/public/LandingPage';
import LoginPage from './pages/public/LoginPage';
import RegisterPage from './pages/public/RegisterPage';

// Customer Pages
import HomePage from './pages/customer/HomePage';
import RestaurantDetailPage from './pages/customer/RestaurantDetailPage';
import CheckoutPage from './pages/customer/CheckoutPage';
import OrdersPage from './pages/customer/OrdersPage';
import ProfilePage from './pages/customer/ProfilePage';

// Owner Pages
import OwnerDashboard from './pages/owner/OwnerDashboard';
import ManageMenuPage from './pages/owner/ManageMenuPage';
import OwnerOrdersPage from './pages/owner/OwnerOrdersPage';

// Delivery Pages
import DeliveryDashboard from './pages/delivery/DeliveryDashboard';
import ActiveDeliveriesPage from './pages/delivery/ActiveDeliveriesPage';

// Admin Pages
import AdminDashboard from './pages/admin/AdminDashboard';
import ManageUsersPage from './pages/admin/ManageUsersPage';
import ManageRestaurantsPage from './pages/admin/ManageRestaurantsPage';

// 404
import NotFound from './pages/NotFound';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <CartProvider>
          <Toaster
            position="top-right"
            toastOptions={{
              style: {
                background: 'rgba(15, 15, 28, 0.95)',
                color: '#fff',
                border: '1px solid rgba(255,255,255,0.1)',
                borderRadius: '12px',
                backdropFilter: 'blur(20px)',
                fontFamily: 'Inter, sans-serif',
                fontSize: '0.9rem',
              },
              success: {
                iconTheme: { primary: '#06D6A0', secondary: '#fff' },
              },
              error: {
                iconTheme: { primary: '#EF476F', secondary: '#fff' },
              },
            }}
          />
          <Routes>
            {/* Public Routes */}
            <Route path="/" element={<LandingPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            {/* Customer Routes */}
            <Route path="/home" element={
              <PrivateRoute allowedRoles={['customer']}>
                <HomePage />
              </PrivateRoute>
            } />
            <Route path="/restaurant/:id" element={
              <PrivateRoute allowedRoles={['customer']}>
                <RestaurantDetailPage />
              </PrivateRoute>
            } />
            <Route path="/checkout" element={
              <PrivateRoute allowedRoles={['customer']}>
                <CheckoutPage />
              </PrivateRoute>
            } />
            <Route path="/orders" element={
              <PrivateRoute allowedRoles={['customer']}>
                <OrdersPage />
              </PrivateRoute>
            } />
            <Route path="/profile" element={
              <PrivateRoute allowedRoles={['customer']}>
                <ProfilePage />
              </PrivateRoute>
            } />

            {/* Owner Routes */}
            <Route path="/owner/dashboard" element={
              <PrivateRoute allowedRoles={['restaurant_owner']}>
                <OwnerDashboard />
              </PrivateRoute>
            } />
            <Route path="/owner/menu" element={
              <PrivateRoute allowedRoles={['restaurant_owner']}>
                <ManageMenuPage />
              </PrivateRoute>
            } />
            <Route path="/owner/orders" element={
              <PrivateRoute allowedRoles={['restaurant_owner']}>
                <OwnerOrdersPage />
              </PrivateRoute>
            } />

            {/* Delivery Routes */}
            <Route path="/delivery/dashboard" element={
              <PrivateRoute allowedRoles={['delivery_partner']}>
                <DeliveryDashboard />
              </PrivateRoute>
            } />
            <Route path="/delivery/active" element={
              <PrivateRoute allowedRoles={['delivery_partner']}>
                <ActiveDeliveriesPage />
              </PrivateRoute>
            } />

            {/* Admin Routes */}
            <Route path="/admin/dashboard" element={
              <PrivateRoute allowedRoles={['admin']}>
                <AdminDashboard />
              </PrivateRoute>
            } />
            <Route path="/admin/users" element={
              <PrivateRoute allowedRoles={['admin']}>
                <ManageUsersPage />
              </PrivateRoute>
            } />
            <Route path="/admin/restaurants" element={
              <PrivateRoute allowedRoles={['admin']}>
                <ManageRestaurantsPage />
              </PrivateRoute>
            } />

            {/* 404 */}
            <Route path="*" element={<NotFound />} />
          </Routes>
        </CartProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
