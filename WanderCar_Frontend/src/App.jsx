import { useEffect } from 'react';
import { Routes, Route } from 'react-router-dom';
import { ToastProvider } from './context/ToastContext';
import { AuthProvider } from './context/AuthContext';
import { BookingProvider } from './context/BookingContext';
import Layout from './components/layout/Layout';
import HomePage from './pages/HomePage';
import HubSelectionPage from './pages/HubSelectionPage';
import VehicleSelectionPage from './pages/VehicleSelectionPage';
import AddOnsPage from './pages/AddOnsPage';
import CustomerInfoPage from './pages/CustomerInfoPage';
import ConfirmPage from './pages/ConfirmPage';
import BookedPage from './pages/BookedPage';
import ModifyCancelPage from './pages/ModifyCancelPage';
import StaffReturnPage from './pages/StaffReturnPage';
import StaffBookingsPage from './pages/StaffBookingsPage';
import StaffDashboardPage from './pages/StaffDashboardPage';
import CustomerCarePage from './pages/CustomerCarePage';
import ProfilePage from './pages/ProfilePage';
import { preloadReferenceData } from './api/locationService';
////sso////
import OAuthSuccess from "./pages/OAuthSuccess";


function App() {
  // Warms the states/cities/hubs/airports cache once, on first mount,
  // regardless of which route the user lands on first — this is what lets
  // getHubById/getCityById/getStateById/findHubForCity/findHubForAirport
  // stay plain synchronous functions everywhere they're already called
  // (ConfirmPage, ModifyCancelPage, BookingSearchCard) instead of every
  // one of those call sites needing to become async.
  useEffect(() => {
    preloadReferenceData();
  }, []);
  return (
    <ToastProvider>
      <AuthProvider>
        <BookingProvider>
          <Routes>
            <Route element={<Layout/>}>
              <Route path="/" element={<HomePage />} />
              <Route path="/hub-selection" element={<HubSelectionPage />} />
              <Route path="/vehicles" element={<VehicleSelectionPage />} />
              <Route path="/booking/add-ons" element={<AddOnsPage />} />
              <Route path="/booking/details" element={<CustomerInfoPage />} />
              <Route path="/booking/confirm" element={<ConfirmPage />} />
              <Route path="/booking/booked" element={<BookedPage />} />
              <Route path="/modify" element={<ModifyCancelPage />} />
              <Route path="/staff/return" element={<StaffReturnPage />} />
              <Route path="/staff/bookings" element={<StaffBookingsPage />} />
              <Route path="/staff/dashboard" element={<StaffDashboardPage />} />
              <Route path="/customer-care" element={<CustomerCarePage />} />
              <Route path="/profile" element={<ProfilePage />} />
              <Route path="/oauth-success" element={<OAuthSuccess />}/>
              <Route path="*" element={<HomePage />} />
            </Route>
          </Routes>
        </BookingProvider>
      </AuthProvider>
    </ToastProvider>
  );
}

export default App;
