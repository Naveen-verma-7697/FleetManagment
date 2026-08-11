import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import Navbar from './Navbar';
import StaffBar from './StaffBar';
import AuthModal from './AuthModal';
import StaffLoginModal from './StaffLoginModal';
import HandoverModal from './HandoverModal';

export default function Layout() {
  const [handoverOpen, setHandoverOpen] = useState(false);

  return (
    <div className="min-h-screen bg-white">
      <Navbar />
      <StaffBar onHandover={() => setHandoverOpen(true)} />
      <Outlet />
      <AuthModal />
      <StaffLoginModal />
      <HandoverModal open={handoverOpen} onClose={() => setHandoverOpen(false)} />
    </div>
  );
}
