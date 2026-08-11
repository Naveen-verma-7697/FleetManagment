import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useBooking } from '../../context/BookingContext';

export default function StaffBar({ onHandover }) {
  const { staff } = useAuth();
  const { setStaffMode, resetDraft } = useBooking();
  const navigate = useNavigate();

  if (!staff) return null;

  function goOnSpotBooking() {
    resetDraft();
    setStaffMode(true);
    navigate('/');
  }

  function goCancellation() {
    navigate('/modify');
  }

  function goReturn() {
    navigate('/staff/return');
  }

  return (
    <div className="anim-staff-slide bg-ink text-white">
      <div className="mx-auto flex max-w-[1200px] flex-wrap items-center gap-2 px-8 py-2.5 text-[13px]">
        <b className="text-[12px] font-medium uppercase tracking-[0.08em] text-white/50">Staff tools</b>
        <button
          type="button"
          onClick={goOnSpotBooking}
          className="rounded-full bg-white/12 px-4 py-1.5 text-[13px] text-white transition-colors hover:bg-white/22"
        >
          On-spot booking
        </button>
        <button
          type="button"
          onClick={goCancellation}
          className="rounded-full bg-white/12 px-4 py-1.5 text-[13px] text-white transition-colors hover:bg-white/22"
        >
          Cancellation
        </button>
        <button
          type="button"
          onClick={onHandover}
          className="rounded-full bg-white/12 px-4 py-1.5 text-[13px] text-white transition-colors hover:bg-white/22"
        >
          Hand-over
        </button>
        <button
          type="button"
          onClick={goReturn}
          className="rounded-full bg-white/12 px-4 py-1.5 text-[13px] text-white transition-colors hover:bg-white/22"
        >
          Return
        </button>
        <button
          type="button"
          onClick={() => navigate('/staff/bookings')}
          className="rounded-full bg-white/12 px-4 py-1.5 text-[13px] text-white transition-colors hover:bg-white/22"
        >
          All bookings
        </button>
        <button
          type="button"
          onClick={() => navigate('/staff/dashboard')}
          className="rounded-full bg-white/12 px-4 py-1.5 text-[13px] text-white transition-colors hover:bg-white/22"
        >
          Dashboard
        </button>
        <span className="ml-auto font-mono text-[11.5px] text-white/50">
          {staff.username} · {staff.hub}
        </span>
      </div>
    </div>
  );
}
