import { useEffect, useRef, useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
//import { LogoIcon, UserIcon } from '../icons/Icons';
import { UserIcon } from '../icons/Icons';
import logo from '../../assets/images/wandercar-logo.png';
import { useAuth } from '../../context/AuthContext';
import Overlay from '../Overlay';

const navLinkClass = ({ isActive }) =>
  `inline-block rounded-full border px-3.5 py-2 transition-colors duration-200 ${
    isActive
      ? 'border-ink/10 bg-ink text-white font-semibold shadow-[0_4px_14px_rgba(18,21,28,0.18)]'
      : 'border-transparent hover:text-ink'
  }`;

export default function Navbar() {
  const { customer, staff, openAuth, staffLogout, openStaffAuth, requestLogout, logoutConfirmOpen, cancelLogout, logout } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);
  const menuRef = useRef(null);

  useEffect(() => {
    if (!menuOpen) return;
    function handleClick(e) {
      if (menuRef.current && !menuRef.current.contains(e.target)) setMenuOpen(false);
    }
    document.addEventListener('mousedown', handleClick);
    return () => document.removeEventListener('mousedown', handleClick);
  }, [menuOpen]);

  function handleHomeAnchor(hash) {
    navigate('/');
    requestAnimationFrame(() => {
      setTimeout(() => {
        document.getElementById(hash)?.scrollIntoView({ behavior: 'smooth' });
      }, 60);
    });
  }

  function handleStaffToggle() {
    if (staff) {
      staffLogout();
    } else {
      openStaffAuth();
    }
  }

  return (
    <nav className="sticky top-0 z-50 flex items-center justify-between bg-white/70 px-8 py-2.5 backdrop-blur-2xl backdrop-saturate-150">
      {/* <Link to="/" className="flex items-center gap-2 font-display text-lg font-semibold text-ink no-underline">
        <span className="grid h-8 w-8 place-items-center rounded-[9px] bg-steel text-white">
          <LogoIcon width="17" height="17" />
        </span>
        WanderCar
      </Link> */}

<Link
  to="/"
  className="group flex items-center gap-3 no-underline"
>
  <img
    src={logo}
    alt="WanderCar"
    className="h-[42px] w-[42px] object-contain transition-all duration-300 ease-in-out group-hover:-translate-y-1 group-hover:scale-110 group-hover:rotate-3"
  />

  <div className="flex flex-col leading-tight">
    <h1 className="font-display text-[22px] font-extrabold tracking-tight">
  <span className="text-slate-900">Wander</span>
  <span className="bg-gradient-to-r from-blue-500 to-blue-700 bg-clip-text text-transparent">
    Car
  </span>
</h1>
  </div>
</Link>

      <ul className="hidden list-none items-center gap-1.5 text-sm font-medium text-ink/65 md:flex">
        <li>
          <NavLink to="/" end className={navLinkClass}>
            Home
          </NavLink>
        </li>
        <li>
          <NavLink to="/modify" className={navLinkClass}>
            Modify Booking
          </NavLink>
        </li>
        <li>
          <button type="button" onClick={() => handleHomeAnchor('about')} className="inline-block rounded-full border border-transparent px-3.5 py-2 transition-colors hover:text-ink">
            About us
          </button>
        </li>
        <li>
          <NavLink to="/customer-care" className={navLinkClass}>
            Customer Care
          </NavLink>
        </li>
      </ul>

      <div className="flex items-center gap-3.5">
        <button
          type="button"
          onClick={handleStaffToggle}
          className="flex items-center gap-1.5 rounded-full bg-white/50 px-4.5 py-2.5 text-sm font-medium text-ink shadow-[0_1px_2px_rgba(18,21,28,0.06),inset_0_1px_0_rgba(255,255,255,0.8)] backdrop-blur-xl backdrop-saturate-150"
        >
          {staff ? 'Exit staff mode' : 'Staff login'}
        </button>

        {customer ? (
          <div className="relative" ref={menuRef}>
            <button
              type="button"
              title={customer.fullName}
              onClick={() => setMenuOpen((v) => !v)}
              aria-haspopup="true"
              aria-expanded={menuOpen}
              className="grid h-9 w-9 place-items-center rounded-full bg-white/50 text-ink shadow-[0_1px_2px_rgba(18,21,28,0.06),inset_0_1px_0_rgba(255,255,255,0.8)] backdrop-blur-xl backdrop-saturate-150"
            >
              <UserIcon width="16" height="16" />
            </button>
            {menuOpen && (
              <div className="absolute right-0 top-[calc(100%+8px)] w-48 overflow-hidden rounded-2xl border border-white/80 bg-white/95 py-1.5 text-sm shadow-[0_18px_40px_rgba(18,21,28,0.18)] backdrop-blur-2xl">
                <div className="border-b border-line px-4 py-2.5">
                  <p className="truncate font-semibold text-ink">{customer.fullName}</p>
                  <p className="truncate text-[12px] text-ink/50">{customer.email}</p>
                </div>
                <button
                  type="button"
                  onClick={() => {
                    setMenuOpen(false);
                    navigate('/profile');
                  }}
                  className="block w-full px-4 py-2.5 text-left text-ink/80 hover:bg-ink/5"
                >
                  Profile
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setMenuOpen(false);
                    requestLogout();
                  }}
                  className="block w-full px-4 py-2.5 text-left text-danger hover:bg-danger/5"
                >
                  Log out
                </button>
              </div>
            )}
          </div>
        ) : (
          <button
            type="button"
            onClick={() => openAuth('login')}
            className="flex items-center gap-1.5 rounded-full bg-primary px-5 py-2.5 text-sm font-semibold text-white shadow-[0_4px_14px_rgba(47,111,237,0.35)] transition-[filter] hover:brightness-95"
          >
            User login
          </button>
        )}
      </div>

      <Overlay open={logoutConfirmOpen} onClose={cancelLogout} maxWidth={340}>
        <h3 className="mb-2 text-center font-display text-[17px] font-semibold">Log out?</h3>
        <p className="mb-5 text-center text-[13.5px] text-ink/55">
          You'll need to log back in to see your bookings and account details.
        </p>
        <div className="flex gap-2.5">
          <button
            type="button"
            onClick={cancelLogout}
            className="flex-1 rounded-full border border-ink/12 bg-white py-2.5 text-[13.5px] font-medium text-ink hover:bg-ink/3"
          >
            Cancel
          </button>
          <button
            type="button"
            onClick={logout}
            className="flex-1 rounded-full bg-danger py-2.5 text-[13.5px] font-medium text-white hover:brightness-95"
          >
            Log out
          </button>
        </div>
      </Overlay>
    </nav>
  );
}