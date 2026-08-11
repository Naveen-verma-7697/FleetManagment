import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import * as staffService from '../api/staffService';
import { formatCurrency, formatDateTime } from '../utils/format';

const statusStyles = {
  CONFIRMED: 'bg-success/12 text-success',
  ONGOING: 'bg-primary/12 text-primary',
  COMPLETED: 'bg-ink/10 text-ink/60',
  CANCELLED: 'bg-danger/12 text-danger',
  PENDING: 'bg-warn/15 text-warn',
};



{/*function for search booking by confirmation number*/}
async function fetchBooking(no) {

  // Login mandatory
  if (no || confirmationNo) {
    if (!customer) {
      openAuth("login");
      return;
    }
  }

  const target = no ?? confirmationNo;

  setLoading(true);
  setError("");

  try {
    const result = await bookingService.getBookingByConfirmation(target);

    setBooking(result);
    setConfirmationNo(result.confirmationNo);
    //setSearched(true);

  } catch (err) {

    setBooking(null);
    setError(err.message || "Booking not found");

  } finally {
    setLoading(false);
  }
}

export default function StaffBookingsPage() {
  const { staff } = useAuth();
  const navigate = useNavigate();
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);

  const [search, setSearch] = useState("");//mine


  const { customer, openAuth }= useAuth(); 
  const [confirmationNo, setConfirmationNo] = useState(customer ? '' : 'WDR-2894');

  useEffect(() => {
    if (!staff) {
      navigate('/');
      return;
    }
    staffService.getAllBookings().then(setBookings).finally(() => setLoading(false));
  }, [staff, navigate]);

  if (!staff) return null;

  //changed added for search functionality
  const filteredBookings = bookings.filter((booking) =>
  booking.confirmationNo
    ?.toLowerCase()
    .includes(search.toLowerCase())
);

  return (
    <div className="anim-screen-fade mx-auto max-w-[1200px] px-6 pt-14 pb-28 md:px-8">

      {/*change added button*/}

<div className="max-w-130 rounded-[22px] border border-line bg-white/70 p-6.5 shadow-[0_6px_20px_rgba(18,21,28,0.05)]">
  <label className="field block">
    <span className="mb-1.5 block text-[11.5px] font-medium text-ink/50">
      Search Booking
    </span>

    <input
      type="text"
      placeholder="Search by Confirmation No (e.g. WDR-2895)"
      value={search}
      onChange={(e) => setSearch(e.target.value)}
      className="w-full rounded-[11px] border border-ink/10 bg-white/70 px-[11px] py-2.5 text-[13.5px] outline-none focus:border-steel"
    />
  </label>
</div>
{/*change added button yha tak*/}


      <div className="mb-9">
        <h1 className="mb-2 font-display text-[26px] font-semibold tracking-[-0.01em] md:text-[30px]">All bookings</h1>
        <p className="max-w-135 text-[15px] text-ink/55">Every reservation, with the renter and the vehicle assigned to it.</p>
      </div>

      {loading ? (
        <p className="text-sm text-ink/50">Loading bookings…</p>
      ) : (
        <div className="overflow-x-auto rounded-[20px] border border-line bg-white/70">
          <table className="w-full min-w-[820px] border-collapse text-[13.5px]">
            <thead>
              <tr className="border-b border-line text-left text-[11.5px] tracking-[0.05em] text-ink/45 uppercase">
                <th className="px-4 py-3 font-medium">Confirmation</th>
                <th className="px-4 py-3 font-medium">Customer</th>
                <th className="px-4 py-3 font-medium">Vehicle</th>
                <th className="px-4 py-3 font-medium">Pickup</th>
                <th className="px-4 py-3 font-medium">Return</th>
                <th className="px-4 py-3 font-medium">Total</th>
                <th className="px-4 py-3 font-medium">Status</th>
              </tr>
            </thead>
            <tbody>
              {/* {bookings.map((b) => ( */}
              {filteredBookings.map((b) => (
                <tr key={b.confirmationNo} className="border-b border-line/70 last:border-b-0">
                  <td className="px-4 py-3 font-mono text-ink">{b.confirmationNo}</td>
                  <td className="px-4 py-3">
                    <p className="font-medium text-ink">{b.customer?.fullName}</p>
                    <p className="text-[12px] text-ink/45">{b.customer?.email}</p>
                  </td>
                  <td className="px-4 py-3">
                    <p className="text-ink">
                      {b.carType?.carTypeName}{b.car ? ` · ${b.car.brand} ${b.car.model}` : ' · not assigned yet'}
                    </p>
                    <p className="font-mono text-[12px] text-ink/45">{b.car?.vehicleNumber}</p>
                  </td>
                  <td className="px-4 py-3 text-ink/70">{formatDateTime(b.pickupDatetime)}</td>
                  <td className="px-4 py-3 text-ink/70">{formatDateTime(b.returnDatetime)}</td>
                  <td className="px-4 py-3 font-medium text-ink">{formatCurrency(b.estimatedAmount)}</td>
                  <td className="px-4 py-3">
                    <span className={`rounded-full px-2.5 py-1 text-[11px] font-bold tracking-[0.05em] uppercase ${statusStyles[b.bookingStatus] || ''}`}>
                      {b.bookingStatus}
                    </span>
                  </td>
                </tr>
              ))}
              {/* {bookings.length === 0 && ( */}
              {filteredBookings.length === 0 && (
                <tr>
                  <td colSpan={7} className="px-4 py-6 text-center text-ink/45">No bookings yet.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
